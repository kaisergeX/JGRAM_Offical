package com.example.project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavHost;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.project.modules.SearchActivity;
import com.example.project.modules.Upload;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import static androidx.navigation.ui.NavigationUI.setupActionBarWithNavController;

public class JGramActivity extends AppCompatActivity {

    private BottomNavigationView bottom_navigation;
    private Toolbar toolbar;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jgram);

        process();
    }

    private void process() {
        setUpBottomNavigation();
    }


    private void setUpBottomNavigation() {
        toolbar = findViewById(R.id.jgramToolbar);
        setSupportActionBar(toolbar);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.home, R.id.upload, R.id.profile)
                .build();
        navController = Navigation.findNavController(this, R.id.my_nav_host_mainfragment);
        bottom_navigation = findViewById(R.id.bottom_navigation);

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(bottom_navigation, navController);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                startActivity(new Intent(JGramActivity.this, SearchActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);


    }
}
