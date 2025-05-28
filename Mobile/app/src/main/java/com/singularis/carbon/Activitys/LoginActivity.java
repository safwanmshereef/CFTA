package com.singularis.carbon.Activitys;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.singularis.carbon.R;
import com.singularis.carbon.Utils.CommonSharedPreferences;
import com.singularis.carbon.Utils.CommonUtils;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Call;
import okhttp3.Callback;
import java.io.IOException;


public class LoginActivity extends AppCompatActivity {
    private EditText emailField, passwordField;
    private Button loginButton, facebookButton, twitterButton;
    private TextView signUp;
    private String TAG = LoginActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);
        loginButton = findViewById(R.id.loginButton);
        facebookButton = findViewById(R.id.facebookButton);
        twitterButton = findViewById(R.id.twitterButton);
        signUp = findViewById(R.id.signUp);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailField.getText().toString().trim();
                String password = passwordField.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(LoginActivity.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                } else {
                   makePostRequest(email,password);
                }

            }
        });

        facebookButton.setOnClickListener(v ->
                Toast.makeText(LoginActivity.this, "Facebook login clicked", Toast.LENGTH_SHORT).show());

        twitterButton.setOnClickListener(v ->
                Toast.makeText(LoginActivity.this, "Twitter login clicked", Toast.LENGTH_SHORT).show());

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void makePostRequest(String email, String password) {
        // Create OkHttpClient instance
        OkHttpClient client = new OkHttpClient();
        Log.e(TAG,"===Url"+CommonUtils.getApiUrl("/login"));
        // Define the data to send in the request
        RequestBody formBody = new FormBody.Builder()
                .add("email", email)
                .add("password", password)
                .build();

        // Create the request
        Request request = new Request.Builder()
                .url(CommonUtils.getApiUrl("/login"))
                .post(formBody)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        // Perform the request asynchronously
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("LoginError", "Request failed: " + e.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Create and display the AlertDialog
                        new AlertDialog.Builder(LoginActivity.this)
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
                    // Parse the response
                    assert response.body() != null;
                    String responseData = response.body().string();
                    try {
                        Log.e("RESP:",responseData);
                        JSONObject jsonResponse = new JSONObject(responseData);
                        JSONObject user = jsonResponse.getJSONObject("data").getJSONObject("user");
                        String token = jsonResponse.getJSONObject("data").getString("token");

//                        // Save to SharedPreferences
//                        SharedPreferences sharedPreferences = getSharedPreferences(CommonUtils.getPreferName(), MODE_PRIVATE);
//                        SharedPreferences.Editor editor = sharedPreferences.edit();
//
//                        // Store user details
//                        editor.putInt("user_id", user.getInt("id"));
//                        editor.putString("user_name", user.getString("name"));
//                        editor.putString("user_email", user.getString("email"));
//                        editor.putBoolean("islogin",true);
//                        editor.putString("token", token);
//
//                        // Apply changes
//                        editor.apply();

                        CommonSharedPreferences.saveInt(LoginActivity.this,"user_id", user.getInt("id"));
                        CommonSharedPreferences.saveString(LoginActivity.this,"user_name", user.getString("name"));
                        CommonSharedPreferences.saveString(LoginActivity.this,"user_email", user.getString("email"));
                        CommonSharedPreferences.saveString(LoginActivity.this,"token", token);
                        CommonSharedPreferences.saveBoolean(LoginActivity.this,"islogin",true);

                        // Switch to main thread to show success message
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new AlertDialog.Builder(LoginActivity.this)
                                        .setTitle("Login Successful")
                                        .setMessage("Welcome To carbon application !")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
//                                                Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                finish();
                                            }
                                        })
                                        .show();
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    // Handle error response
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(LoginActivity.this)
                                    .setTitle("Login Error")
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