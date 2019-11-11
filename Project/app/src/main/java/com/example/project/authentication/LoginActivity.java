package com.example.project.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project.JGramActivity;
import com.example.project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "Firebase Manage-Users";

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    private TextView toSignupTextView;

    private Button buttonLoginDefault;
    private TextInputEditText editTextUsername, editTextPassword;
    private TextInputLayout usernameWrapper, passwordWrapper;
    private TextView textViewForgotPassword;
    private Toolbar toolbar;
    private AlertDialog myProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        process();
    }


    private void process() {
        findView();

        setUpToolBar();
        toSignup();
        setUpProgressDialog();

        getData();
        handleEvent();
    }

    private void getData() {
        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
    }

    private void findView() {
        toSignupTextView = findViewById(R.id.toSignup);
        buttonLoginDefault = findViewById(R.id.buttonLoginFirebase);
        editTextUsername = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        usernameWrapper = findViewById(R.id.emailField);
        passwordWrapper = findViewById(R.id.passwordField);
        textViewForgotPassword = findViewById(R.id.textViewForgotPassword);
    }

    private void handleEvent() {
        buttonLoginDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginJGramAccount();
            }
        });

        textViewForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,ForgotPasswordActivity.class));
            }
        });
        handleTextInputLayoutEvent();
    }

    private void loginJGramAccount() {
        String username = editTextUsername.getText().toString();
        String password = editTextPassword.getText().toString();
        if (TextUtils.isEmpty(username)) {
            usernameWrapper.setError("Username is required!");
        } else if (password.length() < 6) {
            passwordWrapper.setError("Password must be at least 6 characters");
        } else {
            usernameWrapper.setError(null);
            passwordWrapper.setError(null);
            myProgress.show();
            try {
                firebaseAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Intent intent = new Intent(getApplicationContext(), JGramActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            Toast.makeText(getApplicationContext(), "Login Successful!", Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                        } else {
                            myProgress.dismiss();
                            Toast.makeText(getApplicationContext(), "Your username or password is incorrect", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } catch (Exception e) {
                myProgress.dismiss();
                Toast.makeText(getApplicationContext(), "Firebase Login failed!", Toast.LENGTH_SHORT).show();
            } finally {
//                myProgress.dismiss();
            }
        }
    }



    private void handleTextInputLayoutEvent() {
        editTextPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginJGramAccount();
                    return true;
                }
                return false;
            }
        });

        editTextUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                usernameWrapper.setError(null);
            }
        });

        editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                passwordWrapper.setError(null);
            }
        });
    }

    private void toSignup() {
        toSignupTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setUpProgressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this)
                .setView(R.layout.my_progress_layout);
//        TextView progressMessage = findViewById(R.id.textViewProgress);
//        progressMessage.setText("Please wait...");

        myProgress = builder.create();
        myProgress.setCancelable(false);
    }

    private void setUpToolBar() {
        toolbar = (Toolbar) findViewById(R.id.loginToolbar);
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

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}
