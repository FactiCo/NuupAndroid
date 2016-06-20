package com.facticoapp.nuup.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;

import com.facticoapp.nuup.dialogues.Dialogues;

/**
 * Created by Edgar Z. on 6/20/16.
 */

public class RemoteControlReceiver extends BroadcastReceiver {
    private static final String TAG = RemoteControlReceiver.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
            KeyEvent keyEvent = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);

            Dialogues.Log(TAG, "KeyCode: " + keyEvent, Log.DEBUG);

            if (KeyEvent.KEYCODE_MEDIA_PLAY == keyEvent.getKeyCode()) {

            }
        }
    }
}