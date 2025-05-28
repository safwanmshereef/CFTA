package com.singularis.carbon.Beans;

public class FuelType {
    private int fuelId;
    private String name,created_at,updated_at;

    public FuelType(int fuelId, String name, String created_at, String updated_at) {
        this.fuelId = fuelId;
        this.name = name;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public int getFuelId() {
        return fuelId;
    }

    public String getName() {
        return name;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    @Override
    public String toString() {
        return name;
    }
}
