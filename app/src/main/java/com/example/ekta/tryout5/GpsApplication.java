package com.example.ekta.tryout5;

import android.app.Application;
import android.location.Location;

import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by ekta on 13/1/17.
 */

public class GpsApplication extends Application {
    public static GoogleApiClient sGoogleApiClient;
    public static Location sDestinationLocation;
}
