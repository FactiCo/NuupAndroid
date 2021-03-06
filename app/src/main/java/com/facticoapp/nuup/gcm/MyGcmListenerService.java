package com.facticoapp.nuup.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.facticoapp.nuup.CompanionActivity;
import com.facticoapp.nuup.MainActivity;
import com.facticoapp.nuup.R;
import com.facticoapp.nuup.dialogues.Dialogues;
import com.google.android.gms.gcm.GcmListenerService;

/**
 * Created by Edgar Z. on 6/20/16.
 */
public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = MyGcmListenerService.class.getName();

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        Dialogues.Log(TAG, "From: " + from, Log.DEBUG);
        Dialogues.Log(TAG, "Message: " + message, Log.DEBUG);

        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }

        sendNotification(message);
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param alert GCM message received.
     */
    private void sendNotification(String alert) {
        Intent intent = new Intent(this, CompanionActivity.class);
        intent.putExtra("alert", alert);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Nuup notification")
                .setContentText(alert)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}