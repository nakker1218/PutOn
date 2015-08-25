package com.shohei.put_on.controller.utils;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by nakayamashohei on 15/08/20.
 */
public class LocationUtil implements LocationListener {
    private final static String LOG_TAG = LocationUtil.class.getSimpleName();

    private static final int LOCATION_UPDATE_MIN_TIME = 0;
    private static final int LOCATION_UPDATE_MIN_DISTANCE = 0;

    private Context mContext;

    private LocationManager mLocationManager;

    public LocationUtil(Context context) {
        this.mContext = context;
    }

    public void getCurrentLocation() {
        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setAltitudeRequired(false);
        String provider = mLocationManager.getBestProvider(criteria, true);
        if (DebugUtil.DEBUG) Log.d(LOG_TAG, "Provider: " + provider);
        mLocationManager.requestLocationUpdates(provider, LOCATION_UPDATE_MIN_TIME, LOCATION_UPDATE_MIN_DISTANCE, this);
    }

    public void stopGetLocation() {
        mLocationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (DebugUtil.DEBUG) Log.d(LOG_TAG, "Latitude:" + location.getLatitude() + " Longitude:" + location.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
