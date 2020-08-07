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
import com.group19.softwareengineeringproject.adapters.EventRecyclerViewAdapter;
import com.group19.softwareengineeringproject.deprecated.EventListArrayAdapter;
import com.group19.softwareengineeringproject.helpers.EventViewModel;


public class SubEventFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("fragmentheadache" , "onCreateView");
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.whatshot_event_fragment, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.events_list);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(false);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        EventViewModel model = ViewModelProviders.of(getActivity()).get(EventViewModel.class);
        model.getSubEvents().observe(this, item -> {

            EventListArrayAdapter arrayAdapter;
            mAdapter = new EventRecyclerViewAdapter(item);

            recyclerView.setAdapter(mAdapter);
            Log.d("Observerlul" , "reset adapter");

        });
        return rootView;
    }

}
