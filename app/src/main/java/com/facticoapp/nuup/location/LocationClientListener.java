package com.facticoapp.nuup.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.facticoapp.nuup.dialogues.Dialogues;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by Edgar Z. on 6/20/16.
 */

public class LocationClientListener implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private final String TAG = LocationClientListener.class.getName();

    private GoogleApiClient mGoogleApiClient;

    private LocationRequest mLocationRequest;

    private OnLocationClientListener onLocationClientListener;
    private Context context;

    private static final int minimumDistanceBetweenUpdates = 20;

    public LocationClientListener(Context context) {
        this.context = context;
        mGoogleApiClient = new GoogleApiClient.Builder(context).addApi(LocationServices.API).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
    }

    public void connect() {
        mGoogleApiClient.connect();
    }

    public void disconnect() {
        mGoogleApiClient.disconnect();
    }

    protected void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        mLocationRequest.setSmallestDisplacement(minimumDistanceBetweenUpdates);
        mLocationRequest.setInterval(20 * 1000); // Every 20 seconds
        mLocationRequest.setFastestInterval(10 * 1000); // Every 10 seconds
    }

    /* Get last known location */
    public Location getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }

        return LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Dialogues.Log(TAG, "GoogleApiClient connection was successful", Log.INFO);

        createLocationRequest();

        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Dialogues.Log(TAG, "GoogleApiClient connection has been suspend", Log.INFO);

        stopLocationUpdates();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Dialogues.Log(TAG, "GoogleApiClient connection has failed", Log.INFO);
    }

    @Override
    public void onLocationChanged(Location location) {
        //String mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        setOnLocationChanged(location);
    }

    public void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    public void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    private void setOnLocationChanged(Location location) {
        if (onLocationClientListener != null) {
            onLocationClientListener.onLocationChanged(location);
        }
    }

    public void setOnLocationClientListener(OnLocationClientListener onLocationClientListener) {
        this.onLocationClientListener = onLocationClientListener;
    }

    public interface OnLocationClientListener {
        void onLocationChanged(Location location);
    }
}
