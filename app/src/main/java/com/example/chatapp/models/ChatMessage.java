package com.example.chatapp.models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class ChatMessage {
    private String messageTime, message, sendBy, typeMessage,messageId;

    public ChatMessage(String messageTime, String message, String sendBy, String typeMessage,String messageId) {
        this.messageTime = messageTime;
        this.message = message;
        this.sendBy = sendBy;
        this.typeMessage = typeMessage;
        this.messageId=messageId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

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

    public String getTypeMessage() {
        return typeMessage;
    }

    public void setTypeMessage(String typeMessage) {
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
        result.put("messageId",messageId);
        return result;
    }
}
