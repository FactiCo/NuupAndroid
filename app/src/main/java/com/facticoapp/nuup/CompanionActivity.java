package com.facticoapp.nuup;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.facticoapp.nuup.fragments.CompanionFragment;

/**
 * Created by Edgar Z. on 6/21/16.
 */

public class CompanionActivity extends AppCompatActivity {
    public static final String TAG = CompanionActivity.class.getName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_companion);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            Fragment fragment = CompanionFragment.newInstance();
            transaction.replace(R.id.map_container, fragment);
            transaction.commit();
        }
    }
}
