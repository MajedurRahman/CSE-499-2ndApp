package com.design.capstone.cse_499_2ndapp.Model;

/**
 * Created by Majedur Rahman on 8/6/2017.
 */

public class Online  {

    private String id ;
    private double latitude;
    private  double longitude;

    public Online(){


    }

    public Online(String id, double latitude, double longitude) {

        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
