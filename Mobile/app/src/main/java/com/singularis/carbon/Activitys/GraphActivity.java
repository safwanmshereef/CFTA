package com.singularis.carbon.Activitys;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.singularis.carbon.Adapters.LeaderboardAdapter;
import com.singularis.carbon.Beans.User;
import com.singularis.carbon.R;
import com.singularis.carbon.Utils.CommonSharedPreferences;
import com.singularis.carbon.Utils.CommonUtils;

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

public class GraphActivity extends AppCompatActivity {
    private String TAG = GraphActivity.class.getSimpleName();
    private ImageView iconHome,iconTrophy,iconTravel,iconGraph,iconSettings;
    private LineChart lineChart;
    private ArrayList<Entry> entries;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_graph);
        initUI();
        initConstants();
    }

    private void initConstants() {
        entries = new ArrayList<>();
        iconHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GraphActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        iconTrophy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GraphActivity.this, RankingActivity.class);
                startActivity(intent);
            }
        });

        iconGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GraphActivity.this, GraphActivity.class);
                startActivity(intent);
            }
        });

        iconSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GraphActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        iconTravel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GraphActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        String authToken = CommonSharedPreferences.getString(GraphActivity.this,"token",null);
        if (authToken != null) {
            // Perform GET request to fetch profile
            fetchUserRanking(authToken);
        } else {
            // Show AlertDialog and navigate to LoginActivity
            showErrorDialog();
        }
    }

    private void showErrorDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Authentication Error")
                .setMessage("Authentication token not found. Please log in again.")
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> {
                    // Navigate to LoginActivity
                    Intent intent = new Intent(GraphActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish(); // Close the current activity
                })
                .show();
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
                            new AlertDialog.Builder(GraphActivity.this)
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
                                new AlertDialog.Builder(GraphActivity.this)
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
        lineChart = findViewById(R.id.lineChart);

        iconHome = findViewById(R.id.iconHome);
        iconTrophy = findViewById(R.id.iconTrophy);
        iconTravel = findViewById(R.id.iconTravel);
        iconGraph = findViewById(R.id.iconGraph);
        iconSettings = findViewById(R.id.iconSettings);
    }
}