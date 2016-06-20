package com.facticoapp.nuup.services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.session.MediaSession;
import android.media.session.MediaSessionManager;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;

import com.facticoapp.nuup.dialogues.Dialogues;

/**
 * Created by Edgar Z. on 6/20/16.
 */

public class PanicButtonService extends Service {
    private static final String TAG = PanicButtonService.class.getName();

    private AudioManager am;
    private ComponentName mComponentName;

    private MediaSession mMediaSession;

    @Override
    public void onCreate() {
        super.onCreate();
        Dialogues.Log(TAG, "onCreate", Log.DEBUG);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Dialogues.Log(TAG, "onStartCommand", Log.DEBUG);

        //mComponentName = new ComponentName(getPackageName(), RemoteControlReceiver.class.getName());
        //am = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);

        //registerMediaButtonEvent();

        startMediaSession();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Dialogues.Log(TAG, "onDestroy", Log.DEBUG);

        if (mMediaSession != null && mMediaSession.isActive())
            mMediaSession.release();
        //unregisterMediaButtonEvent();
    }

    private void startMediaSession() {
        /*MediaSessionManager mManager = (MediaSessionManager) getSystemService(Context.MEDIA_SESSION_SERVICE);
        MediaSession.Token mMediaSessionToken = mMediaSession.getSessionToken();
        mMediaSession = mManager.getActiveSessions();*/
        mMediaSession = new MediaSession(getApplicationContext(), TAG);

        Intent intent = new Intent("com.facticoapp.PANIC_BUTTON");
        PendingIntent pendingSwitchIntent = PendingIntent.getBroadcast(this, 100, intent, 0);
        mMediaSession.setMediaButtonReceiver(pendingSwitchIntent);
        mMediaSession.setCallback(new MediaSession.Callback() {
            @Override
            public boolean onMediaButtonEvent(@NonNull Intent mediaButtonIntent) {
                KeyEvent keyEvent = mediaButtonIntent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);

                Dialogues.Log(TAG, keyEvent + " onMediaButtonEvent called: " + mediaButtonIntent, Log.DEBUG);
                return false;
            }

            @Override
            public void onCustomAction(String action, Bundle extras) {
                super.onCustomAction(action, extras);

                String ac = extras.getString(action);

                Dialogues.Log(TAG, " onCustomAction called: " + ac, Log.DEBUG);
            }

            @Override
            public void onCommand(@NonNull String command, Bundle args, ResultReceiver cb) {
                super.onCommand(command, args, cb);
                Dialogues.Log(TAG, "onCommand called: " + command, Log.DEBUG);
            }

            @Override
            public void onPause() {
                Log.d(TAG, "onPause called (media button pressed)");
                super.onPause();
            }

            @Override
            public void onPlay() {
                Log.d(TAG, "onPlay called (media button pressed)");
                super.onPlay();
            }

            @Override
            public void onStop() {
                Log.d(TAG, "onStop called (media button pressed)");
                super.onStop();
            }
        });

        mMediaSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS | MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);

        PlaybackState state = new PlaybackState.Builder()
                .setActions(PlaybackState.ACTION_PLAY)
                .setState(PlaybackState.STATE_STOPPED, PlaybackState.PLAYBACK_POSITION_UNKNOWN, 0)
                .build();

        mMediaSession.setPlaybackState(state);

        mMediaSession.setActive(true);
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
