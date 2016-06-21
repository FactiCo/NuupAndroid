package com.facticoapp.nuup.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

/**
 * Created by Edgar Z. on 6/21/16.
 */

public class BluetoothBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = BluetoothBroadcastReceiver.class.getName();

    private Callback mCallback;

    private BluetoothBroadcastReceiver(Callback callback) {
        mCallback = callback;
    }

    public static void register(Callback callback, Context c) {
        c.registerReceiver(new BluetoothBroadcastReceiver(callback), getFilter());
    }

    private static IntentFilter getFilter() {
        return new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())) {
            Log.v(TAG, "Received irrelevant broadcast. Disregarding.");
            return;
        }

        //This is a State Change event, get the state extra, falling back to ERROR
        //if it isn't there (which shouldn't happen)
        int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

        switch (state) {
            case BluetoothAdapter.STATE_CONNECTED:
                safeUnregisterReceiver(context, this);
                fireOnBluetoothConnected();
                break;
            case BluetoothAdapter.ERROR:
                safeUnregisterReceiver(context, this);
                fireOnBluetoothError();
                break;
        }
    }

    private static void safeUnregisterReceiver(Context c, BroadcastReceiver receiver) {
        try {
            c.unregisterReceiver(receiver);
        } catch (IllegalArgumentException ex) {
            Log.w(TAG, "Tried to unregister BluetoothBroadcastReceiver that was not registered.");
        }
    }

    private void fireOnBluetoothConnected() {
        if (mCallback != null) {
            mCallback.onBluetoothConnected();
        }
    }

    private void fireOnBluetoothError() {
        if (mCallback != null) {
            mCallback.onBluetoothError();
        }
    }

    public interface Callback {
        void onBluetoothConnected();

        void onBluetoothError();
    }
}
