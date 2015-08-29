package com.shohei.put_on.controller.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.shohei.put_on.model.Memo;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by nakayamashohei on 15/08/20.
 */
public class LocationUtil implements LocationListener {
    private final static String LOG_TAG = LocationUtil.class.getSimpleName();

    private static final int LOCATION_UPDATE_MIN_TIME = 0;
    private static final int LOCATION_UPDATE_MIN_DISTANCE = 0;

    private Context mContext;

    private Memo mMemo;

    private LocationManager mLocationManager;

    public LocationUtil(Context context) {
        this.mContext = context;
        this.mMemo = new Memo();
        this.mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        final boolean isGpsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!isGpsEnabled) {
            enableLocationSettings();
        }
    }

    public void requestLocation() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setAltitudeRequired(false);
        try {
            String provider = mLocationManager.getBestProvider(criteria, true);
            if (DebugUtil.DEBUG) Log.d(LOG_TAG, "Provider: " + provider);
            mLocationManager.requestLocationUpdates(provider, LOCATION_UPDATE_MIN_TIME, LOCATION_UPDATE_MIN_DISTANCE, this);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private String getCurrentAddress(double latitude, double longitude) throws IOException, IllegalArgumentException {
        String address;
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
        if (!addressList.isEmpty()) {
            address = addressList.get(0).getAdminArea() + addressList.get(0).getLocality();
        } else {
            address = "位置情報の取得に失敗しました";
        }
        return address;
    }

    public void stopGetLocation() {
        mLocationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            mMemo.address = getCurrentAddress(location.getLatitude(), location.getLongitude());
            Log.d(LOG_TAG, mMemo.address);
        } catch (IOException e) {
            e.printStackTrace();
        }
        stopGetLocation();
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

    private void enableLocationSettings() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setMessage("GPSが有効になっていません。\n有効化しますか？")
                .setCancelable(false)
                        //GPS設定画面起動の処理
                .setPositiveButton("はい",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                mContext.startActivity(callGPSSettingIntent);
                            }
                        });
        //キャンセルボタン処理
        alertDialogBuilder.setNegativeButton("いいえ",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
}
