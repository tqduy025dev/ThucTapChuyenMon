package com.tranquangduy.model;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Message  {
    private String message;
    private String sender;
    private String receiver;
    private Boolean isseen;
    private String type;
    private long time;


    public Message() {
    }

    public Message(String message, String sender, String receiver, Boolean isseen, long time, String type) {
        this.message = message;
        this.sender = sender;
        this.receiver = receiver;
        this.isseen = isseen;
        this.time = time;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public Boolean getIsseen() {
        return isseen;
    }

    public void setIsseen(Boolean isseen) {
        this.isseen = isseen;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
