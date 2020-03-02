package com.jil.paintf.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import com.jil.paintf.fragment.AboutFragment;
import com.jil.paintf.fragment.SettingFragment;

public class SettingPagerAdapter extends FragmentPagerAdapter {
    private String[] title ={"设置","关于","最热"};
    public SettingPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                return SettingFragment.Companion.newInstance();
            case 1:
                return AboutFragment.Companion.newInstance();
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return title[position];
    }

    @Override
    public int getCount() {
        return 2;
    }
}
