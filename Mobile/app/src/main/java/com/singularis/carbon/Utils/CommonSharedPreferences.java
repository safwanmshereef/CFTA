package com.singularis.carbon.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class CommonSharedPreferences {

    private static final String PREF_NAME = CommonUtils.getPreferName(); // Name of the SharedPreferences file

    // Private method to get SharedPreferences
    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
    public static boolean isLoggedIn(Context context) {
        return getSharedPreferences(context).getBoolean("islogin", false);
    }
    // Save a boolean value
    public static void saveBoolean(Context context, String key, boolean value) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply(); // Asynchronous saving
    }

    // Save a string value
    public static void saveString(Context context, String key, String value) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply(); // Asynchronous saving
    }

    // Save an integer value
    public static void saveInt(Context context, String key, int value) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply(); // Asynchronous saving
    }

    // Save a float value
    public static void saveFloat(Context context, String key, float value) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(key, value);
        editor.apply(); // Asynchronous saving
    }

    // Save a long value
    public static void saveLong(Context context, String key, long value) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(key, value);
        editor.apply(); // Asynchronous saving
    }

    // Retrieve a boolean value
    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    // Retrieve a string value
    public static String getString(Context context, String key, String defaultValue) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getString(key, defaultValue);
    }

    // Retrieve an integer value
    public static int getInt(Context context, String key, int defaultValue) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getInt(key, defaultValue);
    }

    // Retrieve a float value
    public static float getFloat(Context context, String key, float defaultValue) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getFloat(key, defaultValue);
    }

    // Retrieve a long value
    public static long getLong(Context context, String key, long defaultValue) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getLong(key, defaultValue);
    }

    // Delete a specific value using its key
    public static void deleteValue(Context context, String key) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.apply(); // Asynchronous deletion
    }

    // Clear all values in SharedPreferences
    public static void clearAll(Context context) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply(); // Asynchronous clearing
    }
}
