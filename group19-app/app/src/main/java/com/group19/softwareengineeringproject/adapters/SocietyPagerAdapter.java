package com.group19.softwareengineeringproject.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.group19.softwareengineeringproject.fragments.SubbedSocietiesFragment;

public class SocietyPagerAdapter extends FragmentPagerAdapter {

    public SocietyPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i){
        switch (i) {
            case 0:
                return new SubbedSocietiesFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {return 1;}
}
