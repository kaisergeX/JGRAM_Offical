package com.example.project.modules;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.example.project.R;
import com.example.project.adapter.SearchAdapter;
import com.example.project.entity.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "SearchUser";
    private static final String TAG_RECOMMEND = "RecommendUser";

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore firebaseFirestore;
    private View rootView;
    private Toolbar toolbar;

    private EditText editTextSearch;
    private TextView textViewSearch, textViewRecommend;
    private String userInput;
    private int typeSearch = 0;

    private RecyclerView recyclerView, recyclerViewRecommend;
    private List<User> list, listRecommend;
    private SearchAdapter searchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        process();
    }

    @Override
    protected void onStart() {
        super.onStart();

        getData();
    }

    private void process() {
        findView();
        setUpToolBar();
        handleEvent();
        getRecommendUser();
    }

    private void findView() {
        recyclerView = findViewById(R.id.recyclerViewSearch);
        recyclerViewRecommend = findViewById(R.id.recyclerViewRecommend);
        editTextSearch = findViewById(R.id.editTextSearch);
        textViewSearch = findViewById(R.id.textViewSearch);
        textViewRecommend = findViewById(R.id.textViewRecommend);

        textViewSearch.setVisibility(View.GONE);
        textViewRecommend.setVisibility(View.GONE);
    }

    private void setRecyclerViewSearch() {
        searchAdapter = new SearchAdapter(SearchActivity.this, SearchActivity.this, list);
//                                    searchAdapter.notifyDataSetChanged();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(searchAdapter);
    }

    private void setRecyclerViewRecommend() {
        searchAdapter = new SearchAdapter(SearchActivity.this, SearchActivity.this, listRecommend);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewRecommend.setLayoutManager(layoutManager);
        recyclerViewRecommend.setAdapter(searchAdapter);
    }

    private void handleEvent() {

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                textViewSearch.setVisibility(View.VISIBLE);
                userInput = editTextSearch.getText().toString();

                if (TextUtils.isEmpty(userInput)) {
                    textViewSearch.setText(null);
                    typeSearch = 0;
                } else {
                    typeSearch = 1;
                    if (userInput.substring(0, 1).matches("@")) {
                        typeSearch = 2;
                    }
                }
//                Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    public void run() {
                searchEngine(typeSearch);
//                    }
//                }, 2000);   //2 seconds
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        editTextSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchEngine(typeSearch);
                    getRecommendUser();
                    return true;
                }
                return false;
            }
        });
    }

    private void searchEngine(int type) {
        list = new ArrayList<>();
        firebaseFirestore = FirebaseFirestore.getInstance();
        if (type == 1) {
            textViewSearch.setText("Searching...");

            //search all and show: ONLY a username matches user input (search tag)
            firebaseFirestore.collection("users")
                    .whereEqualTo("name", userInput)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {

                                    String uid = document.getId();
                                    String username = document.getString("name");
                                    String avatarUrl = document.getString("avatarUrl");
                                    list.add(new User(uid, username, avatarUrl));

                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    textViewSearch.setVisibility(View.GONE);
                                }
                                if (list.size() != 0) {
                                    setRecyclerViewSearch();
                                } else if (!TextUtils.isEmpty(userInput)) {
                                    textViewSearch.setText("We found nothing with keyword:\n" + userInput);
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                                textViewSearch.setVisibility(View.VISIBLE);
                                textViewSearch.setText("" + task.getException().getMessage());
                            }
                        }
                    });
        } else if (type == 2) {
            // Search tag (@, #)
            userInput = userInput.substring(1);
        } else {
            //Search all and show: username matches one (or more) character.

            //Another option search
        }
        typeSearch = 0;
    }

    private void getRecommendUser() {
        listRecommend = new ArrayList<>();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int result = 0;

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                int random = new Random().nextInt(3); //random 0,1,2

                                if (result <= 10) {
                                    if (random == 1) {
                                        Log.d(TAG_RECOMMEND, document.getId() + " => " + document.getData());

                                        String uid = document.getId();
                                        String username = document.getString("name");
                                        String avatarUrl = document.getString("avatarUrl");
                                        listRecommend.add(new User(uid, username, avatarUrl));

                                        result++;
                                    }
                                } else {
                                    break;
                                }
                                System.out.println(result + "==============" + random);

                            }
                            if (listRecommend.size() != 0) {
                                textViewRecommend.setVisibility(View.VISIBLE);
                                setRecyclerViewRecommend();
                                Log.d(TAG_RECOMMEND, listRecommend.size() + " => =============");
                            }
                        } else {
                            Log.d(TAG_RECOMMEND, "Error getting documents: ", task.getException());
                            textViewSearch.setVisibility(View.VISIBLE);
                            textViewSearch.setText("" + task.getException().getMessage());
                        }
                    }
                });
    }

    private void getData() {
        userInput = "";
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    private void setUpToolBar() {
        toolbar = (Toolbar) findViewById(R.id.signUpToolbar);
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
