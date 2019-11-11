package com.example.project.settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.project.MainActivity;
import com.example.project.R;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static androidx.navigation.ui.NavigationUI.setupActionBarWithNavController;

public class SettingActivity extends AppCompatActivity {
    private static final String TAG = "Firebase Manage-Users";

    private NavHostFragment navHostFragment;
    private Toolbar toolbar;
    private Button logOutBtn, changePasswordBtn ,changeAvatarBtn;
    private AlertDialog myProgress;

    private AccessToken accessToken;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        toolbar = (Toolbar) findViewById(R.id.settingToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        process();

    }

    private void process() {
        findView();
        setUpActionBar();
        setUpProgressDialog();

        toChangeAvatar();
        checkAccountFacebook();
        handleEventOnClick();
    }

    private void findView() {
        logOutBtn = findViewById(R.id.logOutBtn);
        changePasswordBtn = findViewById(R.id.changePasswordBtn);
        changeAvatarBtn = findViewById(R.id.changeAvatarBtn);
    }


    private void getCurrentUser() {
        currentUser = firebaseAuth.getCurrentUser();
        accessToken = AccessToken.getCurrentAccessToken();
    }

    // https://firebase.google.com/docs/auth/android/manage-users

    //If user want to enable 2-Step Verification with Email
    private void verificationEmail() {

        //Send_email_verification_with_continue_url

        //If your app have its own website, use custom email below
        //else use default verificationEmail of Firebase
//        String url = "http://www.example.com/verify?uid=" + currentUser.getUid();
//        ActionCodeSettings actionCodeSettings = ActionCodeSettings.newBuilder()
//                .setUrl(url)
//                // The default for this is populated with the current android package name.
//                .setAndroidPackageName("com.example.project", false, null)
//                .build();

        currentUser.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email sent.");
                        }
                    }
                });

        // [START localize_verification_email]
        //        firebaseAuth.setLanguageCode("en");

        // To apply the default app language instead of explicitly setting it.
        // firebaseAuth.useAppLanguage();
        // [END localize_verification_email]
    }

    private void checkAccountFacebook() {
        //Need to disable change avatar when an FB account login?
        accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        if (isLoggedIn) {
            changePasswordBtn.setEnabled(false);
            changePasswordBtn.setVisibility(View.GONE);
        } else {
            changePasswordBtn.setEnabled(true);
        }
    }

    private void handleEventOnClick() {
        changePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingActivity.this, ChangePassword.class));
            }
        });
        handleLogout();
    }

    private void handleLogout() {
        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myProgress.show();

//                Sign out Facebook account
//                get current access token and if exist → logout
                accessToken = AccessToken.getCurrentAccessToken();
                boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
                if (isLoggedIn) {
                    LoginManager.getInstance().logOut();
                }

                //Sign out Firebase account
                if (FirebaseAuth.getInstance() != null) {
                    FirebaseAuth.getInstance().signOut();
                }

                myProgress.dismiss();
                Toast.makeText(getApplicationContext(), "See u!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setUpProgressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setView(R.layout.my_progress_layout);
        myProgress = builder.create();
        myProgress.setCancelable(false);
    }

    private void toChangeAvatar(){
        changeAvatarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this , ChangeAvatar.class);
                startActivity(intent);
            }
        });
    }


    private void setUpActionBar() {
        toolbar = (Toolbar) findViewById(R.id.settingToolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Setting");
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
