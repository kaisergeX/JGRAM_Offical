package com.example.project.settings;

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
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project.JGramActivity;
import com.example.project.R;
import com.example.project.authentication.LoginActivity;
import com.facebook.AccessToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseUser;

public class ChangePassword extends AppCompatActivity {
    private static final String TAG = "Firebase Change-Password";
    private static final String TAG_ReAuth = "FirebaseAuthRecentLoginRequiredException";
    //    private AccessToken accessToken;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    private AlertDialog myProgress;
    private Toolbar toolbar;
    private TextInputEditText editTextCurrentPassword, editTextNewPassword, editTextConfirmNewPassword;
    private TextInputLayout currentPasswordWrapper, newPasswordWrapper, confirmNewPasswordWrapper;
    private String currentPassword, newPassword, confirmNewPassword;
    private Button buttonChangePassword;
    private boolean checkCurrentPassword = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        toolbar = (Toolbar) findViewById(R.id.changePasswordToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        process();
    }

    @Override
    protected void onStart() {
        super.onStart();

        getFirebaseAuth();
        getCurrentUser();
    }

    private void process() {
        findView();
        setUpActionBar();
        setUpProgressDialog();
        handleEvent();
    }

    private void findView() {
        toolbar = (Toolbar) findViewById(R.id.changePasswordToolbar);
        editTextCurrentPassword = findViewById(R.id.editTextCurrentPassword);
        editTextNewPassword = findViewById(R.id.editTextNewPassword);
        editTextConfirmNewPassword = findViewById(R.id.editTextConfirmNewPassword);
        currentPasswordWrapper = findViewById(R.id.currentPasswordWrapper);
        newPasswordWrapper = findViewById(R.id.newPasswordWrapper);
        confirmNewPasswordWrapper = findViewById(R.id.confirmNewPasswordWrapper);
        buttonChangePassword = findViewById(R.id.buttonChangePassword);
    }

    private void getFirebaseAuth() {
        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
    }

    private void getCurrentUser() {
        currentUser = firebaseAuth.getCurrentUser();
    }

    private void handleEvent() {
        handleTextInputLayoutEvent();

        buttonChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePassword();
            }
        });
    }

    private void changePassword() {
        currentPassword = editTextCurrentPassword.getText().toString();
        newPassword = editTextNewPassword.getText().toString();
        confirmNewPassword = editTextConfirmNewPassword.getText().toString();
//        checkCurrentPassword();

        if (TextUtils.isEmpty(currentPassword)) {
            currentPasswordWrapper.setError("Password is required.");
        } else if (newPassword.length() < 6) {
            newPasswordWrapper.setError("Password must be at least 6 characters.");
        } else if (!newPassword.matches(confirmNewPassword)) {
            confirmNewPasswordWrapper.setError("Password is not matched.");
        } else {
            try {
                if (currentUser == null) {
                    throw new FirebaseAuthRecentLoginRequiredException("69", "Session timeout!");
                } else {
//                    checkCurrentPassword();
//                    System.out.println("=============== " + checkCurrentPassword);
//                    if (checkCurrentPassword) {
                    myProgress.show();

                    // Get auth credentials from the user for re-authentication.
                    AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), currentPassword);

                    // Prompt the user to re-provide their sign-in credentials
                    currentUser.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        currentUser.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(ChangePassword.this, "Password updated", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(ChangePassword.this, JGramActivity.class));
                                                    Log.d(TAG, "Password updated");
                                                    finish();
                                                } else {
                                                    Toast.makeText(ChangePassword.this, "Cannot updated password!", Toast.LENGTH_SHORT).show();
                                                    Log.d(TAG, "Error password not updated");
                                                    myProgress.dismiss();
                                                }
                                            }
                                        });
                                    } else {
                                        myProgress.dismiss();
                                        currentPasswordWrapper.setError("Password is wrong.");
                                        Log.d(TAG, "Error re-auth failed: " + task.getException().getMessage());
                                    }
                                }
                            });
//                    }else{
//                        currentPasswordWrapper.setError("Password is wrong.");
//                    }
                }
            } catch (FirebaseAuthRecentLoginRequiredException e) {
                myProgress.dismiss();

                Log.d(TAG_ReAuth, "Session timeout! Redirect to Login Fragment is required!" + e.getMessage());
                Toast.makeText(this, "Session timeout! Please login again.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ChangePassword.this, LoginActivity.class));
                finish();
            } catch (Exception e) {
                myProgress.dismiss();
                Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            } finally {
//                myProgress.dismiss();
            }
        }
    }

//    private void checkCurrentPassword() {
//        String email = currentUser.getEmail();
//        System.out.println(currentUser.getEmail()+" 2222222222222222222: "+currentPassword);
//        firebaseAuth.signInWithEmailAndPassword(email, currentPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//                if (task.isSuccessful()) {
//                    checkCurrentPassword = true;
//                } else {
//                    checkCurrentPassword = false;
//                    currentPasswordWrapper.setError("Password is wrong.");
//                }
//            }
//        });
//        getFirebaseAuth();
//        getCurrentUser();
//        System.out.println(" =========== 2.5 ============= "+currentUser.getEmail()+" : "+currentPassword);
//    }

    private void handleTextInputLayoutEvent() {
//        editTextConfirmNewPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
//                if (actionId == EditorInfo.IME_ACTION_DONE) {
//                    changePassword();
//                    return true;
//                }
//                return false;
//            }
//        });

        editTextCurrentPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                currentPasswordWrapper.setError(null);
            }
        });

        editTextNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                newPasswordWrapper.setError(null);
            }
        });

        editTextConfirmNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                confirmNewPasswordWrapper.setError(null);
            }
        });
    }

    private void setUpProgressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setView(R.layout.my_progress_layout);
        myProgress = builder.create();
        myProgress.setCancelable(false);
    }

    private void setUpActionBar() {
        setSupportActionBar(toolbar);
        toolbar.setTitle("Change Password");
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
