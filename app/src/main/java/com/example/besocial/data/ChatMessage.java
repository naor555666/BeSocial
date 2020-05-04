package com.example.besocial.data;

import android.graphics.drawable.Drawable;

public class ChatMessage {

    private String msgBody,sentFrom,sentTo,msgDate,msgTime;
    private Drawable senderProfileImage;

    public ChatMessage(){

    }

    public ChatMessage(String msgBody, String sentFrom, String sentTo, String msgDate, String msgTime, Drawable senderProfileImage) {
        this.msgBody = msgBody;
        this.sentFrom = sentFrom;
        this.sentTo = sentTo;
        this.msgDate = msgDate;
        this.msgTime = msgTime;
        this.senderProfileImage = senderProfileImage;
    }

    public String getMsgBody() {
        return msgBody;
    }

    public void setMsgBody(String msgBody) {
        this.msgBody = msgBody;
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

    public String getMsgDate() {
        return msgDate;
    }

    public void setMsgDate(String msgDate) {
        this.msgDate = msgDate;
    }

    public String getMsgTime() {
        return msgTime;
    }

    public void setMsgTime(String msgTime) {
        this.msgTime = msgTime;
    }

    public Drawable getSenderProfileImage() {
        return senderProfileImage;
    }

    public void setSenderProfileImage(Drawable senderProfileImage) {
        this.senderProfileImage = senderProfileImage;
    }
}
