package com.infocus.locator.Services;

import android.Manifest;
import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.infocus.locator.MainActivity;

/**
 * Created by User on 2/20/2018.
 */

public class GPService extends Service {
    private static final String TAG = GPService.class.getSimpleName();

    private long UPDATE_INTERVAL = 3 * 1000;
    private long FASTEST_INTERVAL = 1000;

    private LocationRequest mLocationRequest;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startLocationUpdates();
        return START_STICKY;
    }
    protected FusedLocationProviderClient getFusedLocationProviderClient() {
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        return client;
    }

    protected void startLocationUpdates() {
        Log.i(TAG, "Start");
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        Log.i(TAG, "Request created");
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);
        Log.i(TAG, "Put settings");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission denied");
            return;
        }
        Log.i(TAG, "Request...");
        getFusedLocationProviderClient().requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        Location location = locationResult.getLastLocation();
                        if (location != null) {
                            String sLog = "Location: (Lat:" + location.getLatitude() + "," + location.getLongitude() + "), accuracy=" + location.getAccuracy();
                            Log.i(TAG, sLog);
                            Intent intent = new Intent(MainActivity.ACC_DATA);
                            intent.putExtra(MainActivity.LDATA, sLog);
                            sendBroadcast(intent);
                        } else {
                            Log.i(TAG, "Location is NULL");
                        }
                    }
                },
                Looper.myLooper());
    }
}
