package com.facticoapp.nuup.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.facticoapp.nuup.R;
import com.facticoapp.nuup.dialogues.Dialogues;
import com.facticoapp.nuup.httpconnection.HttpConnection;
import com.facticoapp.nuup.models.Device;
import com.facticoapp.nuup.parser.GsonParser;
import com.facticoapp.nuup.preferences.PreferencesManager;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;

/**
 * Created by Edgar Z. on 6/20/16.
 */
public class RegistrationIntentService extends IntentService {

    private static final String TAG = RegistrationIntentService.class.getName();
    private static final String[] TOPICS = {"global"};

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            // [START register_for_gcm]
            // [START get_token]
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            // [END get_token]
            Dialogues.Log(TAG, "GCM Registration Token: " + token, Log.INFO);

            sendRegistrationToServer(token);

            // Subscribe to topic channels
            subscribeTopics(token);

            PreferencesManager.putBoolean(getApplication(), PreferencesManager.SENT_TOKEN_TO_SERVER, true);
            // [END register_for_gcm]
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            PreferencesManager.putBoolean(getApplication(), PreferencesManager.SENT_TOKEN_TO_SERVER, false);
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(PreferencesManager.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    /**
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        LatLng location = PreferencesManager.getLocationPreference(getApplication());
        Device device = new Device(token, location);
        String json = GsonParser.createJsonFromObjectWithExposeAnnotations(device);
        Dialogues.Log(TAG, json, Log.ERROR);
        String result = HttpConnection.POST(HttpConnection.DEVICES, json);
        Dialogues.Log(TAG, result, Log.ERROR);

        handleResult(result);
    }

    private void handleResult(String result) {
        Device device = (Device) GsonParser.getObjectFromJSON(result, Device.class);

        if (device != null) {
            PreferencesManager.putLong(getApplication(), PreferencesManager.DEVICE_ID, device.getId());
        }
    }

    /**
     * @param token GCM token
     * @throws IOException if unable to reach the GCM PubSub service
     */
    // [START subscribe_topics]
    private void subscribeTopics(String token) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : TOPICS) {
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }
    // [END subscribe_topics]
}