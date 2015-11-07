package com.shohei.put_on.controller.utils;

import android.app.ActivityManager;
import android.content.Context;

import com.shohei.put_on.service.LayerService;

import java.util.List;

/**
 * Created by nakayamashohei on 15/08/29.
 */
public class ServiceRunningDetector {
    private Context mContext;

    public ServiceRunningDetector(Context context) {
        mContext = context;
    }

    public boolean isServiceRunning() {
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(mContext.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningService
                = activityManager.getRunningServices(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo serviceInfo : runningService) {
            if (LayerService.class.getName().equals(serviceInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}