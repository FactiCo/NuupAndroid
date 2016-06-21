package com.facticoapp.nuup.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by Edgar Z. on 6/21/16.
 */

public class Report implements Serializable {
    private long id;
    @Expose private long device_id;
    @Expose private double latitude;
    @Expose private double longitude;

    public Report(long device_id, double latitude, double longitude) {
        this.device_id = device_id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Report(long device_id, LatLng location) {
        this.device_id = device_id;
        this.latitude = location.latitude;
        this.longitude = location.longitude;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getDevice_id() {
        return device_id;
    }

    public void setDevice_id(long device_id) {
        this.device_id = device_id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
