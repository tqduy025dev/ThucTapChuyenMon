package com.tranquangduy.model;

public class Message {
    private String content;
    private long time;
    private boolean seen;
    private String from;

    public Message(String content, long time, boolean seen, String from) {
        this.content = content;
        this.time = time;
        this.seen = seen;
        this.from = from;
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

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
