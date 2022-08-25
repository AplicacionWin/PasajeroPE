package com.nikola.user.network.Models;

/**
 * Created by user on 2/23/2017.
 */

public class AirportLst {
    private String airport_id,airport_address;
    private double latitude, longitude;

    public String getAirport_id() {
        return airport_id;
    }

    public void setAirport_id(String airport_id) {
        this.airport_id = airport_id;
    }

    public String getAirport_address() {
        return airport_address;
    }

    public void setAirport_address(String airport_address) {
        this.airport_address = airport_address;
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
