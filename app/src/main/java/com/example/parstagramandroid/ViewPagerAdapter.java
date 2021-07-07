package com.example.parstagramandroid;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import org.jetbrains.annotations.NotNull;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public static final String TAG = "ViewPagerAdapter";

    public ViewPagerAdapter(@NonNull @NotNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @org.jetbrains.annotations.NotNull
    @Override
    public Fragment createFragment(int position) {
        Log.d(TAG, "Create fragment: "+ position);
        switch (position) {
            case 0:
                return new FeedFragment();
            case 1:
                return new MainFragment();
            case 2:
                return new SearchFragment();
            case 3:
                return new ProfileFragment();
            default:
                return new ProfileFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
