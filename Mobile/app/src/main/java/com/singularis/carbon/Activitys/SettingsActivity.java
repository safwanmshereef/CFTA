package com.singularis.carbon.Activitys;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;

import com.singularis.carbon.Adapters.CarAdapter;
import com.singularis.carbon.Beans.Cars;
import com.singularis.carbon.Beans.FuelType;
import com.singularis.carbon.R;
import com.singularis.carbon.Utils.AddNewCarBottomSheetDialog;
import com.singularis.carbon.Utils.CommonSharedPreferences;
import com.singularis.carbon.Utils.CommonUtils;
import com.singularis.carbon.Utils.CreateRideBottomSheet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SettingsActivity extends AppCompatActivity {
    private ImageView iconHome,iconTrophy,iconTravel,iconGraph,iconSettings;
    private Button addCarButton, logoutButton,redeemButton,historyButton;
    private TextView username,usernameemail,userRank;
    private ArrayList<Cars> carList;
    private RecyclerView recyclerView;
    private String TAG = SettingsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        initUI();
        initConstants();
        
        
    }

    private void initConstants() {
        String authToken = CommonSharedPreferences.getString(SettingsActivity.this,"token",null);
        carList = new ArrayList<>();
        if (authToken != null) {
            // Perform GET request to fetch profile
            fetchUserProfile(authToken);
            fetchUserCars(authToken);
            fetchUserRanking(authToken);
        } else {
            // Show AlertDialog and navigate to LoginActivity
            showErrorDialog();
        }


        addCarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewCarBottomSheetDialog bottomSheetFragment = new AddNewCarBottomSheetDialog();
                bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
            }
        });
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonSharedPreferences.clearAll(SettingsActivity.this);
                Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });


        iconHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        iconTrophy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, RankingActivity.class);
                startActivity(intent);
            }
        });

        iconGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, GraphActivity.class);
                startActivity(intent);
            }
        });

        iconSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, SettingsActivity.class);
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
        redeemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    Log.e("LOG:",authToken);
                    // Create HTTP client
                    OkHttpClient client = new OkHttpClient();
                    int userId = CommonSharedPreferences.getInt(SettingsActivity.this, "user_id",0);
                    // Create request body
                    RequestBody formBody = new FormBody.Builder()
                            .add("user_id", String.valueOf(userId))
                            .build();
                    Log.e("TAG","user_id:"+userId);
                    Log.e("TAG","fetchUserRanking URL:"+CommonUtils.getApiUrl("/radeemPoints"));
                    // Create request
                    Request request = new Request.Builder()
                            .url(CommonUtils.getApiUrl("/radeemPoints"))
                            .post(formBody)
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
                                    new AlertDialog.Builder(SettingsActivity.this)
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
                                        jsonResponse = new JSONObject(responseData);
                                        Log.e("object", String.valueOf(jsonResponse));
                                        new AlertDialog.Builder(SettingsActivity.this)
                                                .setTitle("Redeem")
                                                .setMessage("Redeem Successfully Complete.")
                                                .setPositiveButton("OK", (dialog, which) -> {
                                                    dialog.dismiss();
                                                    // Reload the activity
                                                    Intent intent = getIntent();
                                                    finish();
                                                    startActivity(intent);
                                                })
                                                .show();

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
                                        new AlertDialog.Builder(SettingsActivity.this)
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
        });
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, MyTravelActivity.class);
                startActivity(intent);
            }
        });
    }
    private void fetchUserRanking(String authToken){
        try{
            Log.e("LOG:",authToken);
            // Create HTTP client
            OkHttpClient client = new OkHttpClient();

            Log.e("TAG","fetchUserRanking URL:"+CommonUtils.getApiUrl("/myranking"));
            // Create request
            Request request = new Request.Builder()
                    .url(CommonUtils.getApiUrl("/myranking"))
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
                            new AlertDialog.Builder(SettingsActivity.this)
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
                        Log.e(TAG,"fetchUserRanking"+ responseData);

                        // Handle success on UI thread if needed
                        runOnUiThread(() -> {
                            // Update UI or display profile data
                            JSONObject jsonResponse = null;
                            try {
                                jsonResponse = new JSONObject(responseData);
                                if (jsonResponse.has("data") && !jsonResponse.isNull("data")) {
                                    JSONObject object = jsonResponse.getJSONObject("data");
                                    Log.e("object", String.valueOf(object));
                                    userRank.setText("Rank: " + object.getString("rank"));
                                }else{}
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }

                        });
                    } else {
                        // Handle HTTP error codes
                        Log.e(TAG,"User Cars Error"+ "Response code: " + response.code());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new AlertDialog.Builder(SettingsActivity.this)
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
        userRank = findViewById(R.id.userRank);
        redeemButton = findViewById(R.id.redeemButton);
        addCarButton = findViewById(R.id.addCarButton);
        logoutButton = findViewById(R.id.logoutButton);
        recyclerView = findViewById(R.id.recyclerView);
        username = findViewById(R.id.username);
        usernameemail = findViewById(R.id.usernameemail);
        historyButton = findViewById(R.id.historyButton);

        iconHome = findViewById(R.id.iconHome);
        iconTrophy = findViewById(R.id.iconTrophy);
        iconTravel = findViewById(R.id.iconTravel);
        iconGraph = findViewById(R.id.iconGraph);
        iconSettings = findViewById(R.id.iconSettings);
    }

    private void showErrorDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Authentication Error")
                .setMessage("Authentication token not found. Please log in again.")
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> {
                    // Navigate to LoginActivity
                    Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish(); // Close the current activity
                })
                .show();
    }
    private void fetchUserProfile(String authToken) {
        Log.e("LOG:",authToken);
        // Create HTTP client
        OkHttpClient client = new OkHttpClient();

        Log.e("TAG","fetchUserProfile URL:"+CommonUtils.getApiUrl("/profile"));
        // Create request
        Request request = new Request.Builder()
                .url(CommonUtils.getApiUrl("/profile"))
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
                        new AlertDialog.Builder(SettingsActivity.this)
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

                    // Handle success on UI thread if needed
                    runOnUiThread(() -> {
                        // Update UI or display profile data
                        JSONObject jsonResponse = null;
                        try {
                            jsonResponse = new JSONObject(responseData);
                            username.setText(jsonResponse.getJSONObject("data").getString("name"));
                            usernameemail.setText(jsonResponse.getJSONObject("data").getString("email"));
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
                            new AlertDialog.Builder(SettingsActivity.this)
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
    private void fetchUserCars(String authToken) {
        Log.e("LOG:",authToken);
        // Create HTTP client
        OkHttpClient client = new OkHttpClient();

        Log.e("TAG","fetchUserCars URL:"+CommonUtils.getApiUrl("/get-cars"));
        // Create request
        Request request = new Request.Builder()
                .url(CommonUtils.getApiUrl("/get-cars"))
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
                        new AlertDialog.Builder(SettingsActivity.this)
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
                    Log.e(TAG,"fetchUserCars"+ responseData);

                    // Handle success on UI thread if needed
                    runOnUiThread(() -> {
                        // Update UI or display profile data
                        JSONObject jsonResponse = null;
                        try {
                            jsonResponse = new JSONObject(responseData);
                            JSONArray carsArray = jsonResponse.getJSONArray("data");
                            Log.e("length", String.valueOf(carsArray.length()));
                            // Iterate through each item in the 'data' array
                            for (int i = 0; i < carsArray.length(); i++) {
                                JSONObject carObject = carsArray.getJSONObject(i);
                                Log.e("carObject", String.valueOf(carObject));

                                // Extract individual fields
                                int carId = carObject.getInt("car_id");
                                int userId = carObject.getInt("user_id");
                                String make = carObject.getString("make");
                                String model = carObject.getString("model");
                                String engineSize = carObject.getString("engine_size");
                                int transmissionTypeId = carObject.getInt("transmission_type_id");
                                String transmissionTypeName = carObject.getString("transmission_type_name");
                                int fuelTypeId = carObject.getInt("fuel_type_id");
                                String fuelTypeName = carObject.getString("fuel_type_name");
                                String milage = carObject.getString("milage");
                                String co2 = carObject.getString("co2");

                                // Create a Car object and add it to the list
                                Cars car = new Cars(carId, userId, make, model, engineSize,
                                        transmissionTypeId, transmissionTypeName,
                                        fuelTypeId, fuelTypeName, milage, co2);
                                carList.add(car);
                            }
                            if (carList != null && !carList.isEmpty()) {
                                CarAdapter  carAdapter = new CarAdapter(carList);
                                recyclerView.setLayoutManager(new LinearLayoutManager(SettingsActivity.this));
                                recyclerView.setAdapter(carAdapter);
                            } else {
                                // Handle the empty list case or show a message
                            }
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
                            new AlertDialog.Builder(SettingsActivity.this)
                                    .setTitle("User Cars Error")
                                    .setMessage("Unexpected response: " + response.code())
                                    .setPositiveButton("OK", null)
                                    .show();
                        }
                    });
                }
            }
        });
    }

}