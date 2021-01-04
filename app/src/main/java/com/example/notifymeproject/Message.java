package com.example.notifymeproject;

public class Message {
    String message,name,key,date;

    public Message(){}

    public Message(String message, String name,String date) {
        this.message = message;
        this.name = name;
        this.key = key;
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public String getDate() { return date; }

    public void setDate(String date) {this.date = date; }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
