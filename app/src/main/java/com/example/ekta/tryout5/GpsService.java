package com.example.ekta.tryout5;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
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

import static com.google.android.gms.internal.zzs.TAG;

public class GpsService extends Service implements GoogleApiClient
        .OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, LocationListener {
    private GoogleApiClient mGoogleApiClient;
    private Location mMyLocation;
    private ResultReceiver mResultReceiver;


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
        /*String accessFineLocation = Manifest.permission.ACCESS_FINE_LOCATION;
        if (ActivityCompat.checkSelfPermission(this, accessFineLocation) !=
                PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{accessFineLocation}, 1);
            }
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                .getCurrentPlace(mGoogleApiClient, null);
        result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
            @Override
            public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
                for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                    Log.i(TAG, String.format("Place '%s' has likelihood: %g",
                            placeLikelihood.getPlace().getName(),
                            placeLikelihood.getLikelihood()));
                }
                likelyPlaces.release();
            }
        });*/
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000 * 5);
        PendingResult<Status> statusPendingResult = LocationServices.FusedLocationApi
                .requestLocationUpdates(mGoogleApiClient, locationRequest, this);

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
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.CURRENT_LOCATION, location);
        bundle.putString(Constants.LAT_LONG_MESSAGE, msg);
        mResultReceiver.send(Constants.RESULT_RECEIVED, bundle);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mResultReceiver = intent.getParcelableExtra(Constants.RECEIVER_TAG);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mGoogleApiClient.connect();
        Log.e(TAG, "onStartCommand: ");
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
