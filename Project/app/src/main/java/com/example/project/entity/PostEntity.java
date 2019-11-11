package com.example.project.entity;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.Serializable;
import java.util.ArrayList;

public class PostEntity implements Serializable {

    private String id;
    private String username;
    private String uid;
    private String avatarUrl;
    private String imageUrl;
    private ArrayList<String> likeUserList;
    private long createdUnixTimeStamps;

    public PostEntity() {
    }


    public static PostEntity setEntity(final QueryDocumentSnapshot document) {
        final PostEntity postEntity = new PostEntity();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uid = (String) document.get("uid");
        postEntity.setId(document.getId());
        postEntity.setUid(uid);
        postEntity.setImageUrl((String) document.get("imageUri"));
        postEntity.setCreatedUnixTimeStamps((Long) document.get("unixTimeStamps"));


        return postEntity;
    }

    public PostEntity(String id, String username, String uid, String avatarUrl, String imageUrl, ArrayList<String> likeUserList, long createdUnixTimeStamps) {
        this.id = id;
        this.username = username;
        this.uid = uid;
        this.avatarUrl = avatarUrl;
        this.imageUrl = imageUrl;
        this.likeUserList = likeUserList;
        this.createdUnixTimeStamps = createdUnixTimeStamps;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public ArrayList<String> getLikeUserList() {
        return likeUserList;
    }

    public void setLikeUserList(ArrayList<String> likeUserList) {
        this.likeUserList = likeUserList;
    }

    public long getCreatedUnixTimeStamps() {
        return createdUnixTimeStamps;
    }

    public void setCreatedUnixTimeStamps(long createdUnixTimeStamps) {
        this.createdUnixTimeStamps = createdUnixTimeStamps;
    }
}
