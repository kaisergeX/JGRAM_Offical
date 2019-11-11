package com.example.project.modules;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.example.project.R;
import com.example.project.SetupSkeleton;
import com.example.project.adapter.ProfileCardImageAdapter;
import com.example.project.entity.PostEntity;
import com.example.project.entity.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserDetail extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView userGallery;
    private List<PostEntity> userImageList;
    private CircleImageView userAvatar;
    private TextView textViewUsername;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        process();
    }

    private void process() {
        findView();
        setUpToolBar();
        setUserNameAndAvatar();
        setDataForImageGrid();
    }

    private void findView() {
        userGallery = findViewById(R.id.userGallery);
        userAvatar = findViewById(R.id.userAvatar);
        textViewUsername = findViewById(R.id.textViewUsername);
    }

    private void setUserNameAndAvatar() {
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");
        textViewUsername.setText(user.getName());

        String avatarUrl = user.getAvatarUrl();
        Glide.with(UserDetail.this)
                .load(avatarUrl)
                .error(R.drawable.default_user)
                .placeholder(R.drawable.default_user)
                .into(userAvatar);
    }

    private void setDataForImageGrid() {
        userImageList = new ArrayList<PostEntity>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("post").whereEqualTo("uid", user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                userImageList.add(PostEntity.setEntity(document));
                            }
                            if (userImageList.size() > 0) {
                                ProfileCardImageAdapter myAdapter = new ProfileCardImageAdapter(UserDetail.this, userImageList);
                                userGallery.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
                                SetupSkeleton.createSkeletion(userGallery, R.layout.profile_card_item, myAdapter);

                            } else {
                            }
                        } else {
                            System.out.println(task.getException().getMessage());
                        }

                    }

                });


    }

    private void setUpToolBar() {
        toolbar = (Toolbar) findViewById(R.id.userDetailToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
