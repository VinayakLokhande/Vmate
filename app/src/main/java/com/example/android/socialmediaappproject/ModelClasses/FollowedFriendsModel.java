package com.example.android.socialmediaappproject.ModelClasses;

public class FollowedFriendsModel {

    private String followedBy;
    private long followedAtTime;

    public FollowedFriendsModel() {
    }

    public FollowedFriendsModel(String followedBy, long followedAtTime) {
        this.followedBy = followedBy;
        this.followedAtTime = followedAtTime;
    }

    public String getFollowedBy() {
        return followedBy;
    }

    public void setFollowedBy(String followedBy) {
        this.followedBy = followedBy;
    }

    public long getFollowedAtTime() {
        return followedAtTime;
    }

    public void setFollowedAtTime(long followedAtTime) {
        this.followedAtTime = followedAtTime;
    }
}
