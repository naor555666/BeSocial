package com.example.besocial.data;

import android.graphics.drawable.Drawable;

public class RedeemableBenefit {
    private String companyName;
    private String benefitDescription;
    private int pointsToRedeem;
    private String minimumSocialLevel;
    private Drawable benefitPicture;

    public RedeemableBenefit(String companyName, String benefitDescription, int pointsToRedeem, String minimumSocialLevel, Drawable benefitPicture) {
        this.companyName = companyName;
        this.benefitDescription = benefitDescription;
        this.pointsToRedeem = pointsToRedeem;
        this.minimumSocialLevel = minimumSocialLevel;
        this.benefitPicture = benefitPicture;
    }

}
