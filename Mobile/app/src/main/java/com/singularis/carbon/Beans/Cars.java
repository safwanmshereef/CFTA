package com.singularis.carbon.Beans;

public class Cars {
    private int carId;
    private int userId;
    private String make;
    private String model;
    private String engineSize;
    private int transmissionTypeId;
    private String transmissionTypeName;
    private int fuelTypeId;
    private String fuelTypeName;
    private String milage;
    private String co2;

    public Cars(int carId, int userId, String make, String model, String engineSize, int transmissionTypeId, String transmissionTypeName, int fuelTypeId, String fuelTypeName, String milage, String co2) {
        this.carId = carId;
        this.userId = userId;
        this.make = make;
        this.model = model;
        this.engineSize = engineSize;
        this.transmissionTypeId = transmissionTypeId;
        this.transmissionTypeName = transmissionTypeName;
        this.fuelTypeId = fuelTypeId;
        this.fuelTypeName = fuelTypeName;
        this.milage = milage;
        this.co2 = co2;
    }

    public int getCarId() {
        return carId;
    }

    public int getUserId() {
        return userId;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public String getEngineSize() {
        return engineSize;
    }

    public int getTransmissionTypeId() {
        return transmissionTypeId;
    }

    public String getTransmissionTypeName() {
        return transmissionTypeName;
    }

    public int getFuelTypeId() {
        return fuelTypeId;
    }

    public String getFuelTypeName() {
        return fuelTypeName;
    }

    public String getMilage() {
        return milage;
    }

    public String getCo2() {
        return co2;
    }

    @Override
    public String toString() {
        return make +'-'+ model ;
    }
}
