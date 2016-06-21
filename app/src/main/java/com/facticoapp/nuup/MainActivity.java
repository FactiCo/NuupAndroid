package com.facticoapp.nuup;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.facticoapp.nuup.dialogues.Dialogues;
import com.facticoapp.nuup.fragments.MainFragment;
import com.facticoapp.nuup.gcm.RegistrationIntentService;
import com.facticoapp.nuup.models.Report;
import com.facticoapp.nuup.models.ReportAzure;
import com.facticoapp.nuup.preferences.PreferencesManager;
import com.facticoapp.nuup.services.ConnectionsIntentService;
import com.facticoapp.nuup.services.LocationService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();

    private Toolbar mToolbar;

    private BroadcastReceiver mGCMRegistrationBroadcastReceiver;
    private boolean isReceiverRegistered;

    // Location permission
    public static final int REQUEST_GOOGLE_PLAY_SERVICES = 1972;
    private static final String[] LOCATION_PERMISSIONS = {
            android.Manifest.permission.ACCESS_FINE_LOCATION
    };
    private static final int REQUEST_LOCATION_PERMISSIONS = 1;
    private static final String FRAGMENT_DIALOG = "dialog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);

        if (savedInstanceState == null) {
            Fragment fragment = MainFragment.newInstance();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.main_container, fragment);
            transaction.commit();

            startLocationRegistrationService();
        }

        startLocationService();

        //Intent service = new Intent(getApplicationContext(), PanicButtonService.class);
        //startService(service);

        //Intent intent = new Intent(getApplicationContext(), DeviceScanActivity.class);
        //startActivity(intent);

        createGCMBroadcastReceiver();

        // Registering BroadcastReceiver
        registerGCMReceiver();

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }

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

    private void createGCMBroadcastReceiver() {
        mGCMRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                /*boolean sentToken = PreferencesManager.getBoolean(getApplication(), PreferencesManager.SENT_TOKEN_TO_SERVER);
                if (sentToken) {
                    Snackbar.make(mToolbar, getString(R.string.gcm_send_message), Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(mToolbar, getString(R.string.token_error_message), Snackbar.LENGTH_SHORT).show();
                }*/
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
            Dialogues.Toast(getApplicationContext(), "¡Botón de pánico activado!. Una alerta se enviará a los usuarios cercanos", Toast.LENGTH_LONG);

            startPanicButton();
        }

        return super.onKeyDown(keyCode, event);
    }

    private void startPanicButton() {
        long deviceId = PreferencesManager.getLong(getApplication(), PreferencesManager.DEVICE_ID);
        Dialogues.Log(TAG, String.valueOf(deviceId), Log.ERROR);
        if (deviceId != -1) {
            LatLng location = PreferencesManager.getLocationPreference(getApplication());
            Report report = new Report(deviceId, location);
            ConnectionsIntentService.startActionAddNewReport(getApplicationContext(), report);
        }

        String token = PreferencesManager.getString(getApplication(), PreferencesManager.TOKEN);
        Dialogues.Log(TAG, "Token: " + token, Log.ERROR);
        if (token != null) {
            ReportAzure report = new ReportAzure(token);
            ConnectionsIntentService.startActionAddNewReportAzure(getApplicationContext(), report);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode == Activity.RESULT_OK) {
                    startLocationService();
                }
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void startLocationRegistrationService() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int code = api.isGooglePlayServicesAvailable(this);
        if (code == ConnectionResult.SUCCESS) {
            onActivityResult(REQUEST_GOOGLE_PLAY_SERVICES, Activity.RESULT_OK, null);
        } else if (api.isUserResolvableError(code) &&
                api.showErrorDialogFragment(this, code, REQUEST_GOOGLE_PLAY_SERVICES)) {
        } else {
            String str = GoogleApiAvailability.getInstance().getErrorString(code);
            Toast.makeText(this, str, Toast.LENGTH_LONG).show();
        }
    }

    private boolean checkPlayServices() {
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
    }

    private void startLocationService() {
        if (!hasPermissionsGranted(LOCATION_PERMISSIONS)) {
            requestLocationPermissions();
            return;
        }

        Intent locationService = new Intent(this, LocationService.class);
        startService(locationService);
    }

    private boolean shouldShowRequestPermissionRationale(String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                return true;
            }
        }
        return false;
    }

    private void requestLocationPermissions() {
        if (shouldShowRequestPermissionRationale(LOCATION_PERMISSIONS)) {
            ConfirmationDialog.newInstance(getString(R.string.location_permission_request))
                    .show(getFragmentManager(), FRAGMENT_DIALOG);
        } else {
            ActivityCompat.requestPermissions(this, LOCATION_PERMISSIONS, REQUEST_LOCATION_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult");
        if (requestCode == REQUEST_LOCATION_PERMISSIONS) {
            if (grantResults.length == LOCATION_PERMISSIONS.length) {
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        ErrorDialog.newInstance(getString(R.string.location_permission_request))
                                .show(getFragmentManager(), FRAGMENT_DIALOG);
                        break;
                    } else {
                        startLocationService();
                        break;
                    }
                }
            } else {
                ErrorDialog.newInstance(getString(R.string.location_permission_request))
                        .show(getFragmentManager(), FRAGMENT_DIALOG);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private boolean hasPermissionsGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static class ErrorDialog extends DialogFragment {
        private static final String ARG_MESSAGE = "message";

        public static ErrorDialog newInstance(String message) {
            ErrorDialog dialog = new ErrorDialog();
            Bundle args = new Bundle();
            args.putString(ARG_MESSAGE, message);
            dialog.setArguments(args);
            return dialog;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Activity activity = getActivity();
            if (activity == null)
                return null;

            return new AlertDialog.Builder(activity)
                    .setMessage(getArguments().getString(ARG_MESSAGE))
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (isAdded())
                                dismiss();
                        }
                    })
                    .create();
        }
    }

    public static class ConfirmationDialog extends DialogFragment {
        private static final String ARG_MESSAGE = "message";

        public static ConfirmationDialog newInstance(String message) {
            ConfirmationDialog dialog = new ConfirmationDialog();
            Bundle args = new Bundle();
            args.putString(ARG_MESSAGE, message);
            dialog.setArguments(args);
            return dialog;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Activity parent = getActivity();
            return new AlertDialog.Builder(parent)
                    .setMessage(getArguments().getString(ARG_MESSAGE))
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(parent, LOCATION_PERMISSIONS,
                                    REQUEST_LOCATION_PERMISSIONS);
                        }
                    })
                    .setNegativeButton(android.R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (isAdded())
                                        dismiss();
                                }
                            })
                    .create();
        }
    }
}
