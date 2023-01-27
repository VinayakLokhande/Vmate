package com.example.android.socialmediaappproject.ModelClasses;

public class ChatListRVModel {
    private String lastMessage;
    private int unseenMessages;

    public ChatListRVModel() {
    }

    public ChatListRVModel(String lastMessage, int unseenMessages) {
        this.lastMessage = lastMessage;
        this.unseenMessages = unseenMessages;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public int getUnseenMessages() {
        return unseenMessages;
    }

    public void setUnseenMessages(int unseenMessages) {
        this.unseenMessages = unseenMessages;
    }
}
