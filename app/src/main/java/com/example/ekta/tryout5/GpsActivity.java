package com.example.ekta.tryout5;


import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class GpsActivity extends AppCompatActivity implements ServiceResultReceiver.Receiver,
        OnMapReadyCallback {

    private static final String TAG = GpsActivity.class.getSimpleName();
    private TextView mLatLong;
    private Location mMyLocation;
    private Intent mServiceIntent;
    private GoogleMap mGoogleMap;
    private Marker mMarker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        mLatLong = (TextView) findViewById(R.id.lat_long);
        initToolBar();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        SupportPlaceAutocompleteFragment autocompleteFragment = (SupportPlaceAutocompleteFragment)
                getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        final AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        final CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams)
                appBarLayout.getLayoutParams();
        params.height = DeviceUtils.getDeviceHeight(this) / 2;
        appBarLayout.setLayoutParams(params);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                setDragCallBack(appBarLayout, params);
            }
        }, 1000);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.i(TAG, "Place name: " + place.getName());
                LatLng latLng = place.getLatLng();
                Log.i(TAG, "Place latlng: " + latLng);
                GpsApplication.sDestinationLocation = new Location("destination");
                GpsApplication.sDestinationLocation.setLatitude(latLng.latitude);
                GpsApplication.sDestinationLocation.setLongitude(latLng.longitude);
                endService();
                callService();

                /*Geocoder geocoder = new Geocoder(GpsActivity.this, Locale.getDefault());

                try {
                    List<Address> fromLocation = geocoder.getFromLocation(latLng.latitude, latLng
                            .longitude, 1);
                    fromLocation.get(0).
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
                getDistance();

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });
    }

    private void endService() {
        if (mServiceIntent != null) {
            stopService(mServiceIntent);
        }
    }

    private void setDragCallBack(AppBarLayout appBarLayout, CoordinatorLayout.LayoutParams params) {
        if (ViewCompat.isLaidOut(appBarLayout)) {
            AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
            if (behavior != null) {
                behavior.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
                    @Override
                    public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                        return false;
                    }
                });
            }
        }
    }

    private float getDistance() {
        float distance = 0;
        if (mMyLocation != null) {
            distance = mMyLocation.distanceTo(GpsApplication.sDestinationLocation);
            Log.i(TAG, "Distance: " + distance);
        } else {
            Log.e(TAG, "My location not initialized: ");
        }
        return distance;
    }

    private void callService() {
        ServiceResultReceiver receiver = new ServiceResultReceiver(new Handler());
        receiver.setReceiver(this);
        mServiceIntent = new Intent(this, GpsService.class);
        mServiceIntent.putExtra(Constants.RECEIVER_TAG, receiver);
        startService(mServiceIntent);
    }

    private void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("");
//        TextView titleView = (TextView) toolbar.findViewById(R.id.tv_toolbar_title);
//        titleView.setText(R.string.app_name);
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
            /*if (mMyLocation != null) {
                LatLng currentLatLong = new LatLng(mMyLocation.getLatitude(), mMyLocation
                .getLongitude());
                if (mGoogleMap != null && mMarker != null) {

                    mMarker.setPosition(currentLatLong);
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(currentLatLong));
                } else {
                    mMarker = mGoogleMap.addMarker(new MarkerOptions().position(currentLatLong)
                            .title("My " +
                            "location"));
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLong));
                }
            }*/

            int distanceToDestination = resultData.getInt(Constants.DISTANCE);
            if (distanceToDestination <= 100) {
                if (mMyLocation != null) {
                    LatLng currentLatLong = new LatLng(mMyLocation.getLatitude(), mMyLocation
                            .getLongitude());
                    if (mGoogleMap != null && mMarker != null) {

                        mMarker.setPosition(currentLatLong);
                        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(currentLatLong));
                    } else {
                        mMarker = mGoogleMap.addMarker(new MarkerOptions().position(currentLatLong)
                                .title("My " +
                                        "location"));
                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLong));
                    }
                }
                endService();
                mLatLong.setText(message + " Distance: " + distanceToDestination + " REACHED!!!");
            } else {
                mLatLong.setText(message + " Distance: " + distanceToDestination);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setPadding(0, DeviceUtils.converDpToPixel(45, this), 0, 0);
        mGoogleMap.setMyLocationEnabled(true);
    }
}
