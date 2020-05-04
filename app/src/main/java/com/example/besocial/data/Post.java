package com.example.besocial.data;

import android.graphics.drawable.Drawable;

public class Post {
    private Drawable userProfilePicture;
    private String postUserName, postDate, postDescription,category,userId;
    private Long numberOfLikes;
    private Drawable postImage;


    public Post(){

    }
    public Post(String userId, Drawable userProfilePicture, String postUserName, String postDate, String postDescription, String category, Drawable postImage) {
        this.userId=userId;
        this.userProfilePicture = userProfilePicture;
        this.postUserName = postUserName;
        this.postDate = postDate;
        this.postDescription = postDescription;
        this.category = category;
        this.postImage = postImage;
        this.numberOfLikes.valueOf(0);
    }

    public Drawable getUserProfilePicture() {
        return userProfilePicture;
    }

    public void setUserProfilePicture(Drawable userProfilePicture) {
        this.userProfilePicture = userProfilePicture;
    }

    public String getPostUserName() {
        return postUserName;
    }

    public void setPostUserName(String postUserName) {
        this.postUserName = postUserName;
    }

    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }

    public String getPostDescription() {
        return postDescription;
    }

    public void setPostDescription(String postDescription) {
        this.postDescription = postDescription;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Long getNumberOfLikes() {
        return numberOfLikes;
    }

    public void setNumberOfLikes(Long numberOfLikes) {
        this.numberOfLikes = numberOfLikes;
    }

    public Drawable getPostImage() {
        return postImage;
    }

    public void setPostImage(Drawable postImage) {
        this.postImage = postImage;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
