package com.server;

public class Message {
    private String sender;
    private String content;
    private String date;

    public Message() {}

    public Message(String sender, String content, String date) {
        this.sender = sender;
        this.content = content;
        this.date = date;
    }

    public String getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public String getTimestamp() {
        return date;
    }

}
