package com.facticoapp.nuup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;

import com.facticoapp.nuup.dialogues.Dialogues;
import com.facticoapp.nuup.fragments.MainFragment;
import com.facticoapp.nuup.gcm.RegistrationIntentService;
import com.facticoapp.nuup.preferences.PreferencesManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();

    private Toolbar mToolbar;

    private BroadcastReceiver mGCMRegistrationBroadcastReceiver;
    private boolean isReceiverRegistered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            Fragment fragment = MainFragment.newInstance();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.main_container, fragment);
            transaction.commit();
        }

        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);

        //Intent service = new Intent(getApplicationContext(), PanicButtonService.class);
        //startService(service);

        //Intent intent = new Intent(getApplicationContext(), DeviceScanActivity.class);
        //startActivity(intent);

        createGCMBroadcastReceiver();

        // Registering BroadcastReceiver
        registerGCMReceiver();

        // Start IntentService to register this application with GCM.
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);

        //setVolumeControlStream(AudioManager.STREAM_MUSIC);*/
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerGCMReceiver();

        if (mToolbar != null) setSupportActionBar(mToolbar);
    }

    @Override
    protected void onPause() {
        unregisterGCMReceiver();
        super.onPause();
    }

    /*private boolean checkPlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        REQUEST_GOOGLE_PLAY_SERVICES).show();
            }
            return false;
        }
        return true;
    }*/

    private void createGCMBroadcastReceiver() {
        mGCMRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean sentToken = PreferencesManager.getBoolean(getApplication(), PreferencesManager.SENT_TOKEN_TO_SERVER);
                if (sentToken) {
                    Snackbar.make(mToolbar, getString(R.string.gcm_send_message), Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(mToolbar, getString(R.string.token_error_message), Snackbar.LENGTH_SHORT).show();
                }
            }
        };
    }

    private void unregisterGCMReceiver() {
        if (mGCMRegistrationBroadcastReceiver == null)
            return;

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mGCMRegistrationBroadcastReceiver);
        isReceiverRegistered = false;
    }

    private void registerGCMReceiver() {
        if (mGCMRegistrationBroadcastReceiver == null)
            return;

        if (!isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mGCMRegistrationBroadcastReceiver,
                    new IntentFilter(PreferencesManager.REGISTRATION_COMPLETE));
            isReceiverRegistered = true;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 24 - KEYCODE_VOLUME_UP
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            Dialogues.Log(TAG, "KeyCode: " + keyCode, Log.DEBUG);
        }

        return super.onKeyDown(keyCode, event);
    }
}
