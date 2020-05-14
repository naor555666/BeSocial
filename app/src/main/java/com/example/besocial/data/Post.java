package com.example.besocial.data;


public class Post {
    private String userProfilePicture;
    private String postUserName, postDate, postDescription,category,userId;
    private Long numberOfLikes;
    private String postImage;
    private String postId;


    public Post(){

    }
    public Post(String userId, String userProfilePicture, String postUserName, String postDate, String postDescription, String category, String postImage) {
        this.userId=userId;
        this.userProfilePicture = userProfilePicture;
        this.postUserName = postUserName;
        this.postDate = postDate;
        this.postDescription = postDescription;
        this.category = category;
        this.postImage = postImage;
        this.numberOfLikes.valueOf(0);
    }

    public String getUserProfilePicture() {
        return userProfilePicture;
    }

    public void setUserProfilePicture(String userProfilePicture) {
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

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }
}
