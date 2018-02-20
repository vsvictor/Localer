package com.infocus.locator.Services;

import android.Manifest;
import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;

/**
 * Created by User on 2/20/2018.
 */

public class FusedService extends IntentService {
    private static final String TAG = FusedService.class.getSimpleName();
    private FusedLocationProviderClient fusedClient;

    public FusedService() {
        super("FusedService");
    }

    public FusedService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        fusedClient = LocationServices.getFusedLocationProviderClient(this);
        Log.i(TAG, "Created started");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.i(TAG, "Service started");

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                //currentLocation();
                reqLocation();
            }
        }, 0, 2000);


    }

    private void currentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission not granted");
            return;
        } else {
            Log.i(TAG, "Permission granted");
            fusedClient.getLastLocation()
                    .addOnSuccessListener(Executors.newSingleThreadExecutor(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                Log.i(TAG, "Location: (Lat:" + location.getLatitude() + "," + location.getLongitude() + "), accuracy=" + location.getAccuracy());
                            } else {
                                Log.i(TAG, "Location is NULL");
                            }
                        }
                    });
        }
    }

    private void reqLocation() {
        LocationCallback cb = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        Log.i(TAG, "Location: (Lan:" + location.getLatitude() + ", Lon:" + location.getLongitude() + "), accuracy=" + location.getAccuracy());
                    } else {
                        Log.i(TAG, "Location is NULL");
                    }
                }
            }

            ;
        };
        LocationRequest req = new LocationRequest();
        req.setInterval(1000);
        req.setFastestInterval(1000);
        req.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedClient.requestLocationUpdates(req, cb, Looper.getMainLooper());
    }
}
