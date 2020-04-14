package com.example.besocial.data;

import com.example.besocial.LatLng;
import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Event {
    private String eventId;
    private String strEventPhotoUrl;
    private String eventCategory;
    private String title;
    private String beginDate;
    private String finishDate;
    private String beginTime;
    private String finishTime;
    private LatLng location;
    private String locationTitle;
    private String description;
    private String eventCreatorUid;
    private String eventCreatorUserName;
    private boolean isCompanyManagmentEvent = false;
    private boolean isFinished = false;
    public Event() {
    }

    public Event(String strEventPhotoUrl, String eventCategory, String title, String beginDate
            , String finishDate, String beginTime, String finishTime, LatLng location
            , String locationTitle, String description, String eventCreatorUid
            , String eventCreatorUserName, boolean isCompanyManagmentEvent) {
        this.strEventPhotoUrl = strEventPhotoUrl;
        this.eventCategory = eventCategory;
        this.title = title;
        this.beginDate = beginDate;
        this.finishDate = finishDate;
        this.beginTime = beginTime;
        this.finishTime = finishTime;
        this.location = location;
        this.locationTitle = locationTitle;
        this.description = description;
        this.eventCreatorUid = eventCreatorUid;
        this.eventCreatorUserName = eventCreatorUserName;
        this.isCompanyManagmentEvent = isCompanyManagmentEvent;
    }


    public String getEventCategory() {
        return eventCategory;
    }

    public void setEventCategory(String eventCategory) {
        this.eventCategory = eventCategory;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public String getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(String finishDate) {
        this.finishDate = finishDate;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public String getLocationTitle() {
        return locationTitle;
    }

    public void setLocationTitle(String locationTitle) {
        this.locationTitle = locationTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEventCreatorUid() {
        return eventCreatorUid;
    }

    public void setEventCreatorUid(String eventCreatorUid) {
        this.eventCreatorUid = eventCreatorUid;
    }

    public String getEventCreatorUserName() {
        return eventCreatorUserName;
    }

    public void setEventCreatorUserName(String eventCreatorUserName) {
        this.eventCreatorUserName = eventCreatorUserName;
    }

    public boolean isCompanyManagmentEvent() {
        return isCompanyManagmentEvent;
    }

    public void setCompanyManagmentEvent(boolean companyManagmentEvent) {
        isCompanyManagmentEvent = companyManagmentEvent;
    }

    public String getStrEventPhotoUrl() {
        return strEventPhotoUrl;
    }

    public void setStrEventPhotoUrl(String strEventPhotoUrl) {
        this.strEventPhotoUrl = strEventPhotoUrl;
    }
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("strEventPhotoUrl", strEventPhotoUrl);
        result.put("eventCategory", eventCategory);
        result.put("title", title);
        result.put("beginDate", beginDate);
        result.put("finishDate", finishDate);
        result.put("beginTime", beginTime);
        result.put("finishTime", finishTime);
        result.put("location", location);
        result.put("locationTitle", locationTitle);
        result.put("description", description);
        result.put("eventCreatorUid", eventCreatorUid);
        result.put("eventCreatorUserName", eventCreatorUserName);
        result.put("isCompanyManagmentEvent", isCompanyManagmentEvent);

        return result;
    }

    @Override
    public String toString() {
        return "Event{" +
                "strEventPhotoUrl=" + strEventPhotoUrl +
                ", eventCategory='" + eventCategory + '\'' +
                ", title='" + title + '\'' +
                ", beginDate='" + beginDate + '\'' +
                ", finishDate='" + finishDate + '\'' +
                ", beginTime='" + beginTime + '\'' +
                ", finishTime='" + finishTime + '\'' +
                ", location=" + location +
                ", locationTitle='" + locationTitle + '\'' +
                ", description='" + description + '\'' +
                ", eventCreatorUid='" + eventCreatorUid + '\'' +
                ", EventCreatorUserName='" + eventCreatorUserName + '\'' +
                ", isCompanyManagmentEvent=" + isCompanyManagmentEvent +
                '}';
    }
}
