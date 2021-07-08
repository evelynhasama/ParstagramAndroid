package com.example.parstagramandroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    public static final String TAG = "ProfileActivity";
    public static final String CREATED_AT = "createdAt";
    public static final String PARSEUSER_BIO_KEY = "bio";
    public static final String USER_KEY = "user";

    RecyclerView rvUserPosts;
    TextView tvBio;
    TextView tvUser;
    ImageView ivPfP;
    List<Post> userPosts;
    ProfilePostsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ParseUser user = getIntent().getParcelableExtra(USER_KEY);

        rvUserPosts = findViewById(R.id.rvUserPostsAct);
        tvBio = findViewById(R.id.tvBioAct);
        tvUser = findViewById(R.id.tvUserAct);
        ivPfP = findViewById(R.id.ivPfPAct);
        Glide.with(ProfileActivity.this).load(R.drawable.photo_placeholder).circleCrop().into(ivPfP);

        tvBio.setText(user.getString(PARSEUSER_BIO_KEY));
        tvUser.setText(user.getUsername());

        userPosts = new ArrayList<>();
        adapter = new ProfilePostsAdapter(ProfileActivity.this,userPosts);
        // set the adapter on the recycler view
        rvUserPosts.setAdapter(adapter);
        // set the layout manager on the recycler view
        rvUserPosts.setLayoutManager(new GridLayoutManager(ProfileActivity.this, 3));
        // query user's posts from Parstagram
        queryUserPosts(user);
    }

    private void queryUserPosts(ParseUser user) {

        // specify what type of data we want to query - Post.class
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.setLimit(20);
        query.addDescendingOrder(CREATED_AT);
        query.whereEqualTo("user", user);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                // check for errors
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                // save received posts to list and notify adapter of new data
                userPosts.addAll(posts);
                Log.d(TAG, "queryUserPosts size: "+ userPosts.size());
                adapter.notifyDataSetChanged();
            }
        });
    }
}