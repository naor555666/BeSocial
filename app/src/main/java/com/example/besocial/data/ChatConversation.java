package com.example.besocial.data;

import android.graphics.drawable.Drawable;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatConversation {
    private String sentFrom,sentTo;
    private String chatId;
    private String sentToProfilePicture;


    public ChatConversation(){

    }

    public ChatConversation(String sentFrom, String sentTo,String chatId, String sentToProfilePicture) {
        this.sentFrom = sentFrom;
        this.sentTo = sentTo;
        this.chatId=chatId;
        this.sentToProfilePicture = sentToProfilePicture;
    }

    public String getSentFrom() {
        return sentFrom;
    }

    public void setSentFrom(String sentFrom) {
        this.sentFrom = sentFrom;
    }

    public String getSentTo() {
        return sentTo;
    }

    public void setSentTo(String sentTo) {
        this.sentTo = sentTo;
    }

    public String getSentToProfilePicture() {
        return sentToProfilePicture;
    }

    public void setSentToProfilePicture(String sentToProfilePicture) {
        this.sentToProfilePicture = sentToProfilePicture;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }
}


