package com.singularis.carbon.Activitys;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.singularis.carbon.Adapters.RideAdapter;
import com.singularis.carbon.Beans.Ride;
import com.singularis.carbon.R;
import com.singularis.carbon.Utils.CommonSharedPreferences;
import com.singularis.carbon.Utils.CommonUtils;
import com.singularis.carbon.Utils.CreateRideBottomSheet;
import com.singularis.carbon.Utils.GPSTracker;
import com.singularis.carbon.Utils.Ride.RidePreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@RequiresApi(api = Build.VERSION_CODES.P)
public class HomeActivity extends AppCompatActivity {
    private ImageView iconHome,iconTrophy,iconTravel,iconGraph,iconSettings;
    private LineChart lineChart;
    private RecyclerView activeRoutes;
    private String authToken;
    private RideAdapter rideAdapter;
    private ArrayList<Ride> rideList;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private TextView tvLocation;
    private  final String TAG = HomeActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        initUI();
        try {
            initConstants();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }


    private void initConstants() throws JSONException {
        authToken = CommonSharedPreferences.getString(HomeActivity.this,"token",null);
        fetchUserRanking(authToken);

        // Request permissions before starting the service
        if (checkPermissions()) {
            //startService(new Intent(HomeActivity.this, UsageTrackingService.class));
        } else {
            requestPermissions();
        }

//        stopService(new Intent(MainActivity.this, UsageTrackingService.class));

        rideList = new ArrayList<>();
        iconHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        iconTrophy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, RankingActivity.class);
                startActivity(intent);
            }
        });

        iconGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, GraphActivity.class);
                startActivity(intent);
            }
        });

        iconSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        iconTravel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateRideBottomSheet bottomSheetFragment = new CreateRideBottomSheet();
                bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
            }
        });
        GPSTracker gpsTracker= new GPSTracker(HomeActivity.this);
        if(gpsTracker.getIsGPSTrackingEnabled()){
            tvLocation.setText(gpsTracker.getLocality(HomeActivity.this));
        }
