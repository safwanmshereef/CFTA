package com.singularis.carbon.Activitys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.singularis.carbon.Beans.TravelData;
import com.singularis.carbon.Adapters.TravelDataAdapter;
import com.singularis.carbon.R;
import com.singularis.carbon.Utils.CommonSharedPreferences;
import com.singularis.carbon.Utils.CommonUtils;
import com.singularis.carbon.Utils.CreateRideBottomSheet;

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

public class MyTravelActivity extends AppCompatActivity {

    private String TAG = MyTravelActivity.class.getSimpleName();
    private Activity activity;
    private ImageView iconHome,iconTrophy,iconTravel,iconGraph,iconSettings;
    private RecyclerView recyclerView;
    private ArrayList<TravelData> travelList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_travel);
        
        initUi();
        initConstants();
    }

    private void initConstants() {
        try{
            activity = MyTravelActivity.this;
            String authToken = CommonSharedPreferences.getString(activity,"token",null);
            FOnPageLoad(authToken);
            iconHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, HomeActivity.class);
                    startActivity(intent);
                }
            });

            iconTrophy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, RankingActivity.class);
                    startActivity(intent);
                }
            });

            iconGraph.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, GraphActivity.class);
                    startActivity(intent);
                }
            });

            iconSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, SettingsActivity.class);
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
        }catch (Exception e){e.printStackTrace();}
    }

    private void FOnPageLoad(String authToken) {
        Log.e("LOG:",authToken);
        // Create HTTP client
        OkHttpClient client = new OkHttpClient();

        Log.e("TAG","fetchUserProfile URL:"+ CommonUtils.getApiUrl("/get-my-routes"));
        // Create request
        Request request = new Request.Builder()
                .url(CommonUtils.getApiUrl("/get-my-routes"))
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
                        new AlertDialog.Builder(activity)
                                .setTitle("Login Failed")
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
                    Log.e("ProfileSuccess", responseData);
                    travelList = new ArrayList<>();
                    // Handle success on UI thread if needed
                    runOnUiThread(() -> {
                        // Update UI or display profile data
                        JSONObject jsonResponse = null;
                        try {
                            jsonResponse = new JSONObject(responseData);
                            Log.e(TAG,"======Data:"+jsonResponse.toString());
                            JSONArray resp = jsonResponse.getJSONArray("data");
                            for (int i = 0; i < resp.length(); i++) {
                                JSONObject obj = resp.getJSONObject(i);
                                // Example data (Replace this with API response parsing)
                                travelList.add(new TravelData(
                                        obj.getInt("id"),
                                        obj.getString("user_id"),
                                        obj.getString("mot"),
                                        obj.isNull("vehicle_id") ? null : obj.getString("vehicle_id"),
                                        obj.isNull("make") ? null : obj.getString("make"),
                                        obj.isNull("model") ? null : obj.getString("model"),
                                        obj.isNull("engine_size") ? null : obj.getString("engine_size"),
                                        obj.getString("transport"),
                                        obj.getString("from_lat"),
                                        obj.getString("from_long"),
                                        obj.getString("to_lat"),
                                        obj.getString("to_long"),
                                        obj.getString("start_date_time"),
                                        obj.getString("end_date_time"),
                                        obj.getString("usage_id"),
                                        obj.getString("car_co2"),
                                        obj.getString("gprs"),
                                        obj.getString("sms"),
                                        obj.getString("phone"),
                                        obj.isNull("co2") ? null : obj.getString("co2"),
                                        obj.isNull("media_id") ? null : obj.getString("media_id"),
                                        obj.getString("status"),
                                        obj.getString("created_at"),
                                        obj.getString("updated_at")
                                ));
                            }
                            TravelDataAdapter adapter = new TravelDataAdapter(activity, travelList);
                            recyclerView.setLayoutManager(new LinearLayoutManager(activity));
                            recyclerView.setAdapter(adapter);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    });
                } else {
                    // Handle HTTP error codes
                    Log.e("ProfileError", "Response code: " + response.code());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(activity)
                                    .setTitle("Profile Error")
                                    .setMessage("Unexpected response: " + response.code())
                                    .setPositiveButton("OK", null)
                                    .show();
                        }
                    });
                }
            }
        });
    }

    private void initUi() {
        iconHome = findViewById(R.id.iconHome);
        iconTrophy = findViewById(R.id.iconTrophy);
        iconTravel = findViewById(R.id.iconTravel);
        iconGraph = findViewById(R.id.iconGraph);
        iconSettings = findViewById(R.id.iconSettings);
        recyclerView = findViewById(R.id.recyclerView);
    }
}