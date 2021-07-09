package com.example.parstagramandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PostDetailsActivity extends AppCompatActivity {

    public static final String POST_ID_EXTRA = "postId";
    public static final String TAG = "PostDetailsActivity";
    public static final String PARSEUSER_PICTURE_KEY = "picture";
    Boolean liked;
    TextView tvUserhandle;
    TextView tvCaption;
    TextView tvLikeCount;
    TextView tvTimestamp;
    ImageView ivLike;
    ImageView ivPostPic;
    ImageView ivProfPic;
    Post post;
    List<String> likers;
    int likeCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        String postId = getIntent().getStringExtra(POST_ID_EXTRA);
        liked = false;
        readObject(postId);

        tvCaption = findViewById(R.id.tvCaption);
        tvLikeCount = findViewById(R.id.tvLikeCount);
        tvTimestamp = findViewById(R.id.tvTimestamp);
        ivLike = findViewById(R.id.ivLike);
        ivPostPic = findViewById(R.id.ivPostPic);
        ivProfPic = findViewById(R.id.ivProfPic);
        tvUserhandle = findViewById(R.id.tvUserhandle);

        ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                liked = setLiked(!liked);
            }
        });
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
                ParseFile profileImage = post.getUser().getParseFile(PARSEUSER_PICTURE_KEY);
                if (profileImage == null){
                    Glide.with(PostDetailsActivity.this).load(R.drawable.photo_placeholder).circleCrop().into(ivProfPic);
                } else {
                    Glide.with(PostDetailsActivity.this).load(profileImage.getUrl()).circleCrop().into(ivProfPic);
                }
                ParseFile image = post.getImage();
                if (image != null) {
                    Glide.with(PostDetailsActivity.this).load(image.getUrl()).into(ivPostPic);
                }
                tvUserhandle.setText(post.getUser().getUsername());

                likeCount = post.getLikes();
                tvLikeCount.setText(String.valueOf(likeCount));

                likers = post.getLikers();
                if (likers != null) {
                    if (likers.contains(ParseUser.getCurrentUser().getObjectId())) {
                        Glide.with(PostDetailsActivity.this).load(R.drawable.ufi_heart_active).into(ivLike);
                        liked = true;
                    } else {
                        Glide.with(PostDetailsActivity.this).load(R.drawable.ufi_heart).into(ivLike);
                        liked = false;
                    }
                } else {
                    likers = new ArrayList<String>();
                    Glide.with(PostDetailsActivity.this).load(R.drawable.ufi_heart).into(ivLike);
                    liked = false;
                }
            } else {
                // something went wrong
                Log.d(TAG, "Unable to read post object ", e);
            }
        });
    }

    public boolean setLiked(Boolean likedBool){
        int change;
        if (likedBool) {
            Glide.with(PostDetailsActivity.this).load(R.drawable.ufi_heart_active).into(ivLike);
            likers.add(ParseUser.getCurrentUser().getObjectId());
            likeCount += 1;
        } else {
            Glide.with(PostDetailsActivity.this).load(R.drawable.ufi_heart).into(ivLike);
            likers.remove(ParseUser.getCurrentUser().getObjectId());
            likeCount -= 1;
        }
        tvLikeCount.setText(String.valueOf(likeCount));
        post.setLikes(likeCount);
        post.setLikers(likers);
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
            }
        });
        return likedBool;
    }

}