package com.jil.paintf.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import com.jil.paintf.fragment.MainFragment;
import com.jil.paintf.fragment.UserFragment;

public class UserPagerAdapter extends FragmentPagerAdapter {
    private String[] title ={"全部","插画","照片"};
    private int uid;
    private int[] count;
    public UserPagerAdapter(@NonNull FragmentManager fm,int uid,int[] count) {
        super(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.uid=uid;
        this.count=count;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return title[position]+"("+count[position]+")";
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return UserFragment.Companion.newInstance(position,uid);
    }

    @Override
    public int getCount() {
        return 3;
    }
}
