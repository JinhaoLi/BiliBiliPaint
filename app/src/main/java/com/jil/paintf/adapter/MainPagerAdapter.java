package com.jil.paintf.adapter;

import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import com.jil.paintf.fragment.MainFragment;
import com.jil.paintf.fragment.RankFragment;

public class MainPagerAdapter extends FragmentPagerAdapter {
    private String[] title ={"推荐","最新","最热","榜单"};
    private int type;

    public MainPagerAdapter(@NonNull FragmentManager fm,int type) {
        super(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.type=type;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return title[position];
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if(position==3){
            return RankFragment.Companion.newInstance(type,"");
        }
        if(type==0)
            return MainFragment.Companion.newInstance(position);
        else
            return MainFragment.Companion.newInstance(position+3);
    }

    @Override
    public int getCount() {
        return title.length;
    }
}
