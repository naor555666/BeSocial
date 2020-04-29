package com.example.besocial.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.besocial.R;
import com.example.besocial.ui.mainactivity.MainActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class LocationUpdatesService extends Service {
    private static final String PACKAGE_NAME = "com.example.besocial.utils";
    private static final String TAG = "LocationUpdatesService";
    public static final String CHANNEL_ID = "ForegroundServiceChannel";

    static final String ACTION_BROADCAST = PACKAGE_NAME + ".broadcast";
    static final String EXTRA_LOCATION = PACKAGE_NAME + ".location";

    public static final int NOTIFICATION_ID1 = 1;
    private static final long INTERVALS_GAP = 5 * 1000;
    private NotificationManager mNotiMgr;
    private Notification.Builder mNotifyBuilder;

    FusedLocationProviderClient mFusedLocationClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private static LocationUpdatesService instance;
    private static GeofencingEvent geofencingClient;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    onNewLocation(location);
                }
            }
        };
        initForeground();
    }

    private void initForeground() {
        Log.d(TAG, "on initForeground");

        if (mNotiMgr == null)
            mNotiMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                "My foreground channel",
                NotificationManager.IMPORTANCE_DEFAULT);

        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                .createNotificationChannel(channel);
        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        mNotifyBuilder = new Notification.Builder(this, CHANNEL_ID)
                .setContentTitle("Location")
                .setSmallIcon(R.drawable.ic_besocial_notification)
                .setContentIntent(pendingIntent);
    }

    public Notification updateNotification(String details) {
        mNotifyBuilder.setContentText(details).setOnlyAlertOnce(false);
        Notification noti = mNotifyBuilder.build();
        noti.flags = Notification.FLAG_ONLY_ALERT_ONCE;
        mNotiMgr.notify(NOTIFICATION_ID1, noti);
        return noti;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(NOTIFICATION_ID1, updateNotification("Location is being used"));
        getLastLocation();
        startLocationUpdates();
        return super.onStartCommand(intent, flags, startId);
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkLocationPermission()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    requestNewLocationData();
                                    onNewLocation(location);
                                    // Log.d(TAG, "latitude: " + location.getLatitude() + " longitude :" + location.getLongitude());

                                }
                            }
                        }
                );
            } else {
                Toast.makeText(getBaseContext(), "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {

        }
    }

    //if not already granted, request for the permission to use location
    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        mLocationRequest = LocationRequest.create();
        //isLocationEnabled(mLocationRequest);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(INTERVALS_GAP);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //mLocationRequest.setNumUpdates(10);

        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );
    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            onNewLocation(locationResult.getLastLocation());
        }
    };

    private void startLocationUpdates() {
        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                mLocationCallback,
                Looper.getMainLooper());
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    private void onNewLocation(Location location) {
        mLastLocation = location;
        Log.d(TAG, "latitude: " + mLastLocation.getLatitude() + " longitude :" + mLastLocation.getLongitude());

        // Notify anyone listening for broadcasts about the new location.
        Intent intent = new Intent(ACTION_BROADCAST);
        intent.putExtra(EXTRA_LOCATION, location);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getBaseContext().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    public boolean stopService(Intent name) {
        return super.stopService(name);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "stopping location service");
        stopLocationUpdates();
        removeGeofences();
        super.onDestroy();
    }

    private void removeGeofences() {
/*        geofencingClient.removeGeofences(MainActivity.getGeofencePendingIntent())
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Geofences removed
                        // ...
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to remove geofences
                        // ...
                    }
                });*/
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static LocationUpdatesService getInstance() {
        return instance;
    }
}
