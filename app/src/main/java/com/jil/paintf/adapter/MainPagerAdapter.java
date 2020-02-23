package com.jil.paintf.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import com.jil.paintf.fragment.MainFragment;

public class MainPagerAdapter extends FragmentPagerAdapter {
    private String[] title ={"推荐","最新","最热","推荐","最新","最热"};

    public MainPagerAdapter(@NonNull FragmentManager fm) {
        super(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return title[position];
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return MainFragment.Companion.newInstance(position);
    }

    @Override
    public int getCount() {
        return 6;
    }
}
