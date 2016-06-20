package com.facticoapp.nuup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.facticoapp.nuup.fragments.DeviceScanFragment;

/**
 * Created by Edgar Z. on 6/20/16.
 */

public class DeviceScanActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    public static String EXTRA_DEVICE_NAME = "device_name";
    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    public static void startActivityForResult(Fragment fragment, int requestCode) {
        Intent intent = new Intent(fragment.getContext(), DeviceScanActivity.class);
        fragment.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_scan);

        mToolbar = (Toolbar) findViewById(R.id.device_scan_toolbar);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            Fragment fragment = DeviceScanFragment.newInstance();
            transaction.replace(R.id.device_scan_container, fragment);
            transaction.commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mToolbar != null) setSupportActionBar(mToolbar);
    }
}
