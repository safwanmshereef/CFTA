package com.singularis.carbon.Utils;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.TrafficStats;
import android.os.IBinder;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

public class UsageTrackingService extends Service {

    private static final String TAG = UsageTrackingService.class.getSimpleName();
    private long callStartTime = 0;
    private long callDuration = 0;
    private long smsCount = 0;
    private long mobileDataUsage = 0;
    private long wifiDataUsage = 0;

    private BroadcastReceiver callReceiver;
    private BroadcastReceiver smsReceiver;
    private BroadcastReceiver dataReceiver;
    private static UsageTrackingCallBack callBack;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        registerReceivers();
        return START_STICKY;
    }

    private void registerReceivers() {
        // Call receiver
        callReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
                if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(state)) {
                    callStartTime = System.currentTimeMillis();
                } else if (TelephonyManager.EXTRA_STATE_IDLE.equals(state)) {
                    if (callStartTime != 0) {
                        callDuration = (System.currentTimeMillis() - callStartTime) / 1000; // Convert to seconds
                    }
                }
            }
        };
        registerReceiver(callReceiver, new IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED));

        // SMS receiver
        smsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Object[] pdus = (Object[]) intent.getExtras().get("pdus");
                if (pdus != null) {
                    smsCount += pdus.length;
                }
            }
        };
        registerReceiver(smsReceiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));

        // Data usage receiver
        dataReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long mobileRx = TrafficStats.getMobileRxBytes();
                long mobileTx = TrafficStats.getMobileTxBytes();
                long wifiRx = TrafficStats.getTotalRxBytes() - mobileRx;
                long wifiTx = TrafficStats.getTotalTxBytes() - mobileTx;

                mobileDataUsage = (mobileRx + mobileTx) / 1000; // KB
                wifiDataUsage = (wifiRx + wifiTx) / 1000; // KB
            }
        };
        registerReceiver(dataReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(callReceiver);
        unregisterReceiver(smsReceiver);
        unregisterReceiver(dataReceiver);
        sendUsageData();
        resetUsageData();
    }

    public static void stopService(Context context, UsageTrackingCallBack callback) {
        callBack = callback;
        context.stopService(new Intent(context, UsageTrackingService.class));
    }

    private void sendUsageData() {
        if (callBack != null) {
            callBack.onUsageDataReceived(callDuration, smsCount, mobileDataUsage, wifiDataUsage);
        }
    }

    private void resetUsageData() {
        callStartTime = 0;
        callDuration = 0;
        smsCount = 0;
        mobileDataUsage = 0;
        wifiDataUsage = 0;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
