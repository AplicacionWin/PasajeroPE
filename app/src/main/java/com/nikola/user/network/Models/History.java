package com.nikola.user.network.Models;

/**
 * Created by Carlos on 1/20/2017.
 */

public class History {
    private String requestId, map_image,cancelReason;
    private String history_date, provider_name, history_picture, history_total, history_type, history_Sadd, history_Dadd;
    private String total_time, base_price, time_price, distance_travel, tax_price, distance_price;
    private String min_price, booking_fee, currnecy_unit, distance_unit, providerPicture;
    private double longitude, latitude;
    private String requestUniqueId;
    private int request_ico_status;
    private String request_icon_status_text;

    public int getRequest_ico_status() {
        return request_ico_status;
    }

    public void setRequest_ico_status(int request_ico_status) {
        this.request_ico_status = request_ico_status;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public String getRequest_icon_status_text() {
        return request_icon_status_text;
    }

    public void setRequest_icon_status_text(String request_icon_status_text) {
        this.request_icon_status_text = request_icon_status_text;
    }

    public String getRequestUniqueId() {
        return requestUniqueId;
    }

    public void setRequestUniqueId(String requestUniqueId) {
        this.requestUniqueId = requestUniqueId;
    }

    public String getProviderPicture() {
        return providerPicture;
    }

    public void setProviderPicture(String providerPicture) {
        this.providerPicture = providerPicture;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getHistory_date() {
        return history_date;
    }

    public void setHistory_date(String history_date) {
        this.history_date = history_date;
    }

    public String getProvider_name() {
        return provider_name;
    }

    public void setProvider_name(String provider_name) {
        this.provider_name = provider_name;
    }

    public String getHistory_picture() {
        return history_picture;
    }

    public void setHistory_picture(String history_picture) {
        this.history_picture = history_picture;
    }

    public String getHistory_total() {
        return history_total;
    }

    public void setHistory_total(String history_total) {
        this.history_total = history_total;
    }

    public String getHistory_type() {
        return history_type;
    }

    public void setHistory_type(String history_type) {
        this.history_type = history_type;
    }

    public String getHistory_Sadd() {
        return history_Sadd;
    }

    public void setHistory_Sadd(String history_Sadd) {
        this.history_Sadd = history_Sadd;
    }

    public String getHistory_Dadd() {
        return history_Dadd;
    }

    public void setHistory_Dadd(String history_Dadd) {
        this.history_Dadd = history_Dadd;
    }

    public String getMap_image() {
        return map_image;
    }

    public void setMap_image(String map_image) {
        this.map_image = map_image;
    }

    public String getTotal_time() {
        return total_time;
    }

    public void setTotal_time(String total_time) {
        this.total_time = total_time;
    }

    public String getBase_price() {
        return base_price;
    }

    public void setBase_price(String base_price) {
        this.base_price = base_price;
    }

    public String getTime_price() {
        return time_price;
    }

    public void setTime_price(String time_price) {
        this.time_price = time_price;
    }

    public String getDistance_travel() {
        return distance_travel;
    }

    public void setDistance_travel(String distance_travel) {
        this.distance_travel = distance_travel;
    }

    public String getTax_price() {
        return tax_price;
    }

    public void setTax_price(String tax_price) {
        this.tax_price = tax_price;
    }

    public String getDistance_price() {
        return distance_price;
    }

    public void setDistance_price(String distance_price) {
        this.distance_price = distance_price;
    }

    public String getBooking_fee() {
        return booking_fee;
    }

    public void setBooking_fee(String booking_fee) {
        this.booking_fee = booking_fee;
    }

    public String getMin_price() {
        return min_price;
    }

    public void setMin_price(String min_price) {
        this.min_price = min_price;
    }

    public String getCurrnecy_unit() {
        return currnecy_unit;
    }

    public void setCurrnecy_unit(String currnecy_unit) {
        this.currnecy_unit = currnecy_unit;
    }

    public String getDistance_unit() {
        return distance_unit;
    }

    public void setDistance_unit(String distance_unit) {
        this.distance_unit = distance_unit;
    }
}
