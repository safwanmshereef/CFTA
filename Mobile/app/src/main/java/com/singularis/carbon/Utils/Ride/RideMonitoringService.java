package com.singularis.carbon.Utils.Ride;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.TrafficStats;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.singularis.carbon.R;

public class RideMonitoringService extends Service {
    private boolean isMonitoring = false;
    private long initialRx, initialTx;
    private SmsReceiver smsReceiver;  // Store reference
    private CallReceiver callReceiver;  // Store reference

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("ForegroundServiceType")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            if (!isMonitoring) {
                isMonitoring = true;
                startForeground(1, createNotification(this));
                startMonitoring();
            }
        }catch (Exception e){e.printStackTrace();}
        return START_STICKY;
    }

    private void startMonitoring() {
        registerReceivers();
        monitorInternetUsage();
    }

    private void registerReceivers() {
        smsReceiver = new SmsReceiver();
        callReceiver = new CallReceiver();

        registerReceiver(smsReceiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
        registerReceiver(callReceiver, new IntentFilter("android.intent.action.PHONE_STATE"));
    }

//    private void monitorInternetUsage() {
//        initialRx = TrafficStats.getTotalRxBytes(); // Track both Wi-Fi + Mobile
//        initialTx = TrafficStats.getTotalTxBytes(); // Track both Wi-Fi + Mobile
//
//        new Thread(() -> {
//            while (isMonitoring) {
//                long rx = TrafficStats.getTotalRxBytes() - initialRx; // Include Wi-Fi
//                long tx = TrafficStats.getTotalTxBytes() - initialTx; // Include Wi-Fi
//                RidePreferences.updateDataUsage(this, rx + tx);
//                try {
//                    Thread.sleep(5000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//    }
private void monitorInternetUsage() {
    initialRx = TrafficStats.getTotalRxBytes();
    initialTx = TrafficStats.getTotalTxBytes();

    new Thread(() -> {
        while (isMonitoring) {
            long rx = TrafficStats.getTotalRxBytes() - initialRx;
            long tx = TrafficStats.getTotalTxBytes() - initialTx;
            long totalDataUsed = rx + tx;

            RidePreferences.updateDataUsage(this, totalDataUsed);

            // Convert bytes to estimated minutes
            double estimatedMinutes = convertBytesToMinutes(this, totalDataUsed);
            Log.d("DataUsage", "Estimated Usage Time: " + estimatedMinutes + " minutes");
            RidePreferences.updateUsageTime(this, estimatedMinutes);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }).start();
}

    public static double convertBytesToMinutes(Context context, long totalBytes) {
        double speedBytesPerSecond = getNetworkSpeed(context); // Get estimated network speed
        if (speedBytesPerSecond <= 0) return 0; // Avoid division by zero

        double timeSeconds = totalBytes / speedBytesPerSecond;
        return timeSeconds / 60; // Convert seconds to minutes
    }

    public static double getNetworkSpeed(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            Network activeNetwork = cm.getActiveNetwork();
            if (activeNetwork != null) {
                NetworkCapabilities nc = cm.getNetworkCapabilities(activeNetwork);
                if (nc != null) {
                    // Get estimated bandwidth in bits per second
                    int downloadSpeedBps = nc.getLinkDownstreamBandwidthKbps() * 1000; // Convert Kbps to bps
                    return downloadSpeedBps / 8.0; // Convert bps to Bytes per second
                }
            }
        }
        return 0; // Return 0 if unable to determine speed
    }

    @Override
    public void onDestroy() {
        isMonitoring = false;

        // Retrieve stored data
        long totalDataUsed = RidePreferences.getDataUsage(this);
        int smsCount = RidePreferences.getSmsCount(this);
        int callCount = RidePreferences.getCallCount(this);

        // Send data back to HomeActivity or another component
        Intent broadcastIntent = new Intent("com.singularis.carbon.RIDE_COMPLETED");
        broadcastIntent.putExtra("totalDataUsed", totalDataUsed);
        broadcastIntent.putExtra("smsCount", smsCount);
        broadcastIntent.putExtra("callCount", callCount);
        sendBroadcast(broadcastIntent);

        // Unregister only if they were registered
        try {
            if (smsReceiver != null) {
                unregisterReceiver(smsReceiver);
            }
            if (callReceiver != null) {
                unregisterReceiver(callReceiver);
            }
        } catch (Exception e) {
            e.printStackTrace(); // Avoid crashing if they weren't registered
        }

        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

//    @RequiresApi(api = Build.VERSION_CODES.O)
//    private Notification createNotification() {
//        try {
//            NotificationChannel channel = new NotificationChannel("ride_channel", "Ride Monitoring", NotificationManager.IMPORTANCE_LOW);
//            getSystemService(NotificationManager.class).createNotificationChannel(channel);
//            return new NotificationCompat.Builder(this, "ride_channel")
//                    .setContentTitle("Ride Monitoring Active")
//                    .setSmallIcon(R.mipmap.ic_launcher)
//                    .build();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    private Notification createNotification(Context context) {
        String channelId = "ride_channel";
        String channelName = "Ride Monitoring";

        // Ensure Notification Manager is available
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create Notification Channel for Android 8+ (API 26+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && notificationManager != null) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_LOW
            );
            notificationManager.createNotificationChannel(channel);
        }

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setContentTitle("Ride Monitoring Active")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_LOW) // For pre-Oreo devices
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setOngoing(true); // Keeps the notification persistent

        // Apply foreground service behavior for Android 15+ (API 34)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // Android 15+
            builder.setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE);
        }

        return builder.build();
    }

}

