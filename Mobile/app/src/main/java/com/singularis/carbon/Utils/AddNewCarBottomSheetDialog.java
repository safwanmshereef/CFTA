package com.singularis.carbon.Utils;

import android.content.Intent;
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
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputLayout;
import com.singularis.carbon.Activitys.SettingsActivity;
import com.singularis.carbon.Beans.FuelType;
import com.singularis.carbon.Beans.TransmissionType;
import com.singularis.carbon.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddNewCarBottomSheetDialog extends BottomSheetDialogFragment {
    private TextInputLayout makeInputLayout, modelInputLayout, engineSizeInputLayout, mileageInputLayout, co2InputLayout;
    private Spinner fuelTypeSpinner, transmissionTypesSpinner;
    private Button submitButton;
    private ArrayList<FuelType> fuelTypes;
    private ArrayList<TransmissionType> transmissionTypes;
    private String selectedTransmissionType = "", selectedFuelType = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_new_car_bottom_sheet_layout, container, false);

        initUI(view); // Initialize UI elements
        initConstants(); // Any constants or default data

        return view;
    }

    private void initUI(View view) {
        // Initialize TextInputLayouts or other UI elements
        makeInputLayout = view.findViewById(R.id.makeInputLayout);
        modelInputLayout = view.findViewById(R.id.modelInputLayout);
        engineSizeInputLayout = view.findViewById(R.id.engineSizeInputLayout);
        mileageInputLayout = view.findViewById(R.id.mileageInputLayout);
        co2InputLayout = view.findViewById(R.id.co2InputLayout);
        fuelTypeSpinner = view.findViewById(R.id.fuelTypeSpinner);
        transmissionTypesSpinner = view.findViewById(R.id.transmissionTypesSpinner);
        submitButton = view.findViewById(R.id.submitButton);

        // Example of additional setup (optional)
        makeInputLayout.setHint("Enter car make");
        modelInputLayout.setHint("Enter car model");
        engineSizeInputLayout.setHint("Enter engine size");
        mileageInputLayout.setHint("Enter car mileage");
        co2InputLayout.setHint("Enter car co2");
    }

    private void initConstants() {
        fuelTypes = new ArrayList<>();
        transmissionTypes = new ArrayList<>();
        getFuelTypes();
        getTransmissionTypes();

        // Handle spinner selections
        transmissionTypesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                selectedTransmissionType = parent.getItemAtPosition(position).toString();
                selectedTransmissionType = String.valueOf(transmissionTypes.get(position).getTranId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedTransmissionType = "";
            }
        });

        fuelTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                selectedFuelType = parent.getItemAtPosition(position).toString();
                selectedFuelType = String.valueOf(fuelTypes.get(position).getFuelId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedFuelType = "";
            }
        });

        submitButton.setOnClickListener(v -> sendCreateCarRequest());
    }

    private void getFuelTypes() {
        // Create OkHttpClient instance
        OkHttpClient client = new OkHttpClient.Builder()
                .build();

        // Create a request
        Request request = new Request.Builder()
                .url(CommonUtils.getApiUrl("/fuel-types"))
                .get()
                .build();

        // Make asynchronous call
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // Show an AlertDialog on failure
                getActivity().runOnUiThread(() -> new AlertDialog.Builder(getActivity())
                        .setTitle("Request Failed")
                        .setMessage("Request failed: " + e.getMessage())
                        .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                        .show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Convert response to a JSON object
                    assert response.body() != null;
                    String responseData = response.body().string();
                    try {
                        JSONObject jsonResponse = new JSONObject(responseData);
                        getActivity().runOnUiThread(() -> {
                            // Update the UI or handle JSON data here
                            Log.e("FuelType Success:", jsonResponse.toString());
                            try {
                                JSONArray jsonArray = jsonResponse.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    int fuelId = jsonObject.getInt("fuel_id");
                                    String name = jsonObject.getString("name");
                                    String created_at = jsonObject.getString("created_at");
                                    String updated_at = jsonObject.getString("updated_at");
                                    fuelTypes.add(new FuelType(fuelId, name, created_at, updated_at));
                                }

                                // Create an ArrayAdapter
                                ArrayAdapter<FuelType> adapter = new ArrayAdapter<>(getActivity(),  R.layout.spinner_item, fuelTypes);
                                adapter.setDropDownViewResource( R.layout.spinner_item);

                                // Set the adapter to the spinner
                                fuelTypeSpinner.setAdapter(adapter);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        });
                    } catch (JSONException e) {
                        getActivity().runOnUiThread(() -> new AlertDialog.Builder(getActivity())
                                .setTitle("Parsing Error")
                                .setMessage("Failed to parse response: " + e.getMessage())
                                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                                .show());
                    }
                } else {
                    getActivity().runOnUiThread(() -> new AlertDialog.Builder(getActivity())
                            .setTitle("Request Failed")
                            .setMessage("Request failed with code: " + response.code())
                            .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                            .show());
                }
            }
        });
    }

    private void getTransmissionTypes() {
        // Create OkHttpClient instance
        OkHttpClient client = new OkHttpClient.Builder()
                .build();

        // Create a request
        Request request = new Request.Builder()
                .url(CommonUtils.getApiUrl("/transmissiontypes"))
                .get()
                .build();

        // Make asynchronous call
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // Show an AlertDialog on failure
                getActivity().runOnUiThread(() -> new AlertDialog.Builder(getActivity())
                        .setTitle("Request Failed")
                        .setMessage("Request failed: " + e.getMessage())
                        .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                        .show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Convert response to a JSON object
                    assert response.body() != null;
                    String responseData = response.body().string();
                    try {
                        JSONObject jsonResponse = new JSONObject(responseData);
                        getActivity().runOnUiThread(() -> {
                            // Update the UI or handle JSON data here
                            Log.e("FuelType Success:", jsonResponse.toString());
                            try {
                                JSONArray jsonArray = jsonResponse.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    int tranId = jsonObject.getInt("tran_id");
                                    String name = jsonObject.getString("name");
                                    transmissionTypes.add(new TransmissionType(tranId, name));
                                }

                                // Create an fuelType ArrayAdapter
                                ArrayAdapter<TransmissionType> transmissionadapter = new ArrayAdapter<>(getActivity(),  R.layout.spinner_item, transmissionTypes);
                                transmissionadapter.setDropDownViewResource( R.layout.spinner_item);
                                transmissionTypesSpinner.setAdapter(transmissionadapter);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        });
                    } catch (JSONException e) {
                        getActivity().runOnUiThread(() -> new AlertDialog.Builder(getActivity())
                                .setTitle("Parsing Error")
                                .setMessage("Failed to parse response: " + e.getMessage())
                                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                                .show());
                    }
                } else {
                    getActivity().runOnUiThread(() -> new AlertDialog.Builder(getActivity())
                            .setTitle("Request Failed")
                            .setMessage("Request failed with code: " + response.code())
                            .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                            .show());
                }
            }
        });
    }

    private void sendCreateCarRequest() {
        // Retrieve input values
        String make = makeInputLayout.getEditText().getText().toString().trim();
        String model = modelInputLayout.getEditText().getText().toString().trim();
        String engineSize = engineSizeInputLayout.getEditText().getText().toString().trim();
        String mileage = mileageInputLayout.getEditText().getText().toString().trim();
        String co2 = co2InputLayout.getEditText().getText().toString().trim();

        // Create request body
        RequestBody formBody = new FormBody.Builder()
                .add("make", make)
                .add("model", model)
                .add("engine_size", engineSize)
                .add("transmission_type", selectedTransmissionType)
                .add("fuel_type", selectedFuelType)
                .add("milage", mileage)
                .add("co2", co2)
                .build();

        // Create OkHttpClient
        OkHttpClient client = new OkHttpClient();
        String authToken = CommonSharedPreferences.getString(getActivity(),"token",null);
        // Create request
        Request request = new Request.Builder()
                .url(CommonUtils.getApiUrl("/create-car"))
                .post(formBody)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Authorization", "Bearer " + authToken)
                .build();

        // Make the network call
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Request failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    String responseData = response.body().string();
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getActivity(), "Car created successfully!", Toast.LENGTH_SHORT).show();
                        System.out.println("Response: " + responseData);

                        // Dismiss the bottom sheet
                        dismissBottomSheet();

                        // Reload SettingsActivity
                        Intent intent = new Intent(getActivity(), SettingsActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        getActivity().finish();
                    });
                } else {
                    getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Failed with code: " + response.code(), Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    // Method to dismiss the bottom sheet
    public void dismissBottomSheet() {
        dismiss();
    }
}
