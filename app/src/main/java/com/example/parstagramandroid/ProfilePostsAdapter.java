package com.example.parstagramandroid;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.parse.ParseFile;

import java.util.List;

public class ProfilePostsAdapter extends RecyclerView.Adapter<ProfilePostsAdapter.ViewHolder> {

    public static final String TAG = "ProfilePostsAdapter";
    public static final String POST_ID_EXTRA = "postId";
    private Context context;
    private List<Post> posts;

    public ProfilePostsAdapter(Context context, List<Post> posts) {
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
            Log.d(TAG, "onCreateViewHolder");
            View view = LayoutInflater.from(context).inflate(R.layout.item_profile_post, parent, false);
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

        private ImageView ivPostPicture;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPostPicture = itemView.findViewById(R.id.ivPostPicture);

            itemView.setOnClickListener(this);
        }

        public void bind(Post post) {
            // Bind the post data to the view elements
            ParseFile image = post.getImage();
            if (image != null) {
                Glide.with(context).load(image.getUrl()).centerCrop().into(ivPostPicture);
            }
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


