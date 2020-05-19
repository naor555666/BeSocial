package com.example.besocial.data;

public class LiteUserDetails {
    private String userProfileImage;
    private String userId;
    private String userFirstName;
    private String userLastName;
    //for event check-in
    private String eventCategory;
    private Boolean companyManagmentEvent;

    public LiteUserDetails() {
    }

    public LiteUserDetails(String userProfileImage, String userId, String userFirstName, String userLastName) {
        this.userProfileImage = userProfileImage;
        this.userId = userId;
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
    }

    public String getUserProfileImage() {
        return userProfileImage;
    }

    public void setUserProfileImage(String userProfileImage) {
        this.userProfileImage = userProfileImage;
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

    public String getEventCategory() {
        return eventCategory;
    }

    public void setEventCategory(String eventCategory) {
        this.eventCategory = eventCategory;
    }

    public Boolean getCompanyManagmentEvent() {
        return companyManagmentEvent;
    }

    public void setCompanyManagmentEvent(Boolean companyManagmentEvent) {
        this.companyManagmentEvent = companyManagmentEvent;
    }
}

