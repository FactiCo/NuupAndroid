package com.facticoapp.nuup.models;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Created by Edgar Z. on 6/21/16.
 */

public class TraceAzure implements Serializable {
    private String UserID;
    private String Latitude;
    private String Longitude;

    public TraceAzure(String userID, String latitude, String longitude) {
        UserID = userID;
        Latitude = latitude;
        Longitude = longitude;
    }

    public TraceAzure(String userID, LatLng location) {
        UserID = userID;
        Latitude = String.valueOf(location.latitude);
        Longitude = String.valueOf(location.longitude);
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }
}
