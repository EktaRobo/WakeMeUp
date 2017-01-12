package com.example.ekta.tryout5;


import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends AppCompatActivity implements ServiceResultReceiver.Receiver{

    private static final String TAG = MainActivity.class.getSimpleName();
    private TextView mLatLong;
    private ServiceResultReceiver mReceiver;
    private Location mMyLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLatLong = (TextView) findViewById(R.id.lat_long);
        mReceiver = new ServiceResultReceiver(new Handler());
        mReceiver.setReceiver(this);
        Intent serviceIntent = new Intent(this, GpsService.class);
        serviceIntent.putExtra(Constants.RECEIVER_TAG, mReceiver);
        startService(serviceIntent);

        SupportPlaceAutocompleteFragment autocompleteFragment = (SupportPlaceAutocompleteFragment)
                getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place name: " + place.getName());
                LatLng latLng = place.getLatLng();
                Log.i(TAG, "Place latlng: " + latLng);
                Location destinationLocation = new Location("destination");
                destinationLocation.setLatitude(latLng.latitude);
                destinationLocation.setLongitude(latLng.longitude);
                if (mMyLocation != null) {
                    float distance = mMyLocation.distanceTo(destinationLocation);
                    Log.i(TAG, "Distance: " + distance);
                } else {
                    Log.e(TAG, "My location not initialized: ");
                }

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 0 :
        }
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        if (resultData != null && resultCode == Constants.RESULT_RECEIVED) {
            String message = resultData.getString(Constants.LAT_LONG_MESSAGE);
            mMyLocation = resultData.getParcelable(Constants.CURRENT_LOCATION);
            mLatLong.setText(message);
        }
    }
}
