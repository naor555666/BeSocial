package com.example.besocial.data;

import android.graphics.drawable.Drawable;

public class Post {
    private Drawable userProfilePicture;
    private String postUserName, postDate, postDescription;
    private Drawable postImage;

    public Post(Drawable userProfilePicture, String postUserName, String postDate, String postDescription, Drawable postImage) {
        this.userProfilePicture = userProfilePicture;
        this.postUserName = postUserName;
        this.postDate = postDate;
        this.postDescription = postDescription;
        this.postImage = postImage;
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

    public Drawable getPostImage() {
        return postImage;
    }

    public void setPostImage(Drawable postImage) {
        this.postImage = postImage;
    }
}
