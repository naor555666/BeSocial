package com.example.besocial.data;

import com.example.besocial.LatLng;



public class User {
    private String userId;
    private String userFirstName;
    private String userLastName;
    private String userEmail;
    private String userAddress;
    private String userCity;
    private String birthday;
    private String socialPoints;
    private String socialLevel;
    private String socialStoreCredits;
    private boolean isManager=false;
    private String profileImage;
    private LatLng userCurrentPosition;



    public User() {
    }

    public User(String userId, String userFirstName, String userLastName,
                String userEmail, String userAddress, String userCity,
                String birthday, String socialPoints, String socialLevel,
                String socialStoreCredits, boolean isManager, String profileImage) {
        this.userId = userId;
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
        this.userEmail = userEmail;
        this.userAddress = userAddress;
        this.userCity = userCity;
        this.birthday = birthday;
        this.socialPoints = socialPoints;
        this.socialLevel = socialLevel;
        this.socialStoreCredits = socialStoreCredits;
        this.isManager = isManager;
        this.profileImage = profileImage;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getSocialPoints() {
        return socialPoints;
    }

    public void setSocialPoints(String socialPoints) {
        this.socialPoints = socialPoints;
    }

    public String getSocialStoreCredits() {
        return socialStoreCredits;
    }

    public void setSocialStoreCredits(String socialStoreCredits) {
        this.socialStoreCredits = socialStoreCredits;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getSocialLevel() {
        return socialLevel;
    }

    public void setSocialLevel(String socialLevel) {
        this.socialLevel = socialLevel;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getUserCity() {
        return userCity;
    }

    public void setUserCity(String userCity) {
        this.userCity = userCity;
    }

    public boolean isManager() {
        return isManager;
    }

    public void setManager(boolean manager) {
        isManager = manager;
    }

    public LatLng getUserCurrentPosition() {
        return userCurrentPosition;
    }

    public void setUserCurrentPosition(LatLng userCurrentPosition) {
        this.userCurrentPosition = userCurrentPosition;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
