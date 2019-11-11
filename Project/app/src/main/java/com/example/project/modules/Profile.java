package com.example.project.modules;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.project.R;
import com.example.project.SetupSkeleton;
import com.example.project.service.FirebaseService;
import com.example.project.settings.SettingActivity;
import com.facebook.AccessToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.project.adapter.ProfileCardImageAdapter;
import com.example.project.entity.PostEntity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class Profile extends Fragment {
    private static final String TAG = "Firebase Manage-Users";
    private View rootView;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private AccessToken accessToken;
    private FirebaseFirestore firebaseFirestore;

    private Button buttonSignOut;
    private RecyclerView profileImageGrid;
    private List<PostEntity> profileImageList;
    private Button toSetting;
    private TextView textViewUsername;
    private CircleImageView userAvatar;

    public Profile() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        process();
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        // https://firebase.google.com/docs/auth/android/manage-users
        getData();
        getUserProfile();
    }

    private void process() {
        findView();
        setDataForImageGrid();
        toSetting();
    }

    private void findView() {
        userAvatar = rootView.findViewById(R.id.userAvatar);
        profileImageGrid = rootView.findViewById(R.id.profileImageGrid);
        toSetting = rootView.findViewById(R.id.toSetting);
        textViewUsername = rootView.findViewById(R.id.textViewUsername);
    }

    private void getData() {
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();


    }

    private void getUserProfile() {
        if (currentUser != null) {
            firebaseFirestore.collection("users")
                    .document(currentUser.getUid())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            String name = "";
                            String email = "";
                            String avatarUrl = null;
                            JSONObject json = null;
                            try {
                                json = new JSONObject(documentSnapshot.getData());
                                name = json.getString("name");
                                email = json.getString("email");
                                avatarUrl = json.getString("avatarUrl");

                                if (TextUtils.isEmpty(name)) {
                                    textViewUsername.setText(email.substring(0, email.indexOf("@")));
                                } else {
                                    textViewUsername.setText(name);
                                }

                                Glide.with(Profile.this)
                                        .load(avatarUrl)
                                        .error(R.drawable.default_user)
                                        .placeholder(R.drawable.default_user)
                                        .into(userAvatar);

                            } catch (Exception e) {
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        }
    }

//    private void updateUserProfile() {
//        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
//                .setDisplayName("Kai")
//                .setPhotoUri(Uri.parse("https://example.com/kai/profile.jpg"))
//                .build();
//
//        currentUser.updateProfile(profileUpdates)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            Log.d(TAG, "User profile updated.");
//                        }
//                    }
//                });
//        //Add to firestore will be add soon
//    }

    private void setDataForImageGrid() {
        FirebaseService firebaseService = new FirebaseService(getActivity());
        FirebaseUser currentUser = firebaseService.getCurrentUser();

        profileImageList = new ArrayList<PostEntity>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("post").whereEqualTo("uid", currentUser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                profileImageList.add(PostEntity.setEntity(document));
                            }
                            if (profileImageList.size() > 0) {
                                ProfileCardImageAdapter myAdapter = new ProfileCardImageAdapter(getActivity(), profileImageList);
                                profileImageGrid.setLayoutManager(new GridLayoutManager(getContext(), 3));
                                SetupSkeleton.createSkeletion(profileImageGrid, R.layout.profile_card_item, myAdapter);
                            } else {
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }

                });
    }

    public void toSetting() {
        toSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SettingActivity.class);
                startActivity(intent);
            }
        });
    }
}
