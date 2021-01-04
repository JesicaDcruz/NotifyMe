package com.example.notifymeproject;

public class Uploads {
    public String name;
    public String url;
    public String uploadTo;

    public Uploads(String name, String url,String uploadTo) {
        this.name = name;
        this.url = url;
        this.uploadTo = uploadTo;
    }
// Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public Uploads() {  }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
}
