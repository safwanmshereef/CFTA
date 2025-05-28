package com.singularis.carbon.Beans;

public class Ride {
    private String id,startDateTime, make,model,fromLat,fromLong;

    public Ride(String id,String startDateTime, String make, String model, String fromLat, String fromLong) {
        this.id = id;
        this.startDateTime = startDateTime;
        this.make = make;
        this.model = model;
        this.fromLat = fromLat;
        this.fromLong = fromLong;
    }

    public String getId() {
        return id;
    }

    public String getStartDateTime() {
        return startDateTime;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public String getFromLat() {
        return fromLat;
    }

    public String getFromLong() {
        return fromLong;
    }
}
