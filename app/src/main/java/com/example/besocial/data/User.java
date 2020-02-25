package com.example.besocial.data;

import java.util.ArrayList;

public class User {
    private String userFirstName;
    private String userLastName;
    private String userEmail;
    private int age;
    private int bonusPoints;
    private String socialLevel;
    private ArrayList<User> followList;


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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getBonusPoints() {
        return bonusPoints;
    }

    public void setBonusPoints(int bonusPoints) {
        this.bonusPoints = bonusPoints;
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
}
