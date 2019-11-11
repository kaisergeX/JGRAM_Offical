package com.example.project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.project.modules.ViewPost;

public class ToViewPost {

    public static void toViewPost(Activity activity, Boolean back , String id , String uid) {
        Intent intent = new Intent(activity, ViewPost.class);
        Bundle b = new Bundle();
        b.putBoolean("back", back);
        b.putSerializable("id" , id);
        b.putString("uid", uid);
        intent.putExtras(b);
        activity.startActivity(intent);
    }

}