//        RidePreferences.endRide(HomeActivity.this);
        setUpActiveRoutes();
    }

    // Check if we have the necessary permissions
    private boolean checkPermissions() {
//        int phoneStatePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
//        int sendSmsPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
//        int receiveSmsPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);
//        int accessNetworkStatePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE);
//        int accessWifiStatePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE);
//
//        return phoneStatePermission == PackageManager.PERMISSION_GRANTED &&
//                sendSmsPermission == PackageManager.PERMISSION_GRANTED &&
//                receiveSmsPermission == PackageManager.PERMISSION_GRANTED &&
//                accessNetworkStatePermission == PackageManager.PERMISSION_GRANTED &&
//                accessWifiStatePermission == PackageManager.PERMISSION_GRANTED;

        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.PROCESS_OUTGOING_CALLS) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED;



    }

    // Request permissions if not granted
    @RequiresApi(api = Build.VERSION_CODES.P)
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.SEND_SMS,
                        Manifest.permission.RECEIVE_SMS,
                        Manifest.permission.READ_CALL_LOG,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.READ_SMS,
                        Manifest.permission.PROCESS_OUTGOING_CALLS,
                        Manifest.permission.PACKAGE_USAGE_STATS,
                        Manifest.permission.FOREGROUND_SERVICE,
                },
                PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (allPermissionsGranted(grantResults)) {
            } else {
                Toast.makeText(this, "Permissions are required to track usage", Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean allPermissionsGranted(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public void setUpActiveRoutes() {
        OkHttpClient client = new OkHttpClient();

        // Set up the request
        Request request = new Request.Builder()
                .url(CommonUtils.getApiUrl("/activeroutes"))
                .get()
                .addHeader("Authorization", "Bearer " + authToken)
                .build();

        // Execute the request asynchronously
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Show failure dialog on the main thread
                runOnUiThread(() -> showFailureDialog(e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (response.isSuccessful()) {
                        // Handle successful response
                        String responseData = response.body().string();
                        JSONObject jsonResponse = new JSONObject(responseData);
                        Log.e("TAG","jsonResponse:"+jsonResponse);
                        JSONArray jsonArray = jsonResponse.getJSONArray("data");

                        // Process the response data
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            rideList.add(new Ride(
                                    object.getString("id"),
                                    object.getString("start_date_time"),
                                    object.getString("make"),
                                    object.getString("model"),
                                    object.getString("from_lat"),
                                    object.getString("from_long")
                            ));
                        }

                        // Update the RecyclerView on the main thread
                        runOnUiThread(() -> {
                            // Set up RecyclerView
                            rideAdapter = new RideAdapter(HomeActivity.this, rideList);
                            activeRoutes.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
                            activeRoutes.setAdapter(rideAdapter);
                        });

                    } else {
                        // Handle unsuccessful response on the main thread
                        runOnUiThread(() -> showFailureDialog("Unexpected response: " + response.code()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // Handle error on the main thread
                    runOnUiThread(() -> showFailureDialog("Error processing response."));
                }
            }
        });
    }


    // Method to display failure dialog
    private  void showFailureDialog(String message) {
        // Run on the UI thread to show an AlertDialog
        runOnUiThread(() -> {
            new AlertDialog.Builder(HomeActivity.this)
                    .setTitle("Request Failed")
                    .setMessage(message)
                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                    .show();
        });
    }
    private void setupGraph() {
        try{

            // Prepare data entries for the graph
            ArrayList<Entry> entries = new ArrayList<>();
            entries.add(new Entry(0, 17));
            entries.add(new Entry(1, 18));
            entries.add(new Entry(2, 17.5f));
            entries.add(new Entry(3, 19));
            entries.add(new Entry(4, 18.5f));

            // Create LineDataSet
            LineDataSet dataSet = new LineDataSet(entries, "CO2 Levels");
            dataSet.setColor(Color.WHITE);
            dataSet.setValueTextColor(Color.WHITE);
            dataSet.setCircleColor(Color.WHITE);
            dataSet.setLineWidth(2f);
            dataSet.setCircleRadius(4f);

            // Create LineData
            LineData lineData = new LineData(dataSet);

            // Customize chart
            lineChart.setData(lineData);
            lineChart.setBackgroundColor(Color.parseColor("#88C3A6"));
            lineChart.getXAxis().setTextColor(Color.WHITE);
            lineChart.getAxisLeft().setTextColor(Color.WHITE);
            lineChart.getAxisRight().setEnabled(false);
            lineChart.getLegend().setTextColor(Color.WHITE);
            Description desc = new Description();
            desc.setText("");
            lineChart.setDescription(desc);

            // Refresh the chart
            lineChart.invalidate();
        }catch (Exception e){e.printStackTrace();}
    }


    private void fetchUserRanking(String authToken){
        try{
            Log.e("LOG:",authToken);
            // Create HTTP client
            OkHttpClient client = new OkHttpClient();

            Log.e("TAG","fetchUserRanking URL:"+ CommonUtils.getApiUrl("/monthly-co2"));
            // Create request
            Request request = new Request.Builder()
                    .url(CommonUtils.getApiUrl("/monthly-co2"))
                    .get()
                    .addHeader("Authorization", "Bearer " + authToken)
                    .build();

            // Execute request
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.e("LoginError", "Request failed: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Create and display the AlertDialog
                            new AlertDialog.Builder(HomeActivity.this)
                                    .setTitle("Request Failed")
                                    .setMessage("Request failed: " + e.getMessage())
                                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                                    .show();
                        }
                    });
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        // Parse and log the response
                        String responseData = response.body().string();
                        Log.e("fetchUserRanking", responseData);

                        // Handle success on UI thread if needed
                        runOnUiThread(() -> {
                            try {
                                JSONObject jsonResponse = new JSONObject(responseData);
                                Log.e(TAG, "===========jsonResponse:" + jsonResponse);
                                JSONArray jsonArray = jsonResponse.getJSONArray("data");

                                ArrayList<Entry> entries = new ArrayList<>();
                                final ArrayList<String> labels = new ArrayList<>(); // Store date labels

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String date = object.getString("date");
                                    float co2Value = (float) object.getDouble("co2");

                                    entries.add(new Entry(i, co2Value)); // Use index for X value
                                    labels.add(date); // Store actual date
                                }

                                // Create LineDataSet
                                LineDataSet dataSet = new LineDataSet(entries, "CO2 Levels");
                                dataSet.setColor(Color.WHITE);
                                dataSet.setValueTextColor(Color.WHITE);
                                dataSet.setCircleColor(Color.WHITE);
                                dataSet.setLineWidth(2f);
                                dataSet.setCircleRadius(4f);

                                // Create LineData
                                LineData lineData = new LineData(dataSet);
                                lineChart.setData(lineData);

                                // Customize chart appearance
                                lineChart.setBackgroundColor(Color.parseColor("#88C3A6"));
                                lineChart.getXAxis().setTextColor(Color.WHITE);
                                lineChart.getAxisLeft().setTextColor(Color.WHITE);
                                lineChart.getAxisRight().setEnabled(false);
                                lineChart.getLegend().setTextColor(Color.WHITE);

                                // Set X-Axis labels correctly
                                XAxis xAxis = lineChart.getXAxis();
                                xAxis.setValueFormatter(new IndexAxisValueFormatter(labels)); // Set date labels
                                xAxis.setGranularity(1f);
                                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                                xAxis.setLabelRotationAngle(45);
                                xAxis.setDrawGridLines(false);

                                // Hide chart description
                                Description desc = new Description();
                                desc.setText("");
                                lineChart.setDescription(desc);

                                // Refresh the chart
                                lineChart.invalidate();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        });

                    } else {
                        // Handle HTTP error codes
                        Log.e("User Cars Error", "Response code: " + response.code());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new AlertDialog.Builder(HomeActivity.this)
                                        .setTitle("User Cars Error")
                                        .setMessage("Unexpected response: " + response.code())
                                        .setPositiveButton("OK", null)
                                        .show();
                            }
                        });
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initUI() {
        tvLocation = findViewById(R.id.tvLocation);
        iconHome = findViewById(R.id.iconHome);
        iconTrophy = findViewById(R.id.iconTrophy);
        iconTravel = findViewById(R.id.iconTravel);
        iconGraph = findViewById(R.id.iconGraph);
        iconSettings = findViewById(R.id.iconSettings);
        lineChart = findViewById(R.id.lineChart);
        activeRoutes = findViewById(R.id.activeRoutes);
    }


}