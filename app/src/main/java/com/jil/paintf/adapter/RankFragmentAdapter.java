package com.jil.paintf.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import com.jil.paintf.fragment.DayRankFragment;
import com.jil.paintf.fragment.MonthRankFragment;
import com.jil.paintf.fragment.WeekRankFragment;

/**
 * 2020/8/29 20:44
 *
 * @author JIL
 **/
public class RankFragmentAdapter extends FragmentStatePagerAdapter {
    private String[] title ={"周榜","月榜","新人榜"};
    private int type;
    public RankFragmentAdapter(@NonNull FragmentManager fm,int type) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.type=type;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        switch(position){
            case 1:
                fragment = MonthRankFragment.newInstance(type);
                break;
            case 2:
                fragment = DayRankFragment.newInstance(type);
                break;
            default:
                //0
                fragment = WeekRankFragment.newInstance(type);
                break;
        }
        return fragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return title[position];
    }

    @Override
    public int getCount() {
        return title.length;
    }
}
