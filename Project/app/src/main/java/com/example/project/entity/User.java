package com.example.project.entity;

import java.io.Serializable;

public class User implements Serializable {

    private String uid;
    private String name;
    private String avatarUrl;

    public User() {
    }

    public User(String uid, String name, String avatarUrl) {
        this.uid = uid;
        this.name = name;
        this.avatarUrl = avatarUrl;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
