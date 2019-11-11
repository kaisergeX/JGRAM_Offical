package com.example.project.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project.R;
import com.example.project.ToViewPost;
import com.example.project.entity.PostEntity;
import com.example.project.modules.ViewPost;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class ProfileCardImageAdapter extends RecyclerView.Adapter<ProfileCardImageAdapter.MyViewHolder> {

    private Activity activity;
    private List<PostEntity> list;


    public ProfileCardImageAdapter(Activity activity, List<PostEntity> list) {
        this.activity = activity;
        this.list = list;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.profile_card_item, parent, false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final PostEntity postEntity = list.get(position);
            Glide
                    .with(activity)
                    .load(postEntity.getImageUrl())
                    .into(holder.cardImageView);
        holder.cardImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToViewPost.toViewPost(activity , true , postEntity.getId(), postEntity.getUid());
            }
        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView cardImageView;

        public MyViewHolder(View itemView) {
            super(itemView);

            cardImageView = (ImageView) itemView.findViewById(R.id.cardImageView);

        }
    }
}
