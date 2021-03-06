package com.facticoapp.nuup.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by Edgar Z. on 6/20/16.
 */

public class Device implements Serializable {
    private long id;
    @Expose private String provider = "android";
    @Expose private String token;
    @Expose private double latitude;
    @Expose private double longitude;

    public Device(String token, double latitude, double longitude) {
        this.token = token;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Device(String token, LatLng location) {
        this.token = token;
        this.latitude = location.latitude;
        this.longitude = location.longitude;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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
