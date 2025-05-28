package com.singularis.carbon.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.singularis.carbon.Beans.Cars;
import com.singularis.carbon.R;

import java.util.ArrayList;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.CarViewHolder> {

    private final ArrayList<Cars> carList;

    public CarAdapter(ArrayList<Cars> carList) {
        this.carList = carList;
    }

    @NonNull
    @Override
    public CarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_car, parent, false);
        return new CarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarViewHolder holder, int position) {
        Cars car = carList.get(position);
        holder.carName.setText(car.getMake().toString());
        holder.carMake.setText(car.getMake().toString());
        holder.carModel.setText(car.getModel().toString());
        holder.carYear.setText(car.getTransmissionTypeName().toString());
        holder.carMileage.setText(car.getMilage().toString());
        holder.carCo2.setText(car.getCo2().toString());
    }

    @Override
    public int getItemCount() {
        return carList.size();
    }

    static class CarViewHolder extends RecyclerView.ViewHolder {

        TextView carName, carMake, carModel, carYear, carMileage, carCo2;

        public CarViewHolder(@NonNull View itemView) {
            super(itemView);
            carName = itemView.findViewById(R.id.name);
            carMake = itemView.findViewById(R.id.make);
            carModel = itemView.findViewById(R.id.model);
            carYear = itemView.findViewById(R.id.year);
            carMileage = itemView.findViewById(R.id.mileage);
            carCo2 = itemView.findViewById(R.id.co2);
        }
    }
}