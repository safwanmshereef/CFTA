package com.singularis.carbon.Utils.Ride;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

public class RidePreferences {
    private static final String PREFS_NAME = "RidePrefs";
    private static final String RIDE_ID_KEY = "ride_id";
    private static final String SMS_COUNT_KEY = "sms_count";
    private static final String CALL_COUNT_KEY = "call_count";
    private static final String DATA_USAGE_KEY = "data_usage";
    private static final String DATA_USAGE_TIME_KEY = "data_usage_time";

    // Start Ride - Initializes ride tracking
    public static void startRide(Context context, String rideId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(RIDE_ID_KEY, rideId);
        editor.putInt(SMS_COUNT_KEY, 0);
        editor.putInt(CALL_COUNT_KEY, 0);
        editor.putLong(DATA_USAGE_KEY, 0);
        editor.apply();
    }

    // Check if a ride is active
    public static boolean isRideActive(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.contains(RIDE_ID_KEY);
    }
    // Get total data usage
    public static long getDataUsage(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getLong(DATA_USAGE_KEY, 0);
    }

    // Get SMS count
    public static int getSmsCount(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(SMS_COUNT_KEY, 0);
    }

    // Get Call count
    public static int getCallCount(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(CALL_COUNT_KEY, 0);
    }

    // End Ride - Retrieves stored data before clearing
    public static JSONObject endRide(Context context) throws JSONException {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        JSONObject rideData = getRideData(context); // Retrieve data before clearing

        // Clear ride data
        prefs.edit().clear().apply();

        return rideData; // Return ride data for use before deletion
    }

    // Increment SMS count
    public static void incrementSmsCount(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int count = prefs.getInt(SMS_COUNT_KEY, 0);
        prefs.edit().putInt(SMS_COUNT_KEY, count + 1).apply();
    }

    // Increment Call count
    public static void incrementCallCount(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int count = prefs.getInt(CALL_COUNT_KEY, 0);
        prefs.edit().putInt(CALL_COUNT_KEY, count + 1).apply();
    }

    // Update Internet Data Usage
    public static void updateDataUsage(Context context, long dataUsed) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        long totalData = prefs.getLong(DATA_USAGE_KEY, 0);
        prefs.edit().putLong(DATA_USAGE_KEY, totalData + dataUsed).apply();
    }

    public static void updateUsageTime(Context context, double minutes) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putFloat("data_usage_time", (float) minutes).apply();
    }

    // Get Ride Data as JSON
    public static JSONObject getRideData(Context context) throws JSONException {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        JSONObject data = new JSONObject();
        data.put("ride_id", prefs.getString(RIDE_ID_KEY, ""));
        data.put("sms_count", prefs.getInt(SMS_COUNT_KEY, 0));
        data.put("call_count", prefs.getInt(CALL_COUNT_KEY, 0));
        data.put("data_usage", prefs.getLong(DATA_USAGE_KEY, 0));
        data.put("data_usage_time", prefs.getFloat(DATA_USAGE_TIME_KEY, 0));
        return data;
    }

    // Get Ride Data as a Bundle (for Intent communication)
    public static Bundle getRideDataBundle(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Bundle bundle = new Bundle();
        bundle.putString("ride_id", prefs.getString(RIDE_ID_KEY, ""));
        bundle.putInt("sms_count", prefs.getInt(SMS_COUNT_KEY, 0));
        bundle.putInt("call_count", prefs.getInt(CALL_COUNT_KEY, 0));
        bundle.putLong("data_usage", prefs.getLong(DATA_USAGE_KEY, 0));
        bundle.putLong("data_usage_time", prefs.getLong(DATA_USAGE_TIME_KEY, 0));
        return bundle;
    }

    // Clear Ride Data
    public static void clearRideData(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().clear().apply(); // Asynchronous clearing
    }
}
