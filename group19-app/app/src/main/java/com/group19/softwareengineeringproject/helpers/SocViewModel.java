package com.group19.softwareengineeringproject.helpers;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.group19.softwareengineeringproject.models.Society;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SocViewModel extends ViewModel {

    private MutableLiveData<List<Society>> societies;

    public LiveData<List<Society>> getSocieties() {
        if (societies == null) {
            societies = new MutableLiveData<>();
            loadSocs();
        }
        return societies;
    }

    public void loadSocs() {
        Log.d("loadSocsRetrofitRequest", "SUCC2");
        Call<List<Society>> societyCall = RetrofitManager.getInstance().api.getSocieties();
        societyCall.enqueue(new Callback<List<Society>>() {
           @Override
           public void onResponse(Call<List<Society>> call, Response<List<Society>> response) {
               societies.setValue(response.body());
               Log.d("loadSocsRetrofitRequest", "SUCC");
           }

           @Override
            public void onFailure(Call<List<Society>> call, Throwable t) {
               Log.d("loadSocsRetrofitRequest", "Failed");
           }
        });
    }
}
