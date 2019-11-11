package com.example.project.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.example.project.R;
import com.example.project.entity.User;
import com.example.project.modules.UserDetail;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.RecyclerViewHolder> {
    private Context context;
    private Activity activity;
    private List<User> list;

    public SearchAdapter(Activity activity, Context context, List<User> list) {
        this.activity = activity;
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public SearchAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.search_item, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.RecyclerViewHolder holder, final int position) {
        holder.textViewSearchUsername.setText(list.get(position).getName());
        holder.cardItemSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, UserDetail.class);
                intent.putExtra("user", list.get(position));
                activity.startActivity(intent);
            }
        });

        String avatarUrl = list.get(position).getAvatarUrl();
        if (avatarUrl != "null" &&avatarUrl != null ) {
            try {
                Glide.with(activity)
                        .load(avatarUrl)
                        .error(R.drawable.default_user)
                        .placeholder(R.drawable.default_user)
                        .into(holder.searchAvatar);
            } catch (Exception e) {
                System.out.println("Toang: "+ e.getMessage());
            }
        }else {
            holder.searchAvatar.setImageResource(R.drawable.default_user);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewSearchUsername;
        private CircleImageView searchAvatar;
        private MaterialCardView cardItemSearch;

        public RecyclerViewHolder(View itemView) {
            super(itemView);

            textViewSearchUsername = (TextView) itemView.findViewById(R.id.textViewSearchUsername);
            searchAvatar = (CircleImageView) itemView.findViewById(R.id.searchAvatar);
            cardItemSearch = (MaterialCardView) itemView.findViewById(R.id.cardItemSearch);
        }
    }
}
