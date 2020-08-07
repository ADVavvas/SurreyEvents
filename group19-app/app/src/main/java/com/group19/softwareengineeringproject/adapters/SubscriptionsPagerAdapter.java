package com.group19.softwareengineeringproject.adapters;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.group19.softwareengineeringproject.fragments.SubscriptionsList;
import com.group19.softwareengineeringproject.deprecated.TestFragment;
import com.group19.softwareengineeringproject.models.SubscriptionItem;

import java.util.Arrays;

public class SubscriptionsPagerAdapter extends FragmentPagerAdapter {
  private static final String subs[] = new String[] {"A", "B", "C"};

  private final SubscriptionsList subscriptionsList = SubscriptionsList.newInstance(new SubscriptionItem(Arrays.asList(subs)));


  public SubscriptionsPagerAdapter(FragmentManager fm) {
    super(fm);
  }

  @Override
  public Fragment getItem(int i) {
    switch (i) {
      case 0:
        return subscriptionsList;
      case 1:
        return new TestFragment();
      case 2:
        return new TestFragment();
      default:
        return null;
    }
  }

  @Override
  public int getCount() {
    return 3;
  }

  @Nullable
  @Override
  public CharSequence getPageTitle(int position) {
    switch (position) {
      case 0:
        return "one";
      case 1:
        return "two";
      case 2:
        return "three";
      default:
        return "null";
    }
  }
}
