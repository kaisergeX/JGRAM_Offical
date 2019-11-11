package com.example.project.entity;

import android.net.Uri;

import java.io.File;
import java.io.Serializable;

public class UploadImageEntity implements Serializable {

    private String uid;
    private String imageUri;
    private Long unixTimeStamps;
    private int likeNumber;

    public UploadImageEntity(){};

    public UploadImageEntity(String uid, String imageUri, Long unixTimeStamps, int likeNumber) {
        this.uid = uid;
        this.imageUri = imageUri;
        this.unixTimeStamps = unixTimeStamps;
        this.likeNumber = likeNumber;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public Long getUnixTimeStamps() {
        return unixTimeStamps;
    }

    public void setUnixTimeStamps(Long unixTimeStamps) {
        this.unixTimeStamps = unixTimeStamps;
    }

    public int getLikeNumber() {
        return likeNumber;
    }

    public void setLikeNumber(int likeNumber) {
        this.likeNumber = likeNumber;
    }
}
