package com.group19.softwareengineeringproject.deprecated;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.group19.softwareengineeringproject.R;

public class TestFragment extends Fragment {

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    ViewGroup rootView = (ViewGroup) inflater.inflate(
        R.layout.test_fragment, container, false);

    return rootView;
  }
}

