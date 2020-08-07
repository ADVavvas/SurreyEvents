package com.group19.softwareengineeringproject.adapters;


import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.group19.softwareengineeringproject.fragments.HotEventFragment;
import com.group19.softwareengineeringproject.fragments.SubEventFragment;

public class EventPagerAdapter extends FragmentPagerAdapter {

    public EventPagerAdapter(FragmentManager fm) {
      super(fm);
    }

    @Override
    public Fragment getItem(int i) {
      switch (i) {
        case 0:
            return new HotEventFragment();
        case 1:
            return new SubEventFragment();
        default:
            return null;
      }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Hot";
            case 1:
                return "Subs";
            default:
                return null;
        }
    }
}
