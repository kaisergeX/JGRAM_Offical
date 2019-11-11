package com.example.project.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.Navigation;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project.MainActivity;
import com.example.project.R;
import com.example.project.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "Firebase Firestore";

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore firebaseFirestore;

    private TextView toLoginTextView;
    private Toolbar toolbar;
    private AlertDialog myProgress;

    private View rootView;

    private TextInputEditText editTextSignUpUsername, editTextSignUpPassword, editTextSignUpConfirm, editTextSignUpEmail;
    private TextInputLayout textInputLayoutUsername, textInputLayoutPassword, textInputLayoutConfirm, textInputLayoutEmail;
    private Button buttonSignUp;
    private String username, email, uID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        process();
    }

    private void process() {
        findView();

        setUpToolBar();
        setUpProgressDialog();
        toLogin();

        getData();
        handleEvent();
    }

    private void findView() {
        toLoginTextView = findViewById(R.id.textView);
        editTextSignUpUsername = findViewById(R.id.editTextSignUpUserName);
        editTextSignUpPassword = findViewById(R.id.editTextSignUpPassword);
        editTextSignUpConfirm = findViewById(R.id.editTextConfirmSignUpPassword);
        editTextSignUpEmail = findViewById(R.id.editTextSignUpEmail);
        textInputLayoutEmail = findViewById(R.id.emailFieldSignUp);
        textInputLayoutUsername = findViewById(R.id.nameFieldSignUp);
        textInputLayoutPassword = findViewById(R.id.passwordFieldSignUp);
        textInputLayoutConfirm = findViewById(R.id.confirmPasswordFieldSignUp);
        buttonSignUp = findViewById(R.id.signUpBtn);
    }

    private void getData() {
        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    private void handleEvent() {
        handleTextInputLayoutEvent();

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUpJGramAccount();
            }
        });
    }

    private void signUpJGramAccount() {
        username = editTextSignUpUsername.getText().toString();
        email = editTextSignUpEmail.getText().toString().trim();
        String password = editTextSignUpPassword.getText().toString();
        String confirmPassword = editTextSignUpConfirm.getText().toString();

        if (TextUtils.isEmpty(username)) {
            textInputLayoutUsername.setError("Username is required!");
        } else if (password.length() < 6) {
            textInputLayoutPassword.setError("Password must be at least 6 characters");
        } else {
            textInputLayoutPassword.setError(null);
            textInputLayoutUsername.setError(null);

            if (password.matches(confirmPassword)) {
                try {
                    myProgress.show();
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                currentUser = firebaseAuth.getCurrentUser();
//                                if (currentUser != null) {
//
//                                }

                                // Add new user to Firestore
                                addNewUserFirestore();

                                Toast.makeText(getApplicationContext(), "Sign Up Successful!", Toast.LENGTH_SHORT).show();

                            } else {
                                myProgress.dismiss();
                                textInputLayoutEmail.setError(task.getException().getMessage());
                                Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (Exception e) {
                    myProgress.dismiss();

                    Toast.makeText(getApplicationContext(), "Create firebase account func failed!", Toast.LENGTH_SHORT).show();
                } finally {
//                    myProgress.dismiss();
                }
            } else {
                textInputLayoutConfirm.setError("Password is not matched!");
            }
        }
    }

    private void addNewUserFirestore() {
        Map<String, Object> newUser = new HashMap<>();
//        newUser.put("uid", currentUser.getUid());
        newUser.put("name", username);
        newUser.put("email", currentUser.getEmail());
        newUser.put("avatarUrl", currentUser.getPhotoUrl()); // null by default

        firebaseFirestore.collection("users").document(currentUser.getUid())
                .set(newUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Document user: " + username + " successfully written!");
                        FirebaseAuth.getInstance().signOut();
                        myProgress.dismiss();
                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        myProgress.dismiss();
                        Log.w(TAG, "Error writing user document", e);
                    }
                });

//        firebaseFirestore.collection("users")
//                .add(newUser)
//                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                    @Override
//                    public void onSuccess(DocumentReference documentReference) {
//                        Log.d(TAG, "Document user: " + username + " successfully written!");
//
//                        FirebaseAuth.getInstance().signOut();
//                        myProgress.dismiss();
//                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        startActivity(intent);
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w(TAG, "Error adding new user document", e);
//                    }
//                });

//        firebaseFirestore.collection("users")
//                .document(currentUser.getUid())
//                .set(newUser)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        Log.d(TAG, "Document user: " + username + " successfully written!");
//
//                        FirebaseAuth.getInstance().signOut();
//                        myProgress.dismiss();
//                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        startActivity(intent);
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w(TAG, "Error adding new user document", e);
//                    }
//                });
    }

    private void handleTextInputLayoutEvent() {
        editTextSignUpUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                textInputLayoutUsername.setError(null);
            }
        });

        editTextSignUpEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                textInputLayoutEmail.setError(null);
            }
        });

        editTextSignUpPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                textInputLayoutPassword.setError(null);
            }
        });

        editTextSignUpConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                textInputLayoutConfirm.setError(null);
            }
        });
    }

    private void setUpProgressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setView(R.layout.my_progress_layout);
        myProgress = builder.create();
        myProgress.setCancelable(false);
    }

    private void setUpToolBar() {
        toolbar = (Toolbar) findViewById(R.id.signUpToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void toLogin() {
        toLoginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
            }
        });
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
