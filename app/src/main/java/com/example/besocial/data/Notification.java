package com.example.besocial.data;

public class Notification {
    private String type;
    private String socialPointsAmount;
    private String IdToNavigate;

    public Notification() {
    }

    public Notification(String type, String socialPointsAmount, String idToNavigate) {
        this.type = type;
        this.socialPointsAmount = socialPointsAmount;
        IdToNavigate = idToNavigate;
    }

    public Notification(String type, String idToNavigate) {
        this.type = type;
        IdToNavigate = idToNavigate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSocialPointsAmount() {
        return socialPointsAmount;
    }

    public void setSocialPointsAmount(String socialPointsAmount) {
        this.socialPointsAmount = socialPointsAmount;
    }

    public String getIdToNavigate() {
        return IdToNavigate;
    }

    public void setIdToNavigate(String idToNavigate) {
        IdToNavigate = idToNavigate;
    }
}
