package com.tranquangduy.model;

public class ChatList {
    private String id;
    private long lasttime;

    public ChatList() {
    }

    public long getLasttime() {
        return lasttime;
    }

    public ChatList(String id, long lasttime) {
        this.id = id;
        this.lasttime = lasttime;
    }

    public ChatList(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
