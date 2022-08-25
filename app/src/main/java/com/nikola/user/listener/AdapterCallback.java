package com.nikola.user.listener;

/**
 * Created by Carlos on 7/7/2017.
 */

public interface AdapterCallback {
    void onMethodCallback(String id, String taxitype, String taxi_price_distance, String taxi_price_min, String taxiimage, String taxi_seats, String basefare);
}
