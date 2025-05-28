package com.singularis.carbon.Utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.util.concurrent.Executor;

public class LocationHelper {

    private final Context context;
    private final FusedLocationProviderClient fusedLocationClient;
    private final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private LocationCallback locationCallback;

    public LocationHelper(Context context, LocationCallback locationCallback) {
        this.context = context;
        this.locationCallback = locationCallback;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        getCurrentLocation();

    }

    // Check permissions and request if not granted
    public void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions();
        } else {
            fetchLocation();
        }
    }

    // Request location permissions
    private void requestPermissions() {
        if (context instanceof Activity) {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            Toast.makeText(context, "Permission request can only be shown in an Activity context", Toast.LENGTH_SHORT).show();
        }
    }

    // Fetch location after permission is granted
    @SuppressLint("MissingPermission")
    private void fetchLocation() {
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null && locationCallback != null) {
                locationCallback.onLocationReceived(location.getLatitude(), location.getLongitude());
            } else {
                requestNewLocationData();
            }
        }).addOnFailureListener
                (e ->
                Toast.makeText(context, "Failed to get location", Toast.LENGTH_SHORT).show()
        );
    }

    // Request fresh location update if last location is unavailable
    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(1000)
                .setMaxUpdateDelayMillis(5000)
                .build();

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationReceived(double latitude, double longitude) {
                if (LocationHelper.this.locationCallback != null) {
                    LocationHelper.this.locationCallback.onLocationReceived(latitude, longitude);
                }
            }
        };

        fusedLocationClient.requestLocationUpdates(locationRequest, (Executor) locationCallback, (LocationListener) null);
    }

    // Handle permission result in your activity/fragment
    public void handlePermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLocation();
            } else {
                Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Callback interface
    public interface LocationCallback {
        void onLocationReceived(double latitude, double longitude);
    }
}
