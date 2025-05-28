package com.singularis.carbon.Beans;

public class TravelData {
    private int id;
    private String userId;
    private String mot;
    private String vehicleId;
    private String make;
    private String model;
    private String engineSize;
    private String transport;
    private String fromLat;
    private String fromLong;
    private String toLat;
    private String toLong;
    private String startDateTime;
    private String endDateTime;
    private String usageId;
    private String car_co2;
    private String gprs;
    private String sms;
    private String phone;
    private String co2;
    private String mediaId;
    private String status;
    private String createdAt;
    private String updatedAt;

    // Constructor
    public TravelData(int id, String userId, String mot, String vehicleId, String make, String model,
                      String engineSize, String transport, String fromLat, String fromLong, String toLat,
                      String toLong, String startDateTime, String endDateTime, String usageId,String car_co2,String gprs,String sms,String phone, String co2,
                      String mediaId, String status, String createdAt, String updatedAt) {
        this.id = id;
        this.userId = userId;
        this.mot = mot;
        this.vehicleId = vehicleId;
        this.make = make;
        this.model = model;
        this.engineSize = engineSize;
        this.transport = transport;
        this.fromLat = fromLat;
        this.fromLong = fromLong;
        this.toLat = toLat;
        this.toLong = toLong;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.usageId = usageId;
        this.car_co2 = car_co2;
        this.gprs = gprs;
        this.sms = sms;
        this.phone = phone;
        this.co2 = co2;
        this.mediaId = mediaId;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters
    public int getId() { return id; }
    public String getUserId() { return userId; }
    public String getMot() { return mot; }
    public String getVehicleId() { return vehicleId; }
    public String getMake() { return make; }
    public String getModel() { return model; }
    public String getEngineSize() { return engineSize; }
    public String getTransport() { return transport; }
    public String getFromLat() { return fromLat; }
    public String getFromLong() { return fromLong; }
    public String getToLat() { return toLat; }
    public String getToLong() { return toLong; }
    public String getStartDateTime() { return startDateTime; }
    public String getEndDateTime() { return endDateTime; }
    public String getUsageId() { return usageId; }
    public String getCarCO2() { return car_co2; }
    public String getGprs() { return gprs; }
    public String getSMS() { return sms; }
    public String getPhone() { return phone; }
    public String getCo2() { return co2; }
    public String getMediaId() { return mediaId; }
    public String getStatus() { return status; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
}

