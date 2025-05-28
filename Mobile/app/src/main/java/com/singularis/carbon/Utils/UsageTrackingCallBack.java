package com.singularis.carbon.Utils;

public interface UsageTrackingCallBack {
    void onUsageDataReceived(long callDuration, long smsCount, long mobileDataUsage, long wifiDataUsage);
}
