package com.facticoapp.nuup.fragments;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facticoapp.nuup.DeviceScanActivity;
import com.facticoapp.nuup.R;
import com.facticoapp.nuup.adapters.DevicesAdapter;

import java.util.Set;

/**
 * Created by Edgar Z. on 6/20/16.
 */

public class DeviceScanFragment extends Fragment {
    private static final String TAG = DeviceScanFragment.class.getName();

    private RecyclerView mPairedDevices;
    private RecyclerView mNewDevices;
    //private Button mScanButton;
    private TextView mPairedLabel;
    private TextView mNewLabel;

    private BluetoothAdapter mBluetoothAdapter;

    private DevicesAdapter mPairedDevicesAdapter;
    private DevicesAdapter mNewDevicesAdapter;

    private boolean mScanning;

    private static final int REQUEST_ENABLE_BT = 3;

    public static Fragment newInstance() {
        return new DeviceScanFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device_scan, container, false);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mPairedDevices = (RecyclerView) view.findViewById(R.id.device_scan_paired_list);
        mNewDevices = (RecyclerView) view.findViewById(R.id.device_scan_new_list);
        //mScanButton = (Button) view.findViewById(R.id.device_scan_scan_button);

        mPairedLabel = (TextView) view.findViewById(R.id.device_scan_paired_label);
        mNewLabel = (TextView) view.findViewById(R.id.device_scan_new_label);

        Activity activity = getActivity();

        // Initialize array adapters. One for already paired devices and
        // one for newly discovered devices
        mPairedDevicesAdapter = new DevicesAdapter(activity);
        mNewDevicesAdapter = new DevicesAdapter(activity);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final Activity activity = getActivity();

        /*mScanButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mScanning = true;
                doDiscovery();
                if (activity != null) activity.invalidateOptionsMenu();
                //v.setVisibility(View.GONE);
            }
        });*/

        // Find and set up the RecyclerView for paired devices
        LinearLayoutManager mPairedLinearLayoutManager = new LinearLayoutManager(activity);
        mPairedLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mPairedDevices.setLayoutManager(mPairedLinearLayoutManager);
        mPairedDevices.setAdapter(mPairedDevicesAdapter);
        mPairedDevicesAdapter.setOnItemClickListener(mPairedDeviceOnClickListener);

        // Find and set up the RecyclerView for newly discovered devices
        LinearLayoutManager mNewLinearLayoutManager = new LinearLayoutManager(activity);
        mNewLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mNewDevices.setLayoutManager(mNewLinearLayoutManager);
        mNewDevices.setAdapter(mNewDevicesAdapter);
        mNewDevicesAdapter.setOnItemClickListener(mNewDeviceOnClickListener);

        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        activity.registerReceiver(mReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        activity.registerReceiver(mReceiver, filter);

        // Get the local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        findBondedDevices();

        mScanning = true;
        doDiscovery();
        activity.invalidateOptionsMenu();
    }

    private void findBondedDevices() {
        // Get a set of currently paired devices
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0) {
            mPairedLabel.setVisibility(View.VISIBLE);
            for (BluetoothDevice device : pairedDevices) {
                mPairedDevicesAdapter.addItem(device);
            }
        } else {
            String noDevices = getResources().getText(R.string.none_paired).toString();
            //mPairedDevicesAdapter.addItem();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Make sure we're not doing discovery anymore
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.cancelDiscovery();
        }

        Activity activity = getActivity();
        if (activity == null)
            return;

        // Unregister broadcast listeners
        activity.unregisterReceiver(mReceiver);
    }

    /**
     * Start device discover with the BluetoothAdapter
     */
    private void doDiscovery() {
        Activity activity = getActivity();

        if (activity != null)
            // Indicate scanning in the title
            activity.setProgressBarIndeterminateVisibility(true);

        if (mNewDevicesAdapter.getItemCount() > 0)
            mNewDevicesAdapter.removeItems();

        // Turn on sub-title for new devices
        mNewLabel.setVisibility(View.VISIBLE);

        cancelDiscovery();

        // Request discover from BluetoothAdapter
        mBluetoothAdapter.startDiscovery();
    }

    private void cancelDiscovery() {
        // If we're already discovering, stop it
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
    }

    /**
     * The on-click listener for all devices in the RecyclerView
     */
    private DevicesAdapter.OnItemClickListener mPairedDeviceOnClickListener
            = new DevicesAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            handleOnItemClick(view, position, "paired");
        }
    };

    private DevicesAdapter.OnItemClickListener mNewDeviceOnClickListener
            = new DevicesAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            handleOnItemClick(view, position, "new");
        }
    };

    private void handleOnItemClick(View view, int position, String type) {
        BluetoothDevice item = null;
        if (type.equals("paired")) {
            item = mPairedDevicesAdapter.getItem(position);
        } else if (type.equals("new")) {
            item = mNewDevicesAdapter.getItem(position);
        }

        if (item == null)
            return;

        // Cancel discovery because it's costly and we're about to connect
        mScanning = false;
        cancelDiscovery();

        // Get the device MAC address
        Activity activity = getActivity();
        if (activity == null)
            return;

        String name = item.getName();
        String address = item.getAddress();

        // Create the result Intent and include the MAC address
        Intent intent = new Intent();
        intent.putExtra(DeviceScanActivity.EXTRA_DEVICE_NAME, name);
        intent.putExtra(DeviceScanActivity.EXTRA_DEVICE_ADDRESS, address);

        // Set result and finish this Activity
        activity.setResult(Activity.RESULT_OK, intent);
        activity.finish();
    }

    /**
     * The BroadcastReceiver that listens for discovered devices and changes the title when
     * discovery is finished
     */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            Activity activity = getActivity();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED && device.getType() != 2) {
                    mNewDevicesAdapter.addItem(device);
                }
                // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                if (activity != null) {
                    activity.setProgressBarIndeterminateVisibility(false);
                    activity.invalidateOptionsMenu();
                }

                if (mNewDevicesAdapter.getItemCount() == 0) {
                    String noDevices = getResources().getText(R.string.none_found).toString();
                    //mNewDevicesAdapter.addItem(noDevices);
                }
            }
        }
    };

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);

        if (!mScanning) {
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(true);
            menu.findItem(R.id.menu_refresh).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(null);
        } else {
            menu.findItem(R.id.menu_stop).setVisible(true);
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(
                    R.layout.actionbar_indeterminate_progress);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Activity activity = getActivity();
        switch (item.getItemId()) {
            case R.id.menu_scan:
                mScanning = true;
                doDiscovery();
                if (activity != null) activity.invalidateOptionsMenu();
                break;
            case R.id.menu_stop:
                mScanning = false;
                cancelDiscovery();
                if (activity != null) activity.invalidateOptionsMenu();
                break;
        }
        return true;
    }
}
