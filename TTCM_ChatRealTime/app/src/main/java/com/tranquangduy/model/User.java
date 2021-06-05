package com.tranquangduy.model;

import java.io.Serializable;

public class User implements Serializable {
    private String id;
    private String userName;
    private String fullName;
    private String email;
    private String imageUrl;
    private String bio;
    private String webpage;
    private String status;


    public User() {
    }

    public User(String id, String userName, String fullName, String email, String imageUrl,String status, String bio,String webpage) {
        this.id = id;
        this.userName = userName;
        this.fullName = fullName;
        this.email = email;
        this.imageUrl = imageUrl;
        this.status =status;
        this.webpage =webpage;
        this.bio = bio;
    }

    public String getWebpage() {
        return webpage;
    }

    public void setWebpage(String webpage) {
        this.webpage = webpage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
