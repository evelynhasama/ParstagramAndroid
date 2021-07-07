package com.example.parstagramandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";
    public static final String FAILED_SIGNUP_TOAST = "Unable to sign up";
    public static final String SUCCESS_SIGNUP_TOAST = "Successfully signed up";
    public static final String FAILED_LOGIN_TOAST = "Invalid login credentials";
    public static final String SUCCESS_LOGIN_TOAST = "Successfully logged in";
    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private TextView tvSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (ParseUser.getCurrentUser() != null){
            goFeedActivity();
        }

        etPassword = findViewById(R.id.etPassword);
        etUsername = findViewById(R.id.etUsername);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignUp = findViewById(R.id.tvSignUp);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                loginUser(username, password);
            }
        });

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                signUpUser(username, password);
            }
        });
    }

    private void loginUser(String username, String password){
        Log.i(TAG, "Attempting to log in "+username);
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Toast.makeText(LoginActivity.this, FAILED_LOGIN_TOAST, Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Issue with login", e);
                    return;
                }
                // navigate to main activity if user has signed in properly
                goFeedActivity();
                Toast.makeText(LoginActivity.this, SUCCESS_LOGIN_TOAST, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void signUpUser(String username, String password){
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e != null) {
                    Toast.makeText(LoginActivity.this, FAILED_SIGNUP_TOAST, Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Issue with sign up", e);
                    return;
                }
                goFeedActivity();
                Toast.makeText(LoginActivity.this, SUCCESS_SIGNUP_TOAST, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void goFeedActivity(){
        Intent intent = new Intent(this, TabbedMainActivity.class);
        startActivity(intent);
        finish();
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
}