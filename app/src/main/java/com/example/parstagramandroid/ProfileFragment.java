package com.example.parstagramandroid;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.io.IOException;
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
    public static final String PARSEUSER_PICTURE_KEY = "picture";

    public final String APP_TAG = "MyCustomApp";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    public final static String photoFileName = "profile.jpg";

    File photoFile;
    RecyclerView rvUserPosts;
    TextView tvBio;
    TextView tvUser;
    ImageView ivPfP;
    View view;
    List<Post> userPosts;
    ProfilePostsAdapter adapter;
    Button btnEdit;
    ParseUser user;

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
        user = ParseUser.getCurrentUser();
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        rvUserPosts = view.findViewById(R.id.rvUserPosts);
        tvBio = view.findViewById(R.id.tvBio);
        tvUser = view.findViewById(R.id.tvUser);
        ivPfP = view.findViewById(R.id.ivPfP);
        btnEdit = view.findViewById(R.id.btnEdit);

        ParseFile image = user.getParseFile(PARSEUSER_PICTURE_KEY);
        if (image == null){
            Glide.with(view).load(R.drawable.photo_placeholder).circleCrop().into(ivPfP);
        } else {
            Glide.with(view).load(image.getUrl()).circleCrop().into(ivPfP);
        }


        tvBio.setText(user.getString(PARSEUSER_BIO_KEY));
        tvUser.setText(user.getUsername());

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View messageView = LayoutInflater.from(getContext()).
                        inflate(R.layout.dialog_edit_profile, null);
                // Create alert dialog builder
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                alertDialogBuilder.setView(messageView);
                // Create alert dialog
                final AlertDialog alertDialog = alertDialogBuilder.create();
                EditText etUsernameEdit = messageView.findViewById(R.id.etUsernameEdit);
                EditText etBioEdit = messageView.findViewById(R.id.etBioEdit);

                etBioEdit.setText(user.getString(PARSEUSER_BIO_KEY), TextView.BufferType.EDITABLE);
                etUsernameEdit.setText(user.getUsername(), TextView.BufferType.EDITABLE);

                // Configure dialog button
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Save",

                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                String username = etUsernameEdit.getText().toString();
                                String bio = etBioEdit.getText().toString();

                                if (username.isEmpty()) {
                                    Toast.makeText(getContext(), "Username is required", Toast.LENGTH_SHORT);
                                } else {
                                    alertDialog.cancel();
                                    user.setUsername(username);
                                    user.put(PARSEUSER_BIO_KEY, bio);
                                    user.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                alertDialog.dismiss();
                                                tvBio.setText(bio);
                                                tvUser.setText(username);
                                            } else {
                                                Toast.makeText(getContext(), "Failed to save information", Toast.LENGTH_SHORT);
                                            }
                                        }
                                    });
                                }
                            }
                        });

                // Configure dialog button (Cancel)
                alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                // Display the dialog
                alertDialog.show();
            }
        });

        ivPfP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLaunchCamera(v);
            }
        });

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                user.put(PARSEUSER_PICTURE_KEY, new ParseFile(photoFile));
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Bitmap takenImage = rotateBitmapOrientation(photoFile.getAbsolutePath());
                            Glide.with(view).load(takenImage).circleCrop().into(ivPfP);
                        } else {
                            Toast.makeText(getContext(), "Failed to save information", Toast.LENGTH_SHORT);
                        }
                    }
                });
            } else { // Result was a failure
                Toast.makeText(getActivity(), "Image failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public Bitmap rotateBitmapOrientation(String photoFilePath) {
        // Create and configure BitmapFactory
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoFilePath, bounds);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        Bitmap bm = BitmapFactory.decodeFile(photoFilePath, opts);
        // Read EXIF Data
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(photoFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;
        int rotationAngle = 0;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;
        // Rotate Bitmap
        Matrix matrix = new Matrix();
        matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true);
        // Return result
        return rotatedBitmap;
    }

    public void onLaunchCamera(View view) {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(getActivity(), "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(APP_TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

}