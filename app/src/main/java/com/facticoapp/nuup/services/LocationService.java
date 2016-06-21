package com.facticoapp.nuup.services;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.facticoapp.nuup.dialogues.Dialogues;
import com.facticoapp.nuup.location.LocationClientListener;
import com.facticoapp.nuup.preferences.PreferencesManager;

/**
 * Created by Edgar Z. on 6/20/16.
 */

public class LocationService extends Service {
    private final String TAG_CLASS = LocationService.class.getName();

    private LocationClientListener clientListener;

    private static final String LOCATION = "location";
    private static final String LOCATION_LAT = "lat";
    private static final String LOCATION_LON = "lon";

    @Override
    public void onCreate() {
        super.onCreate();

        clientListener = new LocationClientListener(getBaseContext());
        clientListener.setOnLocationClientListener(onLocationClientListener);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (clientListener != null) clientListener.connect();

        //return super.onStartCommand(intent, flags, startId);
        return Service.START_STICKY;
    }

    private LocationClientListener.OnLocationClientListener onLocationClientListener = new LocationClientListener.OnLocationClientListener() {
        @Override
        public void onLocationChanged(Location location) {
            Dialogues.Log(TAG_CLASS, "Service Latitude: " + location.getLatitude() + ", Longitude" + location.getLongitude(), Log.ERROR);

            PreferencesManager.putLocationPreference(getApplication(),
                    String.valueOf(location.getLatitude()),
                    String.valueOf(location.getLongitude()));

            sendLocationBroadcast(location);

            stopSelf();
        }
    };

    @Override
    public void onDestroy() {
        clientListener.disconnect();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void sendLocationBroadcast(Location location) {
        Intent intent = new Intent(LocationService.LOCATION);
        intent.putExtra(LOCATION_LAT, String.valueOf(location.getLatitude()));
        intent.putExtra(LOCATION_LON, String.valueOf(location.getLongitude()));
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
