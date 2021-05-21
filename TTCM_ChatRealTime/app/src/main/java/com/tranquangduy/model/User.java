package com.tranquangduy.model;

public class User {
    private String id;
    private String userName;
    private String fullName;
    private String mailAddress;
    private String imageUrl;
    private String bio;


    public User(String id, String userName, String fullName, String mailAddress, String imageUrl, String bio) {
        this.id = id;
        this.userName = userName;
        this.fullName = fullName;
        this.mailAddress = mailAddress;
        this.imageUrl = imageUrl;
        this.bio = bio;
    }

    public User(){
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getMailAddress() {
        return mailAddress;
    }

    public void setMailAddress(String mailAddress) {
        this.mailAddress = mailAddress;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
