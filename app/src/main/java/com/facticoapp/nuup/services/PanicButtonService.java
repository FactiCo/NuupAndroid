package com.facticoapp.nuup.services;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.facticoapp.nuup.dialogues.Dialogues;
import com.facticoapp.nuup.receivers.RemoteControlReceiver;

/**
 * Created by Edgar Z. on 6/20/16.
 */

public class PanicButtonService extends Service {
    private static final String TAG = PanicButtonService.class.getName();

    private AudioManager am;
    private ComponentName mComponentName;

    @Override
    public void onCreate() {
        super.onCreate();
        Dialogues.Log(TAG, "onCreate", Log.DEBUG);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Dialogues.Log(TAG, "onStartCommand", Log.DEBUG);

        mComponentName = new ComponentName(getPackageName(), RemoteControlReceiver.class.getName());
        am = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);

        registerMediaButtonEvent();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Dialogues.Log(TAG, "onDestroy", Log.DEBUG);

        unregisterMediaButtonEvent();
    }

    private void registerMediaButtonEvent() {
        // Start listening for button presses
        am.registerMediaButtonEventReceiver(mComponentName);
    }

    private void unregisterMediaButtonEvent() {
        // Stop listening for button presses
        am.unregisterMediaButtonEventReceiver(mComponentName);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
