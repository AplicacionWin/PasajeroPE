package com.nikola.user.network.Models;

public class FavProvider {

    private String id;
    private String providerId;
    private String userFavProviderId;
    private String name;
    private String proUrl;
    private int rating;
    private String mobile;


    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUserFavProviderId(String userFavProviderId) {
        this.userFavProviderId = userFavProviderId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProUrl(String proUrl) {
        this.proUrl = proUrl;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getUserFavProviderId() {
        return userFavProviderId;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getProUrl() {
        return proUrl;
    }

    public int getRating() {
        return rating;
    }
}
