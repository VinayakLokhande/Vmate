package com.example.android.socialmediaappproject.ModelClasses;

import java.util.List;

public class StoriesRVModel {
    private String storyBy;
    private long storyAt;
    List<PostedStoryInfoModel> stories;

    public StoriesRVModel() {
    }

    public StoriesRVModel(String storyBy, long storyAt, List<PostedStoryInfoModel> stories) {
        this.storyBy = storyBy;
        this.storyAt = storyAt;
        this.stories = stories;
    }

    public String getStoryBy() {
        return storyBy;
    }

    public void setStoryBy(String storyBy) {
        this.storyBy = storyBy;
    }

    public long getStoryAt() {
        return storyAt;
    }

    public void setStoryAt(long storyAt) {
        this.storyAt = storyAt;
    }

    public List<PostedStoryInfoModel> getStories() {
        return stories;
    }

    public void setStories(List<PostedStoryInfoModel> stories) {
        this.stories = stories;
    }
}
