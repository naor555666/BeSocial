package com.example.besocial.data;

import android.graphics.drawable.Drawable;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatConversation {
    private String chatId;
    private String sender,receiver;
    private String receiverProfilePicture;
    private boolean isApproved;

    public ChatConversation(){

    }

    public ChatConversation(String chatId, String sender, String receiver, String sentToProfilePicture, boolean isApproved) {
        this.chatId = chatId;
        this.sender = sender;
        this.receiver = receiver;
        this.receiverProfilePicture = sentToProfilePicture;
        this.isApproved = isApproved;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getReceiverProfilePicture() {
        return receiverProfilePicture;
    }

    public void setReceiverProfilePicture(String receiverProfilePicture) {
        this.receiverProfilePicture = receiverProfilePicture;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }
}


