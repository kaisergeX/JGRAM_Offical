package com.example.project.service;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.project.JGramActivity;
import com.example.project.R;
import com.example.project.ToViewPost;
import com.example.project.entity.PostEntity;
import com.example.project.entity.UploadImageEntity;
import com.example.project.modules.Profile;
import com.example.project.modules.ViewPost;
import com.google.android.gms.tasks.Continuation;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class FirebaseService {

    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private FirebaseStorage storage;
    private AlertDialog myProgress;
    private Activity activity;

    public FirebaseService() {


    }

    public FirebaseService(Activity activity) {
        this.activity = activity;
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        setUpProgressDialog();
    }

    public void likePost(final String postId, String uid, final Activity activity) {
        try {
            myProgress.show();
            DocumentReference docRef = db.collection("post").document(postId);
            Map<String, Object> likeUser = new HashMap<>();
            likeUser.put("id", uid);
            docRef.collection("userGallery").document(uid).set(likeUser)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            reload(activity, postId);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception ex) {
            myProgress.dismiss();
            System.out.println(ex.getMessage());
        } finally {

        }

    }

    public void unLikePost(final String postId, String uid, final Activity activity) {

        try {
            DocumentReference docRef = db.collection("post").document(postId);
            docRef.collection("userGallery").document(uid).delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            reload(activity, postId);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception ex) {
            myProgress.dismiss();
            System.out.println(ex.getMessage());
        } finally {

        }

    }

    private void reload(Activity activity, String postId) {
        Intent intent = new Intent(activity, activity.getClass());
        Bundle b = new Bundle();
        b.putString("id", postId);
//        b.putBoolean("back", true);
        intent.putExtras(b);
        activity.finish();
        activity.startActivity(intent);
    }


    public PostEntity getPost(String postId) {
        try {
            final PostEntity postEntity = new PostEntity();
            DocumentReference docRef = db.collection("post").document(postId);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            postEntity.setUsername("caubengoaiquoc"); //// fix
                            postEntity.setUid((String) document.get("uid"));
                            postEntity.setAvatarUrl((String) document.get("imageUri"));
                            postEntity.setImageUrl((String) document.get("imageUri"));
                            postEntity.setCreatedUnixTimeStamps((Long) document.get("unixTimeStamps"));
                        } else {
                            System.out.println("No docutment");
                        }
                    } else {
                        System.out.println(task.getException());
                    }
                }
            });
            return postEntity;

        } catch (Exception ex) {
            myProgress.dismiss();
            System.out.println(ex.getMessage());
        } finally {

        }
        return null;
    }


    public FirebaseUser getCurrentUser() {
        return currentUser = firebaseAuth.getCurrentUser();
    }

    public void addImageToFirebase(final UploadImageEntity uploadImageEntity, Uri imgaeUri) {

        try {
            myProgress.show();
            storage = FirebaseStorage.getInstance();

            StorageReference storageRef = storage.getReference();

            final StorageReference ref = storageRef.child("postedImages/" + uploadImageEntity.getUid() + "/" + uploadImageEntity.getUnixTimeStamps());

            UploadTask uploadTask = ref.putFile(imgaeUri);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        uploadImageEntity.setImageUri(downloadUri.toString());
                        addImageToFireStore(uploadImageEntity);
                    } else {
                        // ...
                    }
                }
            });


        } catch (Exception ex) {
            myProgress.dismiss();
            Toast.makeText(activity, ex.getMessage(), Toast.LENGTH_LONG);
        } finally {
        }


    }

    public void setAvatar(final Uri imageUri) {
        try {
            myProgress.show();
            final String uid = getCurrentUser().getUid();
            storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            final StorageReference ref = storageRef.child("avatars/" + uid);
            UploadTask uploadTask = ref.putFile(imageUri);
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        db.collection("users")
                                .document(currentUser.getUid())
                                .update("avatarUrl", downloadUri.toString())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Intent intent = new Intent(activity , JGramActivity.class);
                                        activity.startActivity(intent);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG);
                            }
                        });
                    } else {
                        // ...
                    }
                }
            });

        } catch (Exception ex) {
            myProgress.dismiss();
            Toast.makeText(activity, ex.getMessage(), Toast.LENGTH_LONG);
        } finally {
        }
    }



    private void addImageToFireStore(final UploadImageEntity uploadImageEntity) {
        try {
            db.collection("post")
                    .add(uploadImageEntity)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            ToViewPost.toViewPost(activity , false ,
                                    documentReference.getId() , uploadImageEntity.getUid());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception ex) {
            Toast.makeText(activity, ex.getMessage(), Toast.LENGTH_LONG);
        } finally {

        }
    }

    private void getUser(String uid) {

    }


    private void setUpProgressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                .setView(R.layout.my_progress_layout);
//        TextView progressMessage = findViewById(R.id.textViewProgress);
//        progressMessage.setText("Please wait...");

        myProgress = builder.create();
        myProgress.setCancelable(false);
    }

}
