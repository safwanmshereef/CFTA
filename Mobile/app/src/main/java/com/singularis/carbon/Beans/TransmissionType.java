package com.singularis.carbon.Beans;

public class TransmissionType {
    private int tranId;
    private String name;

    // Constructor
    public TransmissionType(int tranId, String name) {
        this.tranId = tranId;
        this.name = name;
    }

    // Getters
    public int getTranId() {
        return tranId;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name; // This will be displayed in the spinner
    }
}

