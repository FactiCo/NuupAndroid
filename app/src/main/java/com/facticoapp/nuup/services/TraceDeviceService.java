package com.facticoapp.nuup.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.facticoapp.nuup.dialogues.Dialogues;
import com.facticoapp.nuup.httpconnection.HttpConnection;
import com.facticoapp.nuup.models.TraceAzure;
import com.facticoapp.nuup.parser.GsonParser;
import com.facticoapp.nuup.preferences.PreferencesManager;
import com.google.android.gms.maps.model.LatLng;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Edgar Z. on 6/21/16.
 */

public class TraceDeviceService extends Service {
    private static final String TAG = TraceDeviceService.class.getName();

    private boolean isTracking = true;

    private Timer mTimer;

    public static void startService(Context context, boolean isTracking) {
        Intent service = new Intent(context, TraceDeviceService.class);
        service.putExtra("isTracking", isTracking);
        context.startService(service);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isTracking = intent.getBooleanExtra("isTracking", false);
        Dialogues.Log(TAG, "TRACE onStartCommand: " + isTracking, Log.ERROR);

        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (isTracking) {
                    LatLng location = PreferencesManager.getLocationPreference(getApplication());
                    String userId = PreferencesManager.getString(getApplication(), PreferencesManager.USER_ID_AZURE);
                    if (location != null && userId != null) {
                        TraceAzure traceAzure = new TraceAzure(userId, location);
                        Message message = new Message();
                        message.obj = traceAzure;
                        mHandler.sendMessage(message);
                    }
                } else {
                    mTimer.cancel();
                    stopSelf();
                }
            }
        }, 0, 10000);//put here time 10000 milliseconds=10 seconds

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mTimer != null)
            mTimer.cancel();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            TraceAzure traceAzure = (TraceAzure) msg.obj;

            String json = GsonParser.createJsonFromObject(traceAzure);
            //String result = HttpConnection.POST(HttpConnection.REPORTS_AZURE, json);
            Dialogues.Log(TAG, "Ruuun: " + json, Log.ERROR);
        }
    };

    private static final int TWO_MINUTES = 1000 * 60 * 2;

    /**
     * Determines whether one Location reading is better than the current Location fix
     *
     * @param location            The new Location that you want to evaluate
     * @param currentBestLocation The current Location fix, to which you want to compare the new one
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /**
     * Checks whether two providers are the same
     */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
