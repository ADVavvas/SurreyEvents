package com.group19.softwareengineeringproject.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.group19.softwareengineeringproject.R;
import com.group19.softwareengineeringproject.adapters.SocietyRecyclerViewAdapter;
import com.group19.softwareengineeringproject.helpers.SocViewModel;

public class SubbedSocietiesFragment extends Fragment {
    private RecyclerView socRecyclerView;
    private RecyclerView.Adapter socAdapter;
    private RecyclerView.LayoutManager socLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("fragmentheadache" , "SocOnCreateView");
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.subbed_socs_fragment, container, false);

        socRecyclerView = (RecyclerView) rootView.findViewById(R.id.socs_list);

        socRecyclerView.setHasFixedSize(false);

        socLayoutManager=new LinearLayoutManager(getContext());
        socRecyclerView.setLayoutManager(socLayoutManager);

        SocViewModel model= ViewModelProviders.of(getActivity()).get(SocViewModel.class);
        model.getSocieties().observe(this, item -> {

            socAdapter = new SocietyRecyclerViewAdapter(item);

            socRecyclerView.setAdapter(socAdapter);
        });

        return rootView;
    }

}
