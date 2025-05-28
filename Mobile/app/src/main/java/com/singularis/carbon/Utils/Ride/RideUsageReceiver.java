package com.singularis.carbon.Utils.Ride;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.singularis.carbon.Utils.Ride.RidePreferences;

public class RideUsageReceiver extends BroadcastReceiver {
    private static final String TAG = "RideUsageReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("com.singularis.carbon.ACTION_USAGE_UPDATE".equals(intent.getAction())) {
            int smsCount = RidePreferences.getSmsCount(context);
            int callCount = RidePreferences.getCallCount(context);
            long dataUsage = RidePreferences.getDataUsage(context);

            Log.d(TAG, "Updated Usage Data - SMS: " + smsCount + ", Calls: " + callCount + ", Data: " + dataUsage + " bytes");

            // Here, you can further process or store this data.
        }
    }
}
