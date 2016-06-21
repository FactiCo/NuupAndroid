package com.facticoapp.nuup.bluetooth;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;
import android.content.Context;

/**
 * Created by Edgar Z. on 6/21/16.
 */

public class BluetoothA2DPRequester implements BluetoothProfile.ServiceListener {
    private Callback mCallback;

    public BluetoothA2DPRequester(Callback callback) {
        mCallback = callback;
    }

    public void request(Context c, BluetoothAdapter adapter) {
        adapter.getProfileProxy(c, this, BluetoothProfile.A2DP);
    }

    @Override
    public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
        if (mCallback != null) {
            mCallback.onA2DPProxyReceived((BluetoothA2dp) bluetoothProfile);
        }
    }

    @Override
    public void onServiceDisconnected(int i) {

    }

    public interface Callback {
        void onA2DPProxyReceived(BluetoothA2dp proxy);
    }
}