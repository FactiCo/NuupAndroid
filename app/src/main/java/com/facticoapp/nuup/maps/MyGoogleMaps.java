package com.facticoapp.nuup.maps;

import android.content.Context;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edgar Z. on 6/21/16.
 */

public class MyGoogleMaps implements OnMapReadyCallback {
    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private Context context;
    private List<Marker> markers = new ArrayList<>();

    public MyGoogleMaps(Context context, SupportMapFragment mapFragment) {
        this.context = context;
        this.mapFragment = mapFragment;

        mapFragment.getMapAsync(this);
    }

    public void initMap() {
        map.setBuildingsEnabled(true);
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.getUiSettings().setZoomControlsEnabled(false); //ZOOM
        map.getUiSettings().setCompassEnabled(true); //COMPASS
        map.getUiSettings().setZoomGesturesEnabled(true); //GESTURES ZOOM
        map.getUiSettings().setRotateGesturesEnabled(true); //ROTATE GESTURES
        map.getUiSettings().setScrollGesturesEnabled(true); //SCROLL GESTURES
        map.getUiSettings().setTiltGesturesEnabled(true); //TILT GESTURES
    }

    public boolean setUpMap() {
        return checkReady();
    }

    private boolean checkReady() {
        return map != null;
    }

    public void clear() {
        if (map != null) map.clear();
        if (markers != null && markers.size() > 0) markers.clear();
    }

    public void centerInCurrentLocation(LatLng location, String title, String message, BitmapDescriptor icon) {
        if (location != null) {
            Marker marker = createMarker(
                    map,
                    location,
                    title,
                    false,
                    message,
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

            if (marker != null) {
                markers.add(marker);
            }
        }
    }

    public void addMarker(LatLng location, String title, String message, BitmapDescriptor icon, boolean dragabble) {
        if (location != null) {
            Marker marker = createMarker(
                    map,
                    location,
                    title,
                    dragabble,
                    message,
                    icon);

            if (marker != null) {
                markers.add(marker);
            }
        }
    }

    public void moveCamera(LatLng location, boolean zoom, int zoomRatio) {
        if (zoom)
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, map.getMaxZoomLevel() - zoomRatio));
        else
            map.moveCamera(CameraUpdateFactory.newLatLng(location));
    }

    public Marker createMarker(GoogleMap map, LatLng point, String title, boolean draggable, String snippet, BitmapDescriptor icon) {
        return map.addMarker(new MarkerOptions()
                .position(point)
                .title(title)
                .draggable(draggable)
                .icon(icon)
                .snippet(snippet)
                .alpha(1.0f)
                .anchor(0.5F, 0.5F)
                .rotation(0.0F));
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;

        setOnMapReady(map);
    }

    // OnMapReadyListener
    private OnMapReadyListener onMapReadyListener;

    public void setOnMapReadyListener(OnMapReadyListener onMapReadyListener) {
        this.onMapReadyListener = onMapReadyListener;
    }

    private void setOnMapReady(GoogleMap map) {
        if (onMapReadyListener != null)
            onMapReadyListener.onMapReady(map);
    }

    public interface OnMapReadyListener {
        void onMapReady(GoogleMap map);
    }
}
