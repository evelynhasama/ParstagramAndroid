package com.example.parstagramandroid;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.w3c.dom.Text;
import org.w3c.dom.UserDataHandler;

import java.util.ArrayList;
import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    public static final String POST_ID_EXTRA = "postId";
    public static final String PARSEUSER_PICTURE_KEY = "picture";
    public static final String USER_EXTRA = "user";
    private Context context;
    private List<Post> posts;

    public PostsAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvUsername;
        private ImageView ivImage;
        private TextView tvDescription;
        private ImageView ivProfileImage;
        private TextView tvLikes;
        private ImageView ivLike;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvLikes = itemView.findViewById(R.id.tvLikes);
            ivLike = itemView.findViewById(R.id.ivLikeHeart);

            itemView.setOnClickListener(this);
        }

        public void bind(Post post) {
            // Bind the post data to the view elements
            tvDescription.setText(post.getDescription());
            tvUsername.setText(post.getUser().getUsername());
            ParseFile image = post.getImage();
            if (image != null) {
                Glide.with(context).load(image.getUrl()).into(ivImage);
            }
            int likeCount = post.getLikes();
            tvLikes.setText(String.valueOf(likeCount));

            List<String> likers = post.getLikers();
            if (likers != null && likers.contains(ParseUser.getCurrentUser().getObjectId())) {
                // if the user likes the image
                Glide.with(context).load(R.drawable.ufi_heart_active).into(ivLike);
            } else {
                Glide.with(context).load(R.drawable.ufi_heart).into(ivLike);
            }

            ParseFile profileImage = post.getUser().getParseFile(PARSEUSER_PICTURE_KEY);
            if (profileImage == null){
                Glide.with(context).load(R.drawable.photo_placeholder).circleCrop().into(ivProfileImage);
            } else {
                Glide.with(context).load(profileImage.getUrl()).circleCrop().into(ivProfileImage);
            }

            ivProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ProfileActivity.class);
                    intent.putExtra(USER_EXTRA, post.getUser());
                    context.startActivity(intent);
                }
            });
        }

        @Override
        public void onClick(View v) {
            // gets item position
            int position = getAdapterPosition();
            // make sure the position is valid, i.e. actually exists in the view
            if (position != RecyclerView.NO_POSITION) {
                Post post = posts.get(position);
                Intent intent = new Intent(context, PostDetailsActivity.class);
                intent.putExtra(POST_ID_EXTRA, post.getObjectId());
                context.startActivity(intent);
            }
        }
    }
}
