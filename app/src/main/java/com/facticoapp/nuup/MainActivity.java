package com.facticoapp.nuup;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;

import com.facticoapp.nuup.dialogues.Dialogues;
import com.facticoapp.nuup.fragments.MainFragment;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();

    private Toolbar mToolbar;

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

        /*Intent service = new Intent(getApplicationContext(), PanicButtonService.class);
        startService(service);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);*/
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mToolbar != null) setSupportActionBar(mToolbar);
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
