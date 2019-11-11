package com.example.project.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project.R;
import com.example.project.ToViewPost;
import com.example.project.entity.PostEntity;
import com.example.project.entity.User;
import com.example.project.modules.UserDetail;
import com.example.project.modules.ViewPost;
import com.example.project.service.FirebaseService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListViewHolder> {

    private Activity activity;
    private List<PostEntity> list;
    private View rootView;
    private FirebaseUser currentUser;
//    private Dialog myDialog;


    public ListAdapter(Activity activity, View rootView, List<PostEntity> list) {
        this.activity = activity;
        this.list = list;
        this.rootView = rootView;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.list_item, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ListViewHolder holder, final int position) {

        FirebaseService firebaseService = new FirebaseService(activity);
        currentUser = firebaseService.getCurrentUser();

        final PostEntity postEntity = list.get(position);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(postEntity.getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        postEntity.setUsername((String) documentSnapshot.get("name")); //// fix
                        postEntity.setAvatarUrl((String) documentSnapshot.get("avatarUrl"));
                        setData(postEntity , holder);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println(e.getMessage());
            }
        });
    }

    private void setData(final PostEntity postEntity, ListViewHolder holder) {
        final User user = new User(postEntity.getUid(), postEntity.getUsername(), postEntity.getAvatarUrl());
        holder.profile_name.setText(postEntity.getUsername());
        holder.viewImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToViewPost.toViewPost(activity , true , postEntity.getId(), postEntity.getUid());
            }
        });
        Date date = new java.util.Date(postEntity.getCreatedUnixTimeStamps() * 1000L);
        // the format of your date
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMM dd yyyy HH:mm");
        // give a timezone reference for formatting (see comment at the bottom)
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+7"));
        String formattedDate = sdf.format(date);
        holder.createdDate.setText(formattedDate);
        Glide
                .with(activity)
                .load((String) postEntity.getImageUrl())
                .into(holder.imageView);
        Glide
                .with(activity)
                .load((String) postEntity.getAvatarUrl())
                .placeholder(R.drawable.default_user)
                .error(R.drawable.default_user)
                .into(holder.profile_image);
        holder.profile_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(currentUser.getUid() + "   " + postEntity.getUid());
                if (currentUser.getUid().equals(postEntity.getUid())) {
                    Navigation.findNavController(rootView).navigate(R.id.profile);
                } else {
                    Intent intent = new Intent(activity, UserDetail.class);
                    intent.putExtra("user", user);
                    activity.startActivity(intent);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ListViewHolder extends RecyclerView.ViewHolder {

        protected TextView profile_name;
        protected CircleImageView profile_image;
        protected ImageView imageView;
        protected Button viewImageButton;
        protected TextView createdDate;

        public ListViewHolder(View v) {
            super(v);
            profile_name = (TextView) v.findViewById(R.id.profile_name);
            profile_image = (CircleImageView) v.findViewById(R.id.profile_image);
            imageView = (ImageView) v.findViewById(R.id.imageView);
            viewImageButton = (Button) v.findViewById(R.id.viewImageButton);
            createdDate = (TextView) v.findViewById(R.id.createdDate);
        }


    }

}
