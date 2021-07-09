package com.example.parstagramandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Date;

public class PostDetailsActivity extends AppCompatActivity {

    public static final String POST_ID_EXTRA = "postId";
    public static final String TAG = "PostDetailsActivity";
    TextView tvUserhandle;
    TextView tvCaption;
    TextView tvLikeCount;
    TextView tvTimestamp;
    ImageView ivLike;
    ImageView ivPostPic;
    ImageView ivProfPic;
    Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        String postId = getIntent().getStringExtra(POST_ID_EXTRA);

        readObject(postId);

        tvCaption = findViewById(R.id.tvCaption);
        tvLikeCount = findViewById(R.id.tvLikeCount);
        tvTimestamp = findViewById(R.id.tvTimestamp);
        ivLike = findViewById(R.id.ivLike);
        ivPostPic = findViewById(R.id.ivPostPic);
        ivProfPic = findViewById(R.id.ivProfPic);
        tvUserhandle = findViewById(R.id.tvUserhandle);
    }

    public void readObject(String postId) {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.getInBackground(postId, (object, e) -> {
            if (e == null) {
                post = object;
                tvCaption.setText(post.getDescription());
                Date createdAt = post.getCreatedAt();
                String timeAgo = Post.calculateTimeAgo(createdAt);
                tvTimestamp.setText(timeAgo);
                Glide.with(PostDetailsActivity.this).load(R.drawable.photo_placeholder).circleCrop().into(ivProfPic);
                ParseFile image = post.getImage();
                if (image != null) {
                    Glide.with(PostDetailsActivity.this).load(image.getUrl()).into(ivPostPic);
                }
                tvUserhandle.setText(post.getUser().getUsername());
                tvLikeCount.setText(String.valueOf(post.getLikes()));
            } else {
                // something went wrong
                Log.d(TAG, "Unable to read post object ", e);
            }
        });
    }
}