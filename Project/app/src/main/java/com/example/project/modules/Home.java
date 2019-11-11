package com.example.project.modules;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.example.project.R;
import com.example.project.SetupSkeleton;
import com.example.project.adapter.ListAdapter;
import com.example.project.entity.PostEntity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import bolts.Task;

import static com.facebook.AccessTokenManager.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class Home extends Fragment {

    private View rootView;
    private RecyclerView postedList;
    private TextView noPostText;
    private SwipeRefreshLayout swipeToRefresh;
    private ListAdapter listAdapter;

    public Home() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_home, container, false);

        process();
        return rootView;
    }

    private void process() {
        findView();
        setData();
        refreshView();
    }


    private void findView() {
        postedList = rootView.findViewById(R.id.postedList);
        noPostText = rootView.findViewById(R.id.noPostText);
        swipeToRefresh = rootView.findViewById(R.id.swipeToRefresh);
    }

    private void setData() {
        try {
            LinearLayoutManager llm = new LinearLayoutManager(getContext());
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            postedList.setLayoutManager(llm);
            final List<PostEntity> postEntities = new ArrayList<PostEntity>();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("post").orderBy("unixTimeStamps", Query.Direction.DESCENDING)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {

                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    postEntities.add(PostEntity.setEntity(document));
                                }
                                if (postEntities.size() > 0) {
                                    listAdapter = new ListAdapter(getActivity(), rootView, postEntities);
                                    SetupSkeleton.createSkeletion(postedList , R.layout.list_item , listAdapter);

                                } else {
                                    noPostText.setVisibility(View.VISIBLE);
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }

                        }

                    });
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {

        }

    }

    private void refreshView() {
        swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setData();
                swipeToRefresh.setRefreshing(false);
            }
        });
    }


}
