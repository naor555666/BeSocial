package com.example.besocial.data;

import android.graphics.drawable.Drawable;

public class RedeemableBenefit {
    private String name;
    private String description;
    private String category;
    private Long cost;
    private String minimumSocialLevel;
    private String benefitPhoto;

    public RedeemableBenefit() {

    }

    public RedeemableBenefit(String name, String description, String category, String minimumSocialLevel, Long cost, String benefitPhoto) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.minimumSocialLevel=minimumSocialLevel;
        this.cost = cost;
        this.benefitPhoto = benefitPhoto;
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

    public Long getCost() {
        return cost;
    }

    public void setCost(Long cost) {
        this.cost = cost;
    }

    public String getBenefitPhoto() {
        return benefitPhoto;
    }

    public void setBenefitPhoto(String benefitPhoto) {
        this.benefitPhoto = benefitPhoto;
    }

    public String getMinimumSocialLevel() {
        return minimumSocialLevel;
    }

    public void setMinimumSocialLevel(String minimumSocialLevel) {
        this.minimumSocialLevel = minimumSocialLevel;
    }
}
