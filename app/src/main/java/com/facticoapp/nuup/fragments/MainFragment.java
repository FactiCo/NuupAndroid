package com.facticoapp.nuup.fragments;

import android.app.Activity;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.facticoapp.nuup.DeviceScanActivity;
import com.facticoapp.nuup.R;
import com.facticoapp.nuup.bluetooth.BluetoothA2DPRequester;
import com.facticoapp.nuup.bluetooth.BluetoothBroadcastReceiver;
import com.facticoapp.nuup.dialogues.Dialogues;
import com.facticoapp.nuup.models.Report;
import com.facticoapp.nuup.models.ReportAzure;
import com.facticoapp.nuup.parser.GsonParser;
import com.facticoapp.nuup.preferences.PreferencesManager;
import com.facticoapp.nuup.services.ConnectionsIntentService;
import com.facticoapp.nuup.services.TraceDeviceService;
import com.google.android.gms.maps.model.LatLng;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Edgar Z. on 6/20/16.
 */

public class MainFragment extends Fragment implements BluetoothBroadcastReceiver.Callback, BluetoothA2DPRequester.Callback {
    private static final String TAG = MainFragment.class.getName();

    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    private Button mScanButton;

    private BluetoothAdapter mBluetoothAdapter;

    private String mConnectedDeviceAddress;

    private AddNewReportReceiver mAddReportReceiver = new AddNewReportReceiver();

    public static Fragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mScanButton = (Button) view.findViewById(R.id.main_scan_button);

        // Get the local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mScanButton.setOnClickListener(mReportOnClickListener);

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Activity activity = getActivity();
            Dialogues.Toast(activity, "Bluetooth is not available", Toast.LENGTH_LONG);
            activity.finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        registerAddNewReportReceiver();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mAddReportReceiver != null) {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mAddReportReceiver);
        }
    }

    private void registerAddNewReportReceiver() {
        IntentFilter filter = new IntentFilter(AddNewReportReceiver.ACTION_ADD_NEW_REPORT);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mAddReportReceiver, filter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDeviceA2DP(data);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    //setupConnection();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Dialogues.Log(TAG, "BT not enabled", Log.DEBUG);
                    Toast.makeText(getActivity(), R.string.bt_not_enabled, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private View.OnClickListener mReportOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //DeviceScanActivity.startActivityForResult(MainFragment.this, REQUEST_CONNECT_DEVICE_SECURE);

            long deviceId = PreferencesManager.getLong(getActivity().getApplication(), PreferencesManager.DEVICE_ID);
            Dialogues.Log(TAG, String.valueOf(deviceId), Log.ERROR);
            if (deviceId != -1) {
                LatLng location = PreferencesManager.getLocationPreference(getActivity().getApplication());
                Report report = new Report(deviceId, location);
                //ConnectionsIntentService.startActionAddNewReport(getActivity(), report);
            }

            String token = PreferencesManager.getString(getActivity().getApplication(), PreferencesManager.TOKEN);
            Dialogues.Log(TAG, "Token: " + token, Log.ERROR);
            if (token != null) {
                ReportAzure report = new ReportAzure(token);
                ConnectionsIntentService.startActionAddNewReportAzure(getActivity(), report);
            }
        }
    };

    public class AddNewReportReceiver extends BroadcastReceiver {
        public static final String ACTION_ADD_NEW_REPORT = "com.facticoapp.nuup.receiver.action.ADD_NEW_REPORT";
        public static final String ACTION_ADD_NEW_REPORT_AZURE = "com.facticoapp.nuup.receiver.action.ADD_NEW_REPORT_AZURE";

        @Override
        public void onReceive(Context context, Intent intent) {
            String result = intent.getStringExtra(ConnectionsIntentService.EXTRA_RESULT);

            if (intent.getAction().equals(ACTION_ADD_NEW_REPORT)) {
                boolean hasError = false;
                if (result != null) {
                    Report report = (Report) GsonParser.getObjectFromJSON(result, Report.class);

                    if (report != null) {
                        TraceDeviceService.startService(context, true);
                    } else {
                        hasError = true;
                    }
                } else {
                    hasError = true;
                }
            }
        }
    }

    private void connectDeviceA2DP(Intent data) {
        mConnectedDeviceAddress = data.getExtras().getString(DeviceScanActivity.EXTRA_DEVICE_ADDRESS);
        String mConnectedDeviceName = data.getExtras().getString(DeviceScanActivity.EXTRA_DEVICE_NAME);

        //Already connected, skip the rest
        if (mBluetoothAdapter.isEnabled()) {
            onBluetoothConnected();
            return;
        }

        //Check if we're allowed to enable Bluetooth. If so, listen for a
        //successful enabling
        if (mBluetoothAdapter.enable()) {
            BluetoothBroadcastReceiver.register(this, getActivity());
        } else {
            Log.e(TAG, "Unable to enable Bluetooth. Is Airplane Mode enabled?");
        }
    }

    @Override
    public void onBluetoothError() {
        Log.e(TAG, "There was an error enabling the Bluetooth Adapter.");
    }

    @Override
    public void onBluetoothConnected() {
        Activity activity = getActivity();
        if (activity == null)
            return;

        new BluetoothA2DPRequester(this).request(activity, mBluetoothAdapter);
    }

    @Override
    public void onA2DPProxyReceived(BluetoothA2dp proxy) {
        Method connect = getConnectMethod();
        BluetoothDevice device = findDeviceByAddress(mBluetoothAdapter, mConnectedDeviceAddress);

        //If either is null, just return. The errors have already been logged
        if (connect == null || device == null) {
            return;
        }

        try {
            connect.setAccessible(true);
            connect.invoke(proxy, device);
        } catch (InvocationTargetException ex) {
            Log.e(TAG, "Unable to invoke connect(BluetoothDevice) method on proxy. " + ex.toString());
        } catch (IllegalAccessException ex) {
            Log.e(TAG, "Illegal Access! " + ex.toString());
        }
    }

    private Method getConnectMethod() {
        try {
            return BluetoothA2dp.class.getDeclaredMethod("connect", BluetoothDevice.class);
        } catch (NoSuchMethodException ex) {
            Log.e(TAG, "Unable to find connect(BluetoothDevice) method in BluetoothA2dp proxy.");
            return null;
        }
    }

    private BluetoothDevice findDeviceByAddress(BluetoothAdapter adapter, String address) {
        boolean isValid = adapter.checkBluetoothAddress(address);
        if (!isValid)
            return null;

        return adapter.getRemoteDevice(address);
    }
}
