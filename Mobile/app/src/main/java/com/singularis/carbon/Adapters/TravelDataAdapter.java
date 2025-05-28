package com.singularis.carbon.Adapters;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.singularis.carbon.Beans.TravelData;
import com.singularis.carbon.R;
import com.singularis.carbon.Utils.DistanceCalculator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TravelDataAdapter extends RecyclerView.Adapter<TravelDataAdapter.ViewHolder> {
    private String TAG = TravelDataAdapter.class.getSimpleName();
    private Context context;
    private ArrayList<TravelData> travelDataList;
    List<Location> locations;

    public TravelDataAdapter(Context context, ArrayList<TravelData> travelDataList) {
        this.context = context;
        this.travelDataList = travelDataList;
        this.locations = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_travel, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TravelData data = travelDataList.get(position);

        holder.startDate.setText("Start: " + data.getStartDateTime());
        holder.endDate.setText("End: " + data.getEndDateTime());
        holder.vehicleMake.setText("Make: " + (data.getMake() != null ? data.getMake() : "N/A"));
        holder.vehicleModel.setText("Model: " + (data.getModel() != null ? data.getModel() : "N/A"));
        holder.co2.setText(data.getCo2() != null ? "CO2: " + data.getCo2() + "g" : "CO2: N/A");

        // Fetch and display from location name
        String fromLocationName = getAddressFromCoordinates(data.getFromLat(), data.getFromLong());
        holder.fromLocation.setText("From: " + (fromLocationName != null ? fromLocationName : "Unknown Location"));
        // Fetch and display to location name
        String toLocationName = getAddressFromCoordinates(data.getToLat(), data.getToLong());
        holder.toLocation.setText("To: " + (toLocationName != null ? toLocationName : "Unknown Location"));

        this.locations.clear();
        Location loc1 = new Location("");
        loc1.setLatitude(Double.parseDouble(data.getFromLat()));
        loc1.setLongitude(Double.parseDouble(data.getFromLong()));

        Location loc2 = new Location("");
        loc2.setLatitude(Double.parseDouble(data.getToLat()));
        loc2.setLongitude(Double.parseDouble(data.getToLong()));

        this.locations.add(loc1);
        this.locations.add(loc2);
        double totalKm = DistanceCalculator.calculateTotalDistance(locations);
        Log.e(TAG,"locations:"+locations);
        Log.e(TAG,"totalKm:"+totalKm);
        holder.toKm.setText("KM "+totalKm);
        holder.todigital.setText("Digital \n GPRS: "+data.getGprs() + " SMS: "+data.getSMS() + " PHONE: "+data.getPhone());
        holder.toCarCo2.setText("Car \n CO2: "+data.getCarCO2());

    }

    @Override
    public int getItemCount() {
        return travelDataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView startDate, endDate, vehicleMake, vehicleModel, co2, fromLocation, toLocation,toKm,todigital,toCarCo2;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            startDate = itemView.findViewById(R.id.tv_start_date);
            endDate = itemView.findViewById(R.id.tv_end_date);
            vehicleMake = itemView.findViewById(R.id.tv_vehicle_make);
            vehicleModel = itemView.findViewById(R.id.tv_vehicle_model);
            co2 = itemView.findViewById(R.id.tv_co2);
            fromLocation = itemView.findViewById(R.id.tv_from_location);
            toLocation = itemView.findViewById(R.id.tv_to_location);
            toKm = itemView.findViewById(R.id.tv_km);
            todigital = itemView.findViewById(R.id.tv_digital);
            toCarCo2 = itemView.findViewById(R.id.tv_car_co2);
        }
    }

    // Convert latitude & longitude to an address using Geocoder
    private String getAddressFromCoordinates(String lat, String lng) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            double latitude = Double.parseDouble(lat);
            double longitude = Double.parseDouble(lng);
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses != null && !addresses.isEmpty()) {
                return addresses.get(0).getLocality();
//                return addresses.get(0).getAddressLine(0); // Full address
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        return null;
    }
}
