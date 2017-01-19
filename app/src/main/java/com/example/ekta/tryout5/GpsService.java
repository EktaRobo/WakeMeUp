package com.example.ekta.tryout5;

import android.app.Service;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.os.ResultReceiver;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static com.google.android.gms.internal.zzs.TAG;

public class GpsService extends Service implements GoogleApiClient
        .OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, LocationListener {
    private Location mMyLocation;
    private ResultReceiver mResultReceiver;
    private LocationRequest mLocationRequest;


    public GpsService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate: Service started");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "An onConnectionFailed error occurred: " + connectionResult.getErrorMessage());
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.e(TAG, "onConnected ");
        checkForCurrentLocation();


    }

    private void checkForCurrentLocation() {
        if (LocationServices.FusedLocationApi != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(GpsApplication
                    .sGoogleApiClient, this);
        }
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        mLocationRequest.setInterval(0);
        PendingResult<Status> statusPendingResult = LocationServices.FusedLocationApi
                .requestLocationUpdates(GpsApplication.sGoogleApiClient, mLocationRequest, this);

        Log.i(TAG, "onConnected: " + statusPendingResult);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mMyLocation = new Location("point A");

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        mMyLocation.setLatitude(latitude);
        mMyLocation.setLongitude(longitude);


        String msg = "onLocationChanged: lat = " + latitude + " long = " + longitude;
        Log.e(TAG, msg);
        int distance = getDistance();
        long interval = getInterval(distance);
        final Handler handler = new Handler();
        if (interval > 0) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 100ms
                    checkForCurrentLocation();
                }
            }, interval);
        }

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> fromLocation = geocoder.getFromLocation(latitude, longitude, 1);
            Log.e(TAG, fromLocation.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.e(TAG, "Interval: " + interval);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.CURRENT_LOCATION, location);
        bundle.putInt(Constants.DISTANCE, distance);
        bundle.putString(Constants.LAT_LONG_MESSAGE, msg);
        mResultReceiver.send(Constants.RESULT_RECEIVED, bundle);
    }

    private long getInterval(float distance) {
        long interval;
        if (distance > (10 * 1000)) {
            interval = (long) ((60 * 60 * 1000) * distance / (140 * 1000));
        } else if (distance <= 10 * 1000 && distance >= 5 * 1000) {
            interval = (5 * 1000);
        } else if (distance < 5 * 1000 && distance > 100) {
            interval = 1000;
        } else {
            interval = 0;
        }
        return interval;
    }

    private int getDistance() {
        float distance;
        distance = mMyLocation.distanceTo(GpsApplication.sDestinationLocation);
        Log.e(TAG, "Distance: " + distance);
        return (int) distance;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            mResultReceiver = intent.getParcelableExtra(Constants.RECEIVER_TAG);

            if (GpsApplication.sGoogleApiClient == null) {
                GpsApplication.sGoogleApiClient = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();
            }
            GpsApplication.sGoogleApiClient.connect();
        }
        Log.e(TAG, "onStartCommand: ");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        GpsApplication.sGoogleApiClient.disconnect();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
