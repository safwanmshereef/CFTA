package com.singularis.carbon.Utils.Ride;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class CallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        RidePreferences.incrementCallCount(context);
    }
}