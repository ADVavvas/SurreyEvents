package com.group19.softwareengineeringproject.helpers;


import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.group19.softwareengineeringproject.models.MapEvent;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventViewModel extends ViewModel {

    private MutableLiveData<List<MapEvent>> events;
    private MutableLiveData<List<MapEvent>> hot;
    private MutableLiveData<List<MapEvent>> subEvents;

    public LiveData<List<MapEvent>> getEvents() {
        if (events == null) {
            events = new MutableLiveData<>();
            loadEvents();
        }
        return events;
    }


    public LiveData<List<MapEvent>> getHot() {
        if (hot == null) {
            hot = new MutableLiveData<>();
            loadHotEvents();
        }
        return hot;
    }

    public LiveData<List<MapEvent>> getSubEvents() {
        if (subEvents == null) {
            subEvents = new MutableLiveData<>();
            loadSubEvents();
        }
        return subEvents;
    }


    public void loadEvents() {
        Call<List<MapEvent>> eventCall = RetrofitManager.getInstance().api.getEvents();
        eventCall.enqueue(new Callback<List<MapEvent>>() {
            @Override
            public void onResponse(Call<List<MapEvent>> call, Response<List<MapEvent>> response) {
                events.setValue(response.body());
                Log.d("RetrofitRequest", "Succ");
            }

            @Override
            public void onFailure(Call<List<MapEvent>> call, Throwable t) {
                Log.d("RetrofitRequest", "Failed");

            }
        });
    }

    public void loadHotEvents() {
        Call<List<MapEvent>> eventCall = RetrofitManager.getInstance().api.getHot();
        eventCall.enqueue(new Callback<List<MapEvent>>() {
            @Override
            public void onResponse(Call<List<MapEvent>> call, Response<List<MapEvent>> response) {
               hot.setValue(response.body());
            }

            @Override
            public void onFailure(Call<List<MapEvent>> call, Throwable t) {

            }
        });
    }

    public void loadSubEvents() {
        Call<List<MapEvent>> eventCall = RetrofitManager.getInstance().api.getSubMapEvents(UserManager.getInstance().getUser().getId());
        eventCall.enqueue(new Callback<List<MapEvent>>() {
            @Override
            public void onResponse(Call<List<MapEvent>> call, Response<List<MapEvent>> response) {
                subEvents.setValue(response.body());
            }

            @Override
            public void onFailure(Call<List<MapEvent>> call, Throwable t) {

            }
        });

    }

}
