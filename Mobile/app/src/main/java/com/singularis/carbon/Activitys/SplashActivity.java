package com.singularis.carbon.Activitys;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.singularis.carbon.R;
import com.singularis.carbon.Utils.CommonSharedPreferences;
import com.singularis.carbon.Utils.CommonUtils;
import com.singularis.carbon.Utils.GPSTracker;

public class SplashActivity extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private String TAG = SplashActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initUI();
        initConstants();
    }

    private void initConstants() {
        try{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                askPermission();
            }
        }catch (Exception e){e.printStackTrace();}
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void askPermission() {
        try{
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.PROCESS_OUTGOING_CALLS) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.PACKAGE_USAGE_STATS) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED|| ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                // Show the permission request dialog
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.INTERNET,
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
                        Manifest.permission.FOREGROUND_SERVICE_LOCATION,
                        Manifest.permission.POST_NOTIFICATIONS,
                }, LOCATION_PERMISSION_REQUEST_CODE);
            } else {
                // Permission is already granted
                delayScreen();
            }



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void delayScreen() {
        try {
            GPSTracker gpsTracker = new GPSTracker(SplashActivity.this);
            if(gpsTracker.getIsGPSTrackingEnabled()) {
                Log.e(TAG, "Latitude:" + gpsTracker.getLatitude());
            }
            // Delay for 5 seconds before navigating to LoginActivity
            // Delay for 5 seconds before navigating to the appropriate activity
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Check if the user is logged in
                    boolean isLoggedIn = CommonSharedPreferences.isLoggedIn(SplashActivity.this);

                    Intent intent;
                    if (isLoggedIn) {
                        // Navigate to HomeActivity if logged in
                        intent = new Intent(SplashActivity.this, HomeActivity.class);
                    } else {
                        // Navigate to LoginActivity if not logged in
                        intent = new Intent(SplashActivity.this, LoginActivity.class);
                    }

                    // Add flags to clear previous activities
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    // Start the target activity
                    startActivity(intent);

                    // Finish SplashActivity to remove it from the back stack
                    finish();
                }
            }, 5000); // 5000 milliseconds = 5 seconds
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initUI() {
        try{

        }catch (Exception e){e.printStackTrace();}
    }

    // Handle permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                delayScreen();
            } else {
                // Permission denied
                Toast.makeText(this, "Location permission denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}