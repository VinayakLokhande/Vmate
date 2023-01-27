package com.example.android.socialmediaappproject.ModelClasses;

public class ChatModel {
    private String senderId,receiverId,message,senderName,receiverName;

    public ChatModel(String senderId, String receiverId, String message, String senderName, String receiverName) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        this.senderName = senderName;
        this.receiverName = receiverName;
    }

    public ChatModel(String senderId, String receiverId, String message) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public ChatModel() {
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
