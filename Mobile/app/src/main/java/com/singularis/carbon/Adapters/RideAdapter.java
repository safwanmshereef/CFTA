package com.singularis.carbon.Adapters;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.singularis.carbon.Activitys.HomeActivity;
import com.singularis.carbon.Beans.Ride;
import com.singularis.carbon.R;
import com.singularis.carbon.Utils.CommonSharedPreferences;
import com.singularis.carbon.Utils.CommonUtils;
import com.singularis.carbon.Utils.GPSTracker;
import com.singularis.carbon.Utils.Ride.RideMonitoringService;
import com.singularis.carbon.Utils.Ride.RidePreferences;
import com.singularis.carbon.Utils.Ride.RideUsageReceiver;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.*;

public class RideAdapter extends RecyclerView.Adapter<RideAdapter.RideViewHolder> {
    private String TAG = RideAdapter.class.getSimpleName();
    private Activity context;
    private List<Ride> rideList;
    private Location location = null;
    private LocationManager locationManager = null;
    private SharedPreferences currentLocation;
    private GPSTracker  gpsTracker;
    private RideUsageReceiver rideUsageReceiver;

    public RideAdapter(Activity context, List<Ride> rideList) {
        this.context = context;
        this.rideList = rideList;


//        currentLocation = context.getSharedPreferences("PREFS_NAME", 0);
//
//        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
//
//        Criteria locationCritera = new Criteria();
//        locationCritera.setAccuracy(Criteria.ACCURACY_FINE);
//        locationCritera.setAltitudeRequired(false);
//        locationCritera.setBearingRequired(false);
//        locationCritera.setCostAllowed(true);
//        locationCritera.setPowerRequirement(Criteria.NO_REQUIREMENT);
//
//        String providerName = locationManager.getBestProvider(locationCritera, true);
//
//        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//
//            return;
//        }
//
//        location = locationManager.getLastKnownLocation(providerName);
//        RideAdapter.MyLocationListener locationListener = new RideAdapter.MyLocationListener();
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        // Register BroadcastReceiver dynamically
        rideUsageReceiver = new RideUsageReceiver();
        IntentFilter filter = new IntentFilter("com.singularis.carbon.ACTION_USAGE_UPDATE");
        ContextCompat.registerReceiver(context, rideUsageReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED);
          gpsTracker = new GPSTracker(context);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        if (rideUsageReceiver != null) {
            context.unregisterReceiver(rideUsageReceiver);
        }
    }

