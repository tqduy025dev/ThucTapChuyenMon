package com.tranquangduy.model;

public class Message {
    private String content;
    private long time;
    private String from;
    private String imgURL;

    public Message(){}

    public Message(String content, long time, String userID, String imgURL) {
        this.content = content;
        this.time = time;
        this.from = userID;
        this.imgURL = imgURL;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getUserID() {
        return from;
    }

    public void setUserID(String userID) {
        this.from = userID;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
