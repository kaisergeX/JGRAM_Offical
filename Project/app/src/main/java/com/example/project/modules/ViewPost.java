package com.example.project.modules;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.Navigation;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.example.project.JGramActivity;
import com.example.project.R;
import com.example.project.SetupSkeleton;
import com.example.project.entity.User;
import com.example.project.service.FirebaseService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.facebook.AccessTokenManager.TAG;

public class ViewPost extends AppCompatActivity {

    private Toolbar toolbar;
    private CircleImageView profile_image;
    private TextView profile_name;
    private ImageView imageView;
    private Button likeButton;
    private boolean back;
    private Intent intent;
    private Bundle bundle;
    private Boolean isLike;
    private String postId;
    private String uid;
    private TextView createdDate;
    private Boolean isOwner;
    private ImageView deleteBtn;
    private FirebaseFirestore db;
    private SkeletonScreen skeletonScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);
        process();

        skeletonScreen = Skeleton.bind(findViewById(R.id.activity_main))
                .load(R.layout.activity_view_post)
                .shimmer(true)
                .color(R.color.shimmer)
                .angle(20)
                .duration(1000)
                .show();
    }

    private void process() {
        findView();
        intent = getIntent();
        bundle = intent.getExtras();
        back = bundle == null ? false : bundle.getBoolean("back");
        setUpToolBar();
        setData();
        likePost();
    }

    private void findView() {
        profile_image = findViewById(R.id.profile_image);
        profile_name = findViewById(R.id.profile_name);
        imageView = findViewById(R.id.imageView);
        likeButton = findViewById(R.id.likeButton);
        createdDate = findViewById(R.id.createdDate);
        deleteBtn = findViewById(R.id.deleteBtn);
    }


    private void setData() {
        isOwner = false;
        isLike = false;
        try {
            postId = bundle.getString("id");

            db = FirebaseFirestore.getInstance();
            final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            final FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            uid = currentUser.getUid();
            final DocumentReference docRef = db.collection("post").document(postId);

            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        final DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            final String ownerId = (String) document.get("uid");
                            if (uid.equals(ownerId)) {
                                isOwner = true;
                                deletePost();
                            }
                            db.collection("users")
                                    .document(ownerId)
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            profile_name.setText((String) documentSnapshot.get("name"));
                                            Date date = new java.util.Date((Long) document.get("unixTimeStamps") * 1000L);
                                            // the format of your date
                                            SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMM dd yyyy HH:mm");
                                            // give a timezone reference for formatting (see comment at the bottom)
                                            sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+7"));
                                            String formattedDate = sdf.format(date);
                                            createdDate.setText(formattedDate);
                                            Glide
                                                    .with(getApplicationContext())
                                                    .load((String) document.get("imageUri"))
                                                    .into(imageView);
                                            Glide
                                                    .with(getApplicationContext())
                                                    .load((String) documentSnapshot.get("avatarUrl"))
                                                    .placeholder(R.drawable.default_user)
                                                    .error(R.drawable.default_user)
                                                    .into(profile_image);
                                            skeletonScreen.hide();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });


                        } else {
                            System.out.println("No docutment");
                        }

                        docRef.collection("userGallery").get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            int likeNumber = 0;
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                likeNumber++;
                                                if (currentUser != null
                                                        && !uid.isEmpty()
                                                        && uid.equals(document.getId())) {
                                                    isLike = true;
                                                }
                                            }
                                            setButton();
                                            likeButton.setText(likeNumber + " like");
                                        } else {
                                            Log.d(TAG, "Error getting documents: ", task.getException());
                                        }
                                    }
                                });
                    } else {
                        System.out.println(task.getException());
                    }
                }
            });

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {

        }
    }

    private void setButton() {
        if (isLike) {
            likeButton.setTextColor(getResources().getColor(R.color.colorPrimary));
        }
    }

    private void deletePost() {
        System.out.println(isOwner);
        if (isOwner) {
            deleteBtn.setVisibility(View.VISIBLE);
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog();
                }
            });
        }
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert");
        builder.setMessage("Are you sure deleting this post");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                db.collection("post").document(postId).delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(), "Delete success", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ViewPost.this, JGramActivity.class);
                                ViewPost.this.startActivity(intent);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void likePost() {
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseService firebaseService = new FirebaseService(ViewPost.this);
                if (!isLike) {
                    firebaseService.likePost(postId, uid, ViewPost.this);
                } else {
                    firebaseService.unLikePost(postId, uid, ViewPost.this);
                }
            }
        });
    }

    private void setUpToolBar() {
        toolbar = (Toolbar) findViewById(R.id.viewPostToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (back) {
                    finish();
                } else {
                    Intent intent = new Intent(getApplicationContext(), JGramActivity.class);
                    startActivity(intent);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
