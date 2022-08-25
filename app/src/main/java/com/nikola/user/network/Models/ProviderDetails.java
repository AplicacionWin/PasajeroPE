package com.nikola.user.network.Models;


import org.json.JSONObject;

import static com.nikola.user.network.ApiManager.APIConsts.Params;

public class ProviderDetails {

    private String createdAt;
    private String description;
    private String mobile;
    private String picture;
    private String email;
    private String providerName;
    private String location;
    private int numReviews;
    private String joinedDate;

    private String studied;
    private String works;
    private String speaks;
    private String lives;
    private double overAllRatings;

    public double getOverAllRatings() {
        return overAllRatings;
    }

    public void setOverAllRatings(double overAllRatings) {
        this.overAllRatings = overAllRatings;
    }

    public String getStudied() {
        return studied;
    }

    public void setStudied(String studied) {
        this.studied = studied;
    }

    public String getWorks() {
        return works;
    }

    public void setWorks(String works) {
        this.works = works;
    }

    public String getSpeaks() {
        return speaks;
    }

    public void setSpeaks(String speaks) {
        this.speaks = speaks;
    }

    public String getLives() {
        return lives;
    }

    public void setLives(String lives) {
        this.lives = lives;
    }

    public String getJoinedDate() {
        return joinedDate;
    }

    public void setJoinedDate(String joinedDate) {
        this.joinedDate = joinedDate;
    }

    private boolean isVerified;
    private String responseRate;
    private int providerId;
    private int numHomes;

    public int getNumHomes() {
        return numHomes;
    }

    public void setNumHomes(int numHomes) {
        this.numHomes = numHomes;
    }

    private String verifiedInfo;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getNumReviews() {
        return numReviews;
    }

    public void setNumReviews(int numReviews) {
        this.numReviews = numReviews;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public String getResponseRate() {
        return responseRate;
    }

    public void setResponseRate(String responseRate) {
        this.responseRate = responseRate;
    }

    public String getVerifiedInfo() {
        return verifiedInfo == null
                || verifiedInfo.length() == 0 ? "No verified documents found" : verifiedInfo;
    }

    public void setVerifiedInfo(String verifiedInfo) {
        this.verifiedInfo = verifiedInfo;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public int getProviderId() {
        return providerId;
    }

    public void setProviderId(int providerId) {
        this.providerId = providerId;
    }

    public boolean parse(JSONObject providerObj) {
        if (providerObj == null)
            return false;
        setProviderId(providerObj.optInt(Params.PROVIDER_ID));
        setProviderName(providerObj.optString(Params.NAME));
        setPicture(providerObj.optString(Params.PICTURE));
        setEmail(providerObj.optString(Params.EMAIL));
        setMobile(providerObj.optString(Params.MOBILE));
        return true;
    }
}
