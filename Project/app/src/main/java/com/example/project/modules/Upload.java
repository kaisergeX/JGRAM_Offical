package com.example.project.modules;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.asksira.bsimagepicker.BSImagePicker;
import com.asksira.bsimagepicker.Utils;
import com.bumptech.glide.Glide;
import com.example.project.R;
import com.example.project.entity.UploadImageEntity;
import com.example.project.service.FirebaseService;


import java.io.File;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Upload extends Fragment implements BSImagePicker.OnSingleImageSelectedListener,
        BSImagePicker.OnMultiImageSelectedListener
        , BSImagePicker.ImageLoaderDelegate {

    private View rootView;
    private ImageView imageView;
    private ViewDataBinding binding;
    private Button postImageBtn;
    private Uri imageUri;
    private BSImagePicker singleSelectionPicker;


    public Upload() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        rootView = inflater.inflate(R.layout.fragment_upload, container, false);
        process();
        return rootView;
    }

    private void process() {
        findView();
        createImagePickerInstance();
        openImagePicker();
        uploadImageToFirebase();
    }

    private void uploadImageToFirebase() {
        postImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseService firebaseService = new FirebaseService(getActivity());

                long unixTime = System.currentTimeMillis() / 1000L;

                UploadImageEntity uploadImageEntity = new UploadImageEntity(firebaseService.getCurrentUser().getUid(),
                        "",
                        unixTime , 0);

                firebaseService.addImageToFirebase(uploadImageEntity , imageUri);
            }
        });
    }

    private void openImagePicker() {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                singleSelectionPicker.show(getChildFragmentManager(), "picker");
            }
        });
    }

    private void createImagePickerInstance() {
        singleSelectionPicker = new BSImagePicker.Builder("com.example.project.provider")
                .setMaximumDisplayingImages(24) //Default: Integer.MAX_VALUE. Don't worry about performance :)
                .setSpanCount(3) //Default: 3. This is the number of columns
                .setGridSpacing(Utils.dp2px(2)) //Default: 2dp. Remember to pass in a value in pixel.
                .setPeekHeight(Utils.dp2px(360)) //Default: 360dp. This is the initial height of the dialog.
                .build();
    }


    private void findView() {
        imageView = rootView.findViewById(R.id.imageView);
        postImageBtn = rootView.findViewById(R.id.postImageBtn);
        postImageBtn.setEnabled(false);
    }


    @Override
    public void loadImage(File imageFile, ImageView ivImage) {
//        this.imageFile = imageFile;
//        Glide.with(this).load(imageFile).into(ivImage);
        Glide.with(Upload.this).load(imageFile).into(ivImage);
    }

    @Override
    public void onMultiImageSelected(List<Uri> uriList, String tag) {

    }

    @Override
    public void onSingleImageSelected(Uri uri, String tag) {
        imageUri = uri;
        postImageBtn.setEnabled(true);
        imageView.setImageURI(uri);
    }


}
