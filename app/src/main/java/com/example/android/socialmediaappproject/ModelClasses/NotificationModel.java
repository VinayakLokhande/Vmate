package com.example.android.socialmediaappproject.ModelClasses;

public class NotificationModel {
    private String notificationBy;
    private long notificationAt;
    private String typeOfNoti;
    private String postId;
    private String postedBy;
    private boolean checkNotiOpenOrNot;
    private String notificationId;


    public NotificationModel() {
    }

    public NotificationModel(String notificationBy, long notificationAt, String typeOfNoti, String postId, String postedBy,
                             boolean checkNotiOpenOrNot, String notificationId) {
        this.notificationBy = notificationBy;
        this.notificationAt = notificationAt;
        this.typeOfNoti = typeOfNoti;
        this.postId = postId;
        this.postedBy = postedBy;
        this.checkNotiOpenOrNot = checkNotiOpenOrNot;
        this.notificationId = notificationId;
    }


    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getNotificationBy() {
        return notificationBy;
    }

    public void setNotificationBy(String notificationBy) {
        this.notificationBy = notificationBy;
    }

    public long getNotificationAt() {
        return notificationAt;
    }

    public void setNotificationAt(long notificationAt) {
        this.notificationAt = notificationAt;
    }

    public String getTypeOfNoti() {
        return typeOfNoti;
    }

    public void setTypeOfNoti(String typeOfNoti) {
        this.typeOfNoti = typeOfNoti;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }

    public boolean isCheckNotiOpenOrNot() {
        return checkNotiOpenOrNot;
    }

    public void setCheckNotiOpenOrNot(boolean checkNotiOpenOrNot) {
        this.checkNotiOpenOrNot = checkNotiOpenOrNot;
    }
}
