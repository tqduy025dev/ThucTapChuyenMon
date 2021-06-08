package com.tranquangduy.model;

public class Notification {
    private String userid;
    private String text;
    private String postid;
    private Boolean ismessage;
    private Boolean ispost;

    public Notification(){}

    public Notification(String userid, String text, String postid, Boolean ismessage, Boolean ispost) {
        this.userid = userid;
        this.text = text;
        this.postid = postid;
        this.ismessage = ismessage;
        this.ispost = ispost;
    }

    public Boolean getIsmessage() {
        return ismessage;
    }

    public void setIsmessage(Boolean ismessage) {
        this.ismessage = ismessage;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public Boolean getIspost() {
        return ispost;
    }

    public void setIspost(Boolean ispost) {
        this.ispost = ispost;
    }
}
