package com.example.project.settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.asksira.bsimagepicker.BSImagePicker;
import com.asksira.bsimagepicker.Utils;
import com.bumptech.glide.Glide;
import com.example.project.R;
import com.example.project.entity.UploadImageEntity;
import com.example.project.modules.Profile;
import com.example.project.service.FirebaseService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.util.List;

public class ChangeAvatar extends AppCompatActivity implements BSImagePicker.OnSingleImageSelectedListener,
        BSImagePicker.OnMultiImageSelectedListener
        , BSImagePicker.ImageLoaderDelegate {

    private BSImagePicker singleSelectionPicker;
    private ImageView imageView;
    private Button postImageBtn;
    private Toolbar toolbar;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_avatar);
        process();
    }

    private void process() {
        findView();
        setAvatar();
        createImagePickerInstance();
        openImagePicker();
        setUpActionBar();
        uploadImageToFirebase();
    }

    private void findView() {
        toolbar = findViewById(R.id.changeAvaToolbar);
        imageView = findViewById(R.id.imageView);
        postImageBtn = findViewById(R.id.postImageBtn);
        postImageBtn.setEnabled(false);

    }

    private void setAvatar(){
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        firebaseFirestore.collection("users")
                .document(currentUser.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Glide
                                .with(ChangeAvatar.this)
                                .load((String) documentSnapshot.get("avatarUrl"))
                                .error(R.drawable.ic_add_a_photo_black_24dp)
                                .into(imageView);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }


    private void openImagePicker() {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                singleSelectionPicker.show(getSupportFragmentManager(), "picker");
            }
        });
    }

    private void createImagePickerInstance() {
        singleSelectionPicker = new BSImagePicker.Builder("com.example.project.provider")
                .setMaximumDisplayingImages(Integer.MAX_VALUE) //Default: Integer.MAX_VALUE. Don't worry about performance :)
                .setSpanCount(3) //Default: 3. This is the number of columns
                .setGridSpacing(Utils.dp2px(2)) //Default: 2dp. Remember to pass in a value in pixel.
                .setPeekHeight(Utils.dp2px(360)) //Default: 360dp. This is the initial height of the dialog.
                .build();
    }

    private void uploadImageToFirebase() {
        postImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseService firebaseService = new FirebaseService(ChangeAvatar.this);
                firebaseService.setAvatar(uri);
            }
        });
    }


    @Override
    public void loadImage(File imageFile, ImageView ivImage) {

    }

    @Override
    public void onMultiImageSelected(List<Uri> uriList, String tag) {

    }

    @Override
    public void onSingleImageSelected(Uri uri, String tag) {
        postImageBtn.setEnabled(true);
        Glide.with(this)
                .load(uri)
                .error(R.drawable.ic_add_a_photo_black_24dp)
                .into(imageView);
        this.uri = uri;
    }

    private void setUpActionBar() {
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
