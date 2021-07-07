package com.example.parstagramandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;


public class TabbedMainActivity extends AppCompatActivity {
    public static final String TAG = "TabbedMainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed_main);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        final FragmentManager fragmentManager = getSupportFragmentManager();

        // define your fragments here
        final Fragment fragment1 = new FeedFragment();
        final Fragment fragment2 = new MainFragment();
        final Fragment fragment3 = new ProfileFragment();

        // handle navigation selection
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment fragment;
                        switch (item.getItemId()) {
                            case R.id.action_feed:
                                fragment = fragment1;
                                break;
                            case R.id.action_post:
                                fragment = fragment2;
                                break;
                            case R.id.action_profile:
                            default:
                                fragment = fragment3;
                                break;
                        }
                        fragmentManager.beginTransaction().replace(R.id.fragContainer, fragment).commit();
                        return true;
                    }
                });
        // Set default selection
        bottomNavigationView.setSelectedItemId(R.id.action_feed);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate menu
        getMenuInflater().inflate(R.menu.main_menu, menu);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setIcon(R.drawable.nav_logo_whiteout);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            ParseUser.logOut();
            ParseUser currentUser = ParseUser.getCurrentUser();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


}