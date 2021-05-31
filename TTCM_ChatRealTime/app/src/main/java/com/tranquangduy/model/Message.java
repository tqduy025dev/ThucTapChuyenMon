package com.tranquangduy.model;

import java.io.Serializable;

public class Message  {
    private String message;
    private String sender;
    private String receiver;
    private long time;


    public Message() {
    }

    public Message(String message, String sender, String receiver, long time) {
        this.message = message;
        this.sender = sender;
        this.receiver = receiver;
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
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
}
