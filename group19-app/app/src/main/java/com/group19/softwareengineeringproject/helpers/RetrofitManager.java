package com.group19.softwareengineeringproject.helpers;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitManager {

    private Retrofit retrofit = new Retrofit.Builder()
            //.baseUrl("http://10.0.2.2:8080")
            //.baseUrl("http://192.168.0.105:3000")
            .baseUrl("https://group19-237313.appspot.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public cwAPI api = retrofit.create(cwAPI.class);

    private static RetrofitManager instance;

    public static RetrofitManager getInstance() {
       if(instance == null) {
           instance = new RetrofitManager();
       }
       return instance;
    }


}
