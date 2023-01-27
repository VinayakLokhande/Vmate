package com.example.android.socialmediaappproject.ModelClasses;

public class PostsRVModel {
    private String postId;
    private String postImage;
    private String postDescription;
    private String postBy;
    private String postAt;
    private int totalLikes;
    private int totalComments;


    public PostsRVModel() {
    }

    public PostsRVModel(String postId, String postImage, String postDescription, String postBy, String postAt,int totalLikes) {
        this.postId = postId;
        this.postImage = postImage;
        this.postDescription = postDescription;
        this.postBy = postBy;
        this.postAt = postAt;
        this.totalLikes = totalLikes;
    }

    public int getTotalComments() {
        return totalComments;
    }

    public void setTotalComments(int totalComments) {
        this.totalComments = totalComments;
    }

    public int getTotalLikes() {
        return totalLikes;
    }

    public void setTotalLikes(int totalLikes) {
        this.totalLikes = totalLikes;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getPostDescription() {
        return postDescription;
    }

    public void setPostDescription(String postDescription) {
        this.postDescription = postDescription;
    }

    public String getPostBy() {
        return postBy;
    }

    public void setPostBy(String postBy) {
        this.postBy = postBy;
    }

    public String getPostAt() {
        return postAt;
    }

    public void setPostAt(String postAt) {
        this.postAt = postAt;
    }
}
