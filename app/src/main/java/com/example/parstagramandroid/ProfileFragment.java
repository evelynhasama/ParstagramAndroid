package com.example.parstagramandroid;

import android.media.Image;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    public static final String TAG = "ProfileFragment";
    public static final String CREATED_AT = "createdAt";
    public static final String PARSEUSER_BIO_KEY = "bio";

    RecyclerView rvUserPosts;
    TextView tvBio;
    TextView tvUser;
    ImageView ivPfP;
    View view;
    List<Post> userPosts;
    ProfilePostsAdapter adapter;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ParseUser user = ParseUser.getCurrentUser();
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        rvUserPosts = view.findViewById(R.id.rvUserPosts);
        tvBio = view.findViewById(R.id.tvBio);
        tvUser = view.findViewById(R.id.tvUser);
        ivPfP = view.findViewById(R.id.ivPfP);
        Glide.with(view).load(R.drawable.instagram_user_filled_24).circleCrop().into(ivPfP);

        tvBio.setText(user.getString(PARSEUSER_BIO_KEY));
        tvUser.setText(user.getUsername());

        userPosts = new ArrayList<>();
        adapter = new ProfilePostsAdapter(getActivity(),userPosts);
        // set the adapter on the recycler view
        rvUserPosts.setAdapter(adapter);
        // set the layout manager on the recycler view
        rvUserPosts.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        // query user's posts from Parstagram
        queryUserPosts(user);

        return view;
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