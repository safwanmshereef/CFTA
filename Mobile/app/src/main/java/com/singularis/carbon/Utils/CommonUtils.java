package com.singularis.carbon.Utils;

public class CommonUtils {

    // Declare a constant for the base URL or any endpoint
    public static final String BASE_URL = "http://192.168.82.20";
    public static final String PORT = ":8080";
    public static final String Access = "/api";
    public static final String Storage = "/storage";

    // Example of a complete URL method
    public static String getApiUrl(String endpoint) {
        return BASE_URL + PORT + Access + endpoint;
    }
    // Example of a complete URL method
    public static String getImageUrl(String imageName) {
        return BASE_URL +PORT+ Storage + imageName;
    }
    // Example of a complete URL method
    public static String getPreferName() {
        return "CarbonPref";
    }
}
