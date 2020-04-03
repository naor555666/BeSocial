package com.example.besocial.data;

import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Date;

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
    private ArrayList<User> followList;
    private String userProfileImage;

    public User(String userId, String userFirstName, String userLastName, String userEmail, String userAddress, String userCity, String birthday, String socialPoints, String socialLevel, String socialStoreCredits, ArrayList<User> followList) {
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
        this.followList = followList;
    }

    public User() {

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

    public ArrayList<User> getFollowList() {
        return followList;
    }

    public void setFollowList(ArrayList<User> followList) {
        this.followList = followList;
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

    public String getUserProfileImage() {
        return userProfileImage;
    }

    public void setUserProfileImage(String userProfileImage) {
        this.userProfileImage = userProfileImage;
    }
}
