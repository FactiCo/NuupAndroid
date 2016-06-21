package com.facticoapp.nuup.models;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by Edgar Z. on 6/21/16.
 */

public class ReportAzure implements Serializable {
    @Expose
    private long UserID;
    @Expose
    private String Device = "android";
    @Expose
    private String Token;

    public ReportAzure(String Token) {
        Token = Token;
    }

    public long getId() {
        return UserID;
    }

    public void setId(long UserID) {
        this.UserID = UserID;
    }

    public String getDevice() {
        return Device;
    }

    public void setDevice(String device) {
        Device = device;
    }

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }
}
