package com.example.project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project.authentication.LoginActivity;
import com.example.project.authentication.SignUpActivity;
import com.example.project.service.FirebaseService;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "FacebookLogin";
    private static final String TAG_Firestore = "Firebase Manage-Users";

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore firebaseFirestore;

    private CallbackManager callbackManager;

    private LoginButton buttonLoginFacebook;
    private Button buttonLoginRedirect;
    private Button buttonSignUpRedirect;

    private TextView textToChange;

    private AlertDialog myProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        process();
    }

    @Override
    protected void onStart() {
        super.onStart();

        checkSignedIn();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void process() {
        findView();
        getData();
        setUpProgressDialog();
        handleEvent();
        changeTextColor();
        //        checkHashKeyDev();
    }


    private void findView() {
        buttonLoginFacebook = findViewById(R.id.buttonLoginFacebook);
        buttonLoginRedirect = findViewById(R.id.buttonLoginRedirect);
        buttonSignUpRedirect = findViewById(R.id.buttonSignUpRedirect);
        textToChange = findViewById(R.id.textView3);
    }

    private void changeTextColor() {
        String name = getColoredSpanned("Welcome to ", "#707070");
        String surName = getColoredSpanned("JGRAM", "#fe6a00");

        textToChange.setText(Html.fromHtml(name + " " + surName));
    }

    private String getColoredSpanned(String text, String color) {
        String input = "<font color=" + color + ">" + text + "</font>";
        return input;
    }


    private void getData() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    private void handleEvent() {
        try {
            loginFacebook();
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        buttonLoginRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });

        buttonSignUpRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SignUpActivity.class));
            }
        });
    }

    public void onFacebookBtnClick(View view) {
        buttonLoginFacebook.performClick();
    }

    private void loginFacebook() {
        callbackManager = CallbackManager.Factory.create();
        buttonLoginFacebook.setPermissions("email", "public_profile");

        buttonLoginFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                myProgress.show();
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                myProgress.dismiss();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(), "RegisterCallback failed. " + error.getMessage(),
                        Toast.LENGTH_LONG).show();
                myProgress.dismiss();
                Log.d(TAG, "facebook:onError", error);
            }
        });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(getApplicationContext(), "Login FB Successful!", Toast.LENGTH_SHORT).show();
                            addNewUserFaceBookFirestore();
                        } else {
                            // If sign in fails, display a message to the user.
                            System.out.println("======================== " + task.getException().getMessage());
                            myProgress.dismiss();
                            Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void addNewUserFaceBookFirestore() {
        currentUser = firebaseAuth.getCurrentUser();

        String providerId = "", fb_uid = "", name = "", email = "";
        String photoUrl = "";
        Uri avatarUrl = null;
        boolean needAddNewUser = false;

        if (currentUser != null) {
            FirebaseService firebaseService = new FirebaseService(MainActivity.this);

            for (UserInfo profile : currentUser.getProviderData()) {
                // Id of the provider (ex: google.com)
                providerId = profile.getProviderId();

                // UID specific to the provider
                fb_uid = profile.getUid();

                // Name, email address, and profile photo Url
                name = profile.getDisplayName();
                email = profile.getEmail();
                photoUrl = profile.getPhotoUrl().toString();
            }
            try {
                Map<String, Object> newUser = new HashMap<>();
                newUser.put("providerId", providerId);
                newUser.put("name", name);
                newUser.put("email", email);
                newUser.put("avatarUrl", photoUrl);
                newUser.put("fb_ID", fb_uid);

                firebaseFirestore.collection("users").document(currentUser.getUid())
                        .set(newUser)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG_Firestore, "Document fb user successfully written!");
                                myProgress.dismiss();
                                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                myProgress.dismiss();
                                Log.w(TAG_Firestore, "Error adding new fb user document", e);

                            }
                        });
            } catch (Exception e) {
                myProgress.dismiss();
                Log.w(TAG_Firestore, "Error adding new fb user document" + e.getMessage());
            }
        }
    }

    private void checkSignedIn() {
        //         Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            Intent intent = new Intent(MainActivity.this, JGramActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void setUpProgressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setView(R.layout.my_progress_layout);
        myProgress = builder.create();
        myProgress.setCancelable(false);
    }

//    /*   *   *   *   *   *   *   *   *   *
//     *  CHECK DEVELOPMENT HASHKEY FUNCTION
//     *  ⚠ Security Alert!!! ⚠
//     *
//     *  Unless needed to change or see the development hashkey,
//     *  DON'T TOUCH IT!
//     *
//     * */
//    private void checkHashKeyDev() {
//        try {
//            PackageInfo packageInfo = getPackageManager().getPackageInfo("com.example.project", PackageManager.GET_SIGNATURES);
//            for (Signature signature : packageInfo.signatures) {
//                MessageDigest messageDigest = MessageDigest.getInstance("SHA");
//                messageDigest.update(signature.toByteArray());
//                Log.d("KeyHash", Base64.encodeToString(messageDigest.digest(), Base64.DEFAULT));
//            }
//        } catch (Exception e) {
//            Log.d("ErrorKeyHash", e.getMessage());
//        }
//    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
