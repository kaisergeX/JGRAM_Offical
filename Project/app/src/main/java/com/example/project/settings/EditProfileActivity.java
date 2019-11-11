package com.example.project.settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EditProfileActivity extends AppCompatActivity {
    private static final String TAG = "Firebase Manage-Users";

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        //tool bar need to add 2 btn: accept change, reject change in 2 corner.
        toolbar = (Toolbar) findViewById(R.id.editProfileToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        process();
    }

    private void process() {
        findView();
        setUpActionBar();
        handleEventOnClick();
    }

    private void findView(){
    }

    //*** RED ZONE
    private void deleteUserAccount(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User account deleted.");
                        }
                    }
                });
    }

    private void handleEventOnClick(){

    }

    private void setUpActionBar(){
        toolbar = (Toolbar) findViewById(R.id.editProfileToolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Edit Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
