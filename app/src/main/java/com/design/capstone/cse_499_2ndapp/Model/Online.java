package com.design.capstone.cse_499_2ndapp.Model;

/**
 * Created by Majedur Rahman on 8/6/2017.
 */

public class Online  {

    private String id ;
    private double latitide;
    private  double longitude;

    public Online(String id, double latitide, double longitude) {

        this.id = id;
        this.latitide = latitide;
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getLatitide() {
        return latitide;
    }

    public void setLatitide(double latitide) {
        this.latitide = latitide;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