    @Override
    public RideViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_vehicle_ride, parent, false);
        return new RideViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RideViewHolder holder, int position) {
        Ride ride = rideList.get(position);

        // Set the data to the views
        holder.startTimeTextView.setText("Start Time: " + ride.getStartDateTime());
        holder.carMakeTextView.setText("Car Make: " + ride.getMake());
        holder.carModelTextView.setText("Car Model: " + ride.getModel());
        holder.latitudeTextView.setText("Latitude: " + ride.getFromLat());
        holder.longitudeTextView.setText("Longitude: " + ride.getFromLong());

        if(RidePreferences.isRideActive(context)){
            RidePreferences.startRide(context, ride.getId());
        }
        // Handle the End Ride button click
        holder.endRideButton.setOnClickListener(v -> {
            Log.e("LOG:", "endRideButton:");
            endRide(ride.getId());
        });
    }

   /* public void endRide(String ride_id) {
        try {
//            if (!RidePreferences.isRideActive(context)) {
//                Toast.makeText(context, "No active ride!", Toast.LENGTH_SHORT).show();
//                return;
//            }

            context.stopService(new Intent(context, RideMonitoringService.class));


            JSONObject rideData = RidePreferences.getRideData(context);
//            new AlertDialog.Builder(context)
//                    .setTitle("Ride End")
//                    .setMessage("data: " + rideData.toString())
//                    .setPositiveButton("OK", null)
//                    .show();
            Log.e(TAG,"Latitude:"+String.valueOf(gpsTracker.getLatitude()));
            if(gpsTracker.getLatitude() !=  0.0 && gpsTracker.getLongitude() != 0.0) {
                sendPostRequest(context, ride_id, rideData.getString("sms_count"), rideData.getString("call_count"), rideData.getString("data_usage"));
            }else{
                gpsTracker.showSettingsAlert();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }*/
   public void endRide(String ride_id) {
       try {
           context.stopService(new Intent(context, RideMonitoringService.class));

           JSONObject rideData = RidePreferences.getRideData(context);

           Log.e(TAG, "Latitude: " + gpsTracker.getLatitude());

           if (gpsTracker.getLatitude() != 0.0 && gpsTracker.getLongitude() != 0.0) {
               sendPostRequest(context, ride_id, rideData.getString("sms_count"), rideData.getString("call_count"), rideData.getString("data_usage_time"));
           } else {
               gpsTracker.showSettingsAlert();
           }

           // **Broadcast Ride Usage Data**
           Intent usageIntent = new Intent("com.singularis.carbon.ACTION_USAGE_UPDATE");
           context.sendBroadcast(usageIntent);

       } catch (Exception e) {
           e.printStackTrace();
       }
   }

    public static String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }
    public void sendPostRequest(Activity context, String ride_id,String callDuration, String smsCount, String dataUsage) {
        OkHttpClient client = new OkHttpClient();
        String authToken = CommonSharedPreferences.getString(context, "token", null);
        RequestBody requestBody = new FormBody.Builder()
                .add("to_lat", String.valueOf(gpsTracker.getLatitude()))
                .add("to_long", String.valueOf(gpsTracker.getLongitude()))
                .add("end_date_time", getCurrentDateTime())
                .add("travel_id", ride_id)
                .add("phone", callDuration)
                .add("sms", smsCount)
                .add("gprs", dataUsage)
                .build();

        Request request = new Request.Builder()
                .url(CommonUtils.getApiUrl("/endroutes"))
                .post(requestBody)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Authorization", "Bearer " + authToken)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(context)
                                    .setTitle("Request Failed")
                                    .setMessage("Request failed: " + e.getMessage())
                                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                                    .show();
                        }});
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    Log.e("LOG:", "responseData:" + responseData);
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                    new AlertDialog.Builder(context)
                            .setTitle("Request Success")
                            .setMessage("Ride End: " + response.code())
                            .setPositiveButton("OK", (dialog, which) -> {
                                try {
                                    RidePreferences.endRide(context);
                                    // Restart HomeActivity
                                    Intent intent = new Intent(context, HomeActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    context.startActivity(intent);
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                                dialog.dismiss();
                            })
                            .show();
                        }});
                } else {
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                        new AlertDialog.Builder(context)
                                .setTitle("Request Failed")
                                .setMessage("Server error: " + response.code())
                                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                                .show();}});
                }
            }
        });
    }


    public String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    @Override
    public int getItemCount() {
        return rideList.size();
    }

    public static class RideViewHolder extends RecyclerView.ViewHolder {

        TextView startTimeTextView, carMakeTextView, carModelTextView, latitudeTextView, longitudeTextView;
        Button endRideButton;

        public RideViewHolder(View itemView) {
            super(itemView);
            startTimeTextView = itemView.findViewById(R.id.startTimeTextView);
            carMakeTextView = itemView.findViewById(R.id.carMakeTextView);
            carModelTextView = itemView.findViewById(R.id.carModelTextView);
            latitudeTextView = itemView.findViewById(R.id.latitudeTextView);
            longitudeTextView = itemView.findViewById(R.id.longitudeTextView);
            endRideButton = itemView.findViewById(R.id.endRideButton);
        }
    }

    public class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {

            loc.getLatitude();
            loc.getLongitude();

            Log.v("11111", "Latitude :" + loc.getLatitude());
            Log.v("22222", "Longitude :" + loc.getLongitude());

        }
    }

    public String getCurrentLatitude() {
        try {
            if (!currentLocation.getString("currentLatitude", "").equalsIgnoreCase(""))
                return currentLocation.getString("currentLatitude", "");
            else if (location.getLatitude() != 0.0) return Double.toString(location.getLatitude());
            else return "0.0";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0.0";
    }

    public String getCurrentLongitude() {
        try {
            if (!currentLocation.getString("currentLongitude", "").equalsIgnoreCase(""))
                return currentLocation.getString("currentLongitude", "");
            else if (location.getLongitude() != 0.0)
                return Double.toString(location.getLongitude());
            else return "0.0";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0.0";
    }
}

