package com.example.android.socialmediaappproject.ModelClasses;

public class UsersInfoDataModel {
    private String username , email , password , phone , profession , coverPhoto , profilePhoto , userId, userBio;
    private int followersCount;
    private int userPostsCount;
    private int userFollowingsCount;

    public UsersInfoDataModel() {
    }

    public UsersInfoDataModel(String username, String email, String password, String phone, String profession) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.profession = profession;
    }

    public int getUserFollowingsCount() {
        return userFollowingsCount;
    }

    public void setUserFollowingsCount(int userFollowingsCount) {
        this.userFollowingsCount = userFollowingsCount;
    }

    public int getUserPostsCount() {
        return userPostsCount;
    }

    public void setUserPostsCount(int userPostsCount) {
        this.userPostsCount = userPostsCount;
    }

    public String getUserBio() {
        return userBio;
    }

    public void setUserBio(String userBio) {
        this.userBio = userBio;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }


    public String getCoverPhoto() {
        return coverPhoto;
    }

    public void setCoverPhoto(String coverPhoto) {
        this.coverPhoto = coverPhoto;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }
}
