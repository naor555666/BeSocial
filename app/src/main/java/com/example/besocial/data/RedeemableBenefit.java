package com.example.besocial.data;

import android.graphics.drawable.Drawable;

public class RedeemableBenefit {
    private String name;
    private String description;
    private String category;
    private String cost;
    //private String minimumSocialLevel;
    private Drawable benefitPhoto;


    public RedeemableBenefit(){

    }
    public RedeemableBenefit(String companyName, String benefitDescription, String pointsToRedeem,String category,Drawable benefitPhoto) {
        this.name = companyName;
        this.description = benefitDescription;
        this.cost = pointsToRedeem;
        this.category=category;
        this.benefitPhoto=benefitPhoto;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public Drawable getBenefitPhoto() {
        return benefitPhoto;
    }

    public void setBenefitPhoto(Drawable benefitPhoto) {
        this.benefitPhoto = benefitPhoto;
    }
}
