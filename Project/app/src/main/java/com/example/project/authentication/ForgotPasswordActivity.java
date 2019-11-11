package com.example.project.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project.MainActivity;
import com.example.project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {
    private static final String TAG = "Forgot_Password";

    private FirebaseAuth firebaseAuth;

    private Toolbar toolbar;
    private AlertDialog myProgress;

    private TextInputEditText editTextEmailForgotPassword;
    private TextInputLayout emailForgotPasswordWrapper;
    private Button buttonForgotPassword;
    private TextView textViewStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        process();
    }

    private void process() {
        findView();
        setUpToolBar();
        setUpProgressDialog();

        getData();
        handleEvent();
    }

    private void getData() {
        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
    }

    private void findView() {
        emailForgotPasswordWrapper = findViewById(R.id.emailForgotPasswordWrapper);
        editTextEmailForgotPassword = findViewById(R.id.editTextEmailForgotPassword);
        buttonForgotPassword = findViewById(R.id.buttonForgotPassword);
        textViewStatus = findViewById(R.id.textViewStatus);
        textViewStatus.setText(null);
    }

    private void handleEvent() {
        buttonForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmailResetPassword();
            }
        });

        editTextEmailForgotPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                emailForgotPasswordWrapper.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void sendEmailResetPassword() {
        String emailAddress = editTextEmailForgotPassword.getText().toString();

        if(emailAddress.matches("")){
            emailForgotPasswordWrapper.setError("Email is required!");
        }else {
            myProgress.show();
            try {
                firebaseAuth.sendPasswordResetEmail(emailAddress)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    myProgress.dismiss();
                                    Toast.makeText(ForgotPasswordActivity.this, "Email sent! Please check ur email.", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "Email sent.");
                                    startActivity(new Intent(ForgotPasswordActivity.this, MainActivity.class));
                                    finish();
                                }else {
                                    myProgress.dismiss();
                                    textViewStatus.setText(task.getException().getMessage());
                                    emailForgotPasswordWrapper.setError("Example: your_name@provide.domain");
                                    Log.w(TAG, "Error send email forgot password"+ task.getException().getMessage());
                                }
                            }
                        });
            }catch (Exception e){
                myProgress.dismiss();
                Log.w(TAG, "Error send email forgot password"+ e.getMessage());
            }
        }
    }

    private void setUpProgressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setView(R.layout.my_progress_layout);

        myProgress = builder.create();
        myProgress.setCancelable(false);
    }

    private void setUpToolBar() {
        toolbar = (Toolbar) findViewById(R.id.forgotPasswordToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Forgot Password");
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
