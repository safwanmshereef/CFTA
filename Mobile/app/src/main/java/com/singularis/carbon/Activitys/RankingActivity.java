package com.singularis.carbon.Activitys;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;

import com.singularis.carbon.Adapters.LeaderboardAdapter;
import com.singularis.carbon.Beans.User;
import com.singularis.carbon.R;
import com.singularis.carbon.Utils.CommonSharedPreferences;
import com.singularis.carbon.Utils.CommonUtils;
import com.singularis.carbon.Utils.CreateRideBottomSheet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RankingActivity extends AppCompatActivity {

    private ImageView iconHome,iconTrophy,iconTravel,iconGraph,iconSettings;
    private RecyclerView recyclerView;
    private LeaderboardAdapter adapter;
    private ArrayList<User> userList;
    private TextView first_person_txt,second_person_txt,third_person_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ranking);

        recyclerView = findViewById(R.id.recyclerView);

        // Sample data




        initUI();
        initConstants();
    }

    private void initUI() {

        iconHome = findViewById(R.id.iconHome);
        iconTrophy = findViewById(R.id.iconTrophy);
        iconTravel = findViewById(R.id.iconTravel);
        iconGraph = findViewById(R.id.iconGraph);
        iconSettings = findViewById(R.id.iconSettings);
        first_person_txt = findViewById(R.id.first_person_txt);
        second_person_txt = findViewById(R.id.second_person_txt);
        third_person_txt = findViewById(R.id.third_person_txt);
    }

    private void initConstants() {
        String authToken = CommonSharedPreferences.getString(RankingActivity.this,"token",null);
        if (authToken != null) {
            // Perform GET request to fetch profile
            fetchUserRanking(authToken);
        } else {
            // Show AlertDialog and navigate to LoginActivity
            showErrorDialog();
        }

        iconHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RankingActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        iconTrophy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RankingActivity.this, RankingActivity.class);
                startActivity(intent);
            }
        });

        iconGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RankingActivity.this, GraphActivity.class);
                startActivity(intent);
            }
        });

        iconSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RankingActivity.this, SettingsActivity.class);
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
    }
    private void showErrorDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Authentication Error")
                .setMessage("Authentication token not found. Please log in again.")
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> {
                    // Navigate to LoginActivity
                    Intent intent = new Intent(RankingActivity.this, LoginActivity.class);
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

            Log.e("TAG","fetchUserRanking URL:"+ CommonUtils.getApiUrl("/rankinglist"));
            // Create request
            Request request = new Request.Builder()
                    .url(CommonUtils.getApiUrl("/rankinglist"))
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
                            new AlertDialog.Builder(RankingActivity.this)
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
                            // Update UI or display profile data
                            JSONObject jsonResponse = null;
                            try {
                                userList = new ArrayList<>();
                                jsonResponse = new JSONObject(responseData);
                                JSONArray jsonArray = jsonResponse.getJSONArray("data");
                                if (jsonResponse.has("first") && !jsonResponse.isNull("first")) {
                                    JSONObject jsonFirst = jsonResponse.getJSONObject("first");
                                    first_person_txt.setText(jsonFirst.getString("name"));
                                }

                                if (jsonResponse.has("second") && !jsonResponse.isNull("second")) {
                                    JSONObject jsonSecond = jsonResponse.getJSONObject("second");
                                    second_person_txt.setText(jsonSecond.getString("name"));
                                }

                                if (jsonResponse.has("third") && !jsonResponse.isNull("third")) {
                                    JSONObject jsonThird = jsonResponse.getJSONObject("third");
                                    third_person_txt.setText(jsonThird.getString("name"));
                                }

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    userList.add(new User(object.getString("name"), object.getString("email"), object.getInt("rank")));
                                }
                                adapter = new LeaderboardAdapter(RankingActivity.this, userList);
                                recyclerView.setLayoutManager(new LinearLayoutManager(RankingActivity.this));
                                recyclerView.setAdapter(adapter);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }

                        });
                    } else {
                        // Handle HTTP error codes
                        Log.e("User Cars Error", "Response code: " + response.code());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new AlertDialog.Builder(RankingActivity.this)
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
}