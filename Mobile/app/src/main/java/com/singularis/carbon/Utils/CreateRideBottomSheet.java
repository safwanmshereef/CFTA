package com.singularis.carbon.Utils;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.singularis.carbon.Activitys.HomeActivity;
import com.singularis.carbon.Activitys.LoginActivity;
import com.singularis.carbon.Activitys.SettingsActivity;
import com.singularis.carbon.Adapters.CarAdapter;
import com.singularis.carbon.Beans.Cars;
import com.singularis.carbon.R;
import com.singularis.carbon.Utils.Ride.RideMonitoringService;
import com.singularis.carbon.Utils.Ride.RidePreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreateRideBottomSheet extends BottomSheetDialogFragment {
    private String TAG = CreateRideBottomSheet.class.getSimpleName();
    private Spinner modeOfVehicleSpinner;
    private Spinner ownVehicleSpinner;
    private View ownVehicleLayout;
    private Button submitButton;
    private LocationHelper locationHelper;
    private ArrayList<Cars> carList;
    private double latitude,  longitude;

    private Location location = null;
    private LocationManager locationManager = null;
    private SharedPreferences currentLocation;
    private SharedPreferences.Editor editor;

    private static final int PERMISSION_REQUEST_CODE = 100;
    private GPSTracker gpsTracker;

    public CreateRideBottomSheet() {
        // Default constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_ride_bottom_sheet, container, false);
        initUI(view);
        initConstants();

        return view;
    }

    private void initConstants() {
        // Initialize LocationHelper
//        locationHelper = new LocationHelper(getActivity());
//        locationHelper.getCurrentLocation();
//        locationHelper.setLocationCallback((latitude, longitude) -> { Log.e("LOG:","Latitude: " + latitude + ", Longitude: " + longitude); this.latitude = latitude; this.longitude = longitude; });

        gpsTracker = new GPSTracker(getActivity());


//        locationHelper = new LocationHelper(getActivity(), new LocationHelper.LocationCallback() {
//            @Override
//            public void onLocationReceived(double latitude, double longitude) {
//
//            }
//        });
//
//
//        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
//
//        Criteria locationCritera = new Criteria();
//        locationCritera.setAccuracy(Criteria.ACCURACY_FINE);
//        locationCritera.setAltitudeRequired(false);
//        locationCritera.setBearingRequired(false);
//        locationCritera.setCostAllowed(true);
//        locationCritera.setPowerRequirement(Criteria.NO_REQUIREMENT);
//
//        String providerName = locationManager.getBestProvider(locationCritera,true);
//
//        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//
//        location = locationManager.getLastKnownLocation(providerName);
//        MyLocationListener locationListener = new MyLocationListener();
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0, locationListener);
//
//        currentLocation = getActivity().getSharedPreferences("PREFS_NAME", 0);
//        editor = currentLocation.edit();


        getMyCars();

        String[] modes = {"Public", "Own", "Hybrid","Cycle"}; // Modes for the spinner

        // Create an ArrayAdapter with a custom spinner item layout
        ArrayAdapter<String> modeAdapter = new ArrayAdapter<>(requireContext(), R.layout.spinner_item, modes);
        modeAdapter.setDropDownViewResource(R.layout.spinner_item); // Set dropdown view resource
        modeOfVehicleSpinner.setAdapter(modeAdapter); // Attach adapter to spinner

        // Handle mode selection
        modeOfVehicleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Check the selected mode and update the layout visibility
                if ("Own".equals(modes[position])) { // Use .equals() for string comparison
                    ownVehicleLayout.setVisibility(View.VISIBLE); // Show the own vehicle layout
                } else {
                    ownVehicleLayout.setVisibility(View.GONE); // Hide the own vehicle layout
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Default to hiding the layout if nothing is selected
                ownVehicleLayout.setVisibility(View.GONE);
            }
        });


        // Handle submit button click
        submitButton.setOnClickListener(v -> {

                String modeOfVehicle = modeOfVehicleSpinner.getSelectedItem().toString();
                int selectedPosition = ownVehicleSpinner.getSelectedItemPosition();
                String selectedCar = modeOfVehicle.equals("Own") ? String.valueOf(carList.get(selectedPosition).getCarId()) : "null";

//                // Show alert dialog with ride details
//                new AlertDialog.Builder(requireContext())
//                        .setTitle("Ride Started")
//                        .setMessage("Latitude: " + getCurrentLatitude() + "\nLongitude: " + getCurrentLongitude() + "\nMode: " + modeOfVehicle + (modeOfVehicle.equals("Own") ? "\nCar: " + selectedCar : ""))
//                        .setPositiveButton("OK", null)
//                        .show();



            if (RidePreferences.isRideActive(getActivity())) {
                Toast.makeText(getActivity(), "An active ride is already in progress!", Toast.LENGTH_SHORT).show();
                return;
            }else{
                createPostAction(String.valueOf(gpsTracker.getLatitude()),String.valueOf(gpsTracker.getLongitude()),modeOfVehicle, selectedCar);
            }
            });
    }
    public String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }
    private void createPostAction(String currentLatitude, String currentLongitude, String modeOfVehicle, String selectedCar) {
        try{
            OkHttpClient client = new OkHttpClient();
            String authToken = CommonSharedPreferences.getString(getActivity(),"token",null);
            // Form data
            RequestBody body = new FormBody.Builder()
                    .add("mot", modeOfVehicle.equals("Own") ? "Own":"Public")
                    .add("vehicle_id", selectedCar)
                    .add("transport", modeOfVehicle.equals("Own") ? "null":"Public")
                    .add("from_lat", currentLatitude)
                    .add("from_long", currentLongitude)
                    .add("start_date_time", getCurrentTime())
                    .build();

            // Request
            Request request = new Request.Builder()
                    .url(CommonUtils.getApiUrl("/routes"))
                    .post(body)
                    .addHeader("Authorization", "Bearer " + authToken)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .build();

            // Execute the request asynchronously
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Create and display the AlertDialog
                            new AlertDialog.Builder(getActivity())
                                    .setTitle("Request Failed")
                                    .setMessage("Request failed: " + e.getMessage())
                                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                                    .show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try{
                    if (response.isSuccessful()) {
                        // Handle response
                        String responseData = response.body().string();
                        JSONObject jsonResponse = null;
                        try {
                            jsonResponse = new JSONObject(responseData);
                            Log.e(TAG,"Responce:"+jsonResponse.toString());
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        // Process response data (e.g., log it or update UI)
                        JSONObject finalJsonResponse = jsonResponse;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    new AlertDialog.Builder(getActivity())
                                            .setTitle("Request successfully")
                                            .setMessage(finalJsonResponse.getString("message"))
                                            .setPositiveButton("OK", (dialog, which) -> {
                                                if (getActivity() == null) return; // Prevent NullPointerException

                                                if (checkPermissions()) {
                                                    try {
                                                        RidePreferences.startRide(getActivity(), finalJsonResponse.getString("data"));

                                                        // Start RideMonitoringService
                                                        Intent serviceIntent = new Intent(getActivity(), RideMonitoringService.class);
                                                        getActivity().startService(serviceIntent);

                                                        // Restart HomeActivity
                                                        Intent intent = new Intent(getActivity(), HomeActivity.class);
                                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        getActivity().startActivity(intent);

                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                } else {
                                                    requestPermissions();
                                                }

                                                dialog.dismiss();
                                                dismiss(); // Assuming this is a DialogFragment, otherwise remove this line
                                            })
                                            .show();

                                }catch(Exception e){e.printStackTrace();}
                                }
                            });
                    } else {
                        // Handle unsuccessful response
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Create and display the AlertDialog
                                new AlertDialog.Builder(getActivity())
                                        .setTitle("Request Failed")
                                        .setMessage("Unexpected response: " + response.code())
                                        .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                                        .show();
                            }
                        });
                    }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }catch (Exception e){e.printStackTrace();}
    }

    // Check if we have the necessary permissions
    private boolean checkPermissions() {
        int phoneStatePermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE);
        int sendSmsPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.SEND_SMS);
        int receiveSmsPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECEIVE_SMS);
        int accessNetworkStatePermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_NETWORK_STATE);
        int accessWifiStatePermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_WIFI_STATE);

        return phoneStatePermission == PackageManager.PERMISSION_GRANTED &&
                sendSmsPermission == PackageManager.PERMISSION_GRANTED &&
                receiveSmsPermission == PackageManager.PERMISSION_GRANTED &&
                accessNetworkStatePermission == PackageManager.PERMISSION_GRANTED &&
                accessWifiStatePermission == PackageManager.PERMISSION_GRANTED;
    }

    // Request permissions if not granted
    private void requestPermissions() {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.SEND_SMS,
                        Manifest.permission.RECEIVE_SMS,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.ACCESS_WIFI_STATE
                },
                PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (allPermissionsGranted(grantResults)) {
                getActivity().startService(new Intent(getActivity(), UsageTrackingService.class));
            } else {
                Toast.makeText(getActivity(), "Permissions are required to track usage", Toast.LENGTH_LONG).show();
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

    public String getCurrentLatitude() {
        try {
            if (!currentLocation.getString("currentLatitude", "").equalsIgnoreCase("")) return currentLocation.getString("currentLatitude", "");
            else if (location.getLatitude() != 0.0) return Double.toString(location.getLatitude());
            else return "0.0";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0.0";
    }

    public String getCurrentLongitude() {
        try {
            if (!currentLocation.getString("currentLongitude", "").equalsIgnoreCase("")) return currentLocation.getString("currentLongitude", "");
            else if (location.getLongitude() != 0.0) return Double.toString(location.getLongitude());
            else return "0.0";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0.0";
    }

    private void getMyCars() {
        String authToken = CommonSharedPreferences.getString(getActivity(), "token", null);
        carList = new ArrayList<>();
        Log.e("LOG:", authToken);
        // Create HTTP client
        OkHttpClient client = new OkHttpClient();

        Log.e("TAG", "fetchUserCars URL:" + CommonUtils.getApiUrl("/get-cars"));
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
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Create and display the AlertDialog
                        new androidx.appcompat.app.AlertDialog.Builder(getActivity())
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
                    Log.e("fetchUserCars", responseData);

                    // Handle success on UI thread if needed
                    getActivity().runOnUiThread(() -> {
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
                                ArrayAdapter<Cars> ownAdapter = new ArrayAdapter<>(requireContext(), R.layout.spinner_item, carList);
                                ownAdapter.setDropDownViewResource(R.layout.spinner_item);
                                ownVehicleSpinner.setAdapter(ownAdapter);
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
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new androidx.appcompat.app.AlertDialog.Builder(getActivity())
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

    private void initUI(View view) {
        // Initialize views
        modeOfVehicleSpinner = view.findViewById(R.id.modeOfVehicleSpinner);
        ownVehicleSpinner = view.findViewById(R.id.ownVehicleSpinner);
        ownVehicleLayout = view.findViewById(R.id.ownVehicleLayout);
        submitButton = view.findViewById(R.id.submitButton);
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
}
