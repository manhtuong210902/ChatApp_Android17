package com.example.chatapp;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class ChatMessage {
    private String messageTime, message, sendBy;
    private boolean typeMessage;

    public String getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(String messageTime) {
        this.messageTime = messageTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSendBy() {
        return sendBy;
    }

    public void setSendBy(String sendBy) {
        this.sendBy = sendBy;
    }

    public boolean isTypeMessage() {
        return typeMessage;
    }

    public void setTypeMessage(boolean typeMessage) {
        this.typeMessage = typeMessage;
    }

    public ChatMessage(String messageTime, String message, String sendBy, boolean typeMessage) {
        this.messageTime = messageTime;
        this.message = message;
        this.sendBy = sendBy;
        this.typeMessage = typeMessage;
    }

    public ChatMessage(){

    }

    @Exclude
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("messageTime", messageTime);
        result.put("message", message);
        result.put("sendBy", sendBy);
        result.put("typeMessage", typeMessage);
        return result;
    }
}
