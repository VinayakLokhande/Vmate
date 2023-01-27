package com.example.android.socialmediaappproject.ModelClasses;

public class PostedStoryInfoModel {
    private String storyImg;
    private long storyPostedAt;

    public PostedStoryInfoModel() {
    }

    public PostedStoryInfoModel(String storyImg, long storyPostedAt) {
        this.storyImg = storyImg;
        this.storyPostedAt = storyPostedAt;
    }

    public String getStoryImg() {
        return storyImg;
    }

    public void setStoryImg(String storyImg) {
        this.storyImg = storyImg;
    }

    public long getStoryPostedAt() {
        return storyPostedAt;
    }

    public void setStoryPostedAt(long storyPostedAt) {
        this.storyPostedAt = storyPostedAt;
    }
}
