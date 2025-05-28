package com.singularis.carbon.Activitys;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.RequestBody;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.IOException;

public class RegisterActivity extends AppCompatActivity {
    private EditText nameField, emailField, passwordField, confirmPasswordField;
    private Button registerButton;
    private TextView loginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        nameField = findViewById(R.id.nameField);
        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);
        confirmPasswordField = findViewById(R.id.confirmPasswordField);
        registerButton = findViewById(R.id.registerButton);
        loginLink = findViewById(R.id.loginLink);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameField.getText().toString();
                String email = emailField.getText().toString();
                String password = passwordField.getText().toString();
                String confirmPassword = confirmPasswordField.getText().toString();

                if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else if (!password.equals(confirmPassword)) {
                    Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                } else {
                    registerUser(name, email, password, confirmPassword);
                }
            }
        });

        loginLink.setOnClickListener(v -> {
            // Navigate to Login Screen
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void registerUser(String name, String email, String password, String passwordConfirmation) {
        // Create HTTP client
        OkHttpClient client = new OkHttpClient();

        // Create request body
        RequestBody formBody = new FormBody.Builder()
                .add("name", name)
                .add("email", email)
                .add("password", password)
                .add("password_confirmation", passwordConfirmation)
                .build();

        // Create request
        Request request = new Request.Builder()
                .url(CommonUtils.getApiUrl("/register"))
                .post(formBody)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        // Execute request
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("LoginError", "Request failed: " + e.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Create and display the AlertDialog
                        new AlertDialog.Builder(RegisterActivity.this)
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

                        // Apply changes
//                        editor.apply();




                        CommonSharedPreferences.saveInt(RegisterActivity.this,"user_id", user.getInt("id"));
                        CommonSharedPreferences.saveString(RegisterActivity.this,"user_name", user.getString("name"));
                        CommonSharedPreferences.saveString(RegisterActivity.this,"user_email", user.getString("email"));
                        CommonSharedPreferences.saveString(RegisterActivity.this,"token", token);
                        CommonSharedPreferences.saveBoolean(RegisterActivity.this,"islogin",true);


                        // Switch to main thread to show success message
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new AlertDialog.Builder(RegisterActivity.this)
                                        .setTitle("Register Successful")
                                        .setMessage("Welcome To carbon application !")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
//                                                Toast.makeText(RegisterActivity.this, "Register successful", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
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
                            new AlertDialog.Builder(RegisterActivity.this)
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