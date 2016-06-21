package com.facticoapp.nuup.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facticoapp.nuup.R;
import com.facticoapp.nuup.maps.MyGoogleMaps;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;

/**
 * Created by zace3d on 6/21/16.
 */

public class CompanionFragment extends Fragment {

    private SupportMapFragment mMapFragment;
    private MyGoogleMaps mGoogleMaps;

    public static Fragment newInstance() {
        return new CompanionFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_companion, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_map);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Activity activity = getActivity();
        mGoogleMaps = new MyGoogleMaps(activity, mMapFragment);
        mGoogleMaps.setOnMapReadyListener(new MyGoogleMaps.OnMapReadyListener() {
            @Override
            public void onMapReady(GoogleMap map) {
                setUpMapIfNeeded();
            }
        });
    }

    private void setUpMapIfNeeded() {
        if (mGoogleMaps.setUpMap()) {
            mGoogleMaps.initMap();

            //mGoogleMaps.centerInCurrentLocation();
        }
    }
}
