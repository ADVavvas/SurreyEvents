package com.group19.softwareengineeringproject.helpers;

import com.group19.softwareengineeringproject.models.LoginResult;
import com.group19.softwareengineeringproject.models.MapEvent;
import com.group19.softwareengineeringproject.models.Society;
import com.group19.softwareengineeringproject.models.Story;
import com.group19.softwareengineeringproject.models.User;

import java.util.HashMap;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface cwAPI {

    @GET("/events")
    Call<List<MapEvent>> getEvents();

    @GET("/events/{eventId}")
    Call<MapEvent> getEvent(@Path("eventId") String eventId);

    @GET("/stories")
    Call<List<Story>> getStories();


    @GET("/stories/event/{eventId}")
    Call<Story> getStoryByEventId(@Path("eventId") String eventId);

    @Multipart
    @POST("/upload")
    Call<ResponseBody> uploadImage(@Part MultipartBody.Part file,
                                   @Part("name") RequestBody requestBody);

    @POST("/events/create")
    Call<ResponseBody> createEvent(@Body HashMap<String, Object> map);

    @PUT("/events/view/{eventId}")
    Call<ResponseBody> incrementView(@Path("eventId") String eventId, @Body HashMap<String, Object> requestBody);

    @PUT("/events/favourite/{eventId}")
    Call<Boolean> favouriteEvent(@Path("eventId") String eventId, @Body HashMap<String, Object> requestBody);

    @GET("/events/favourite/{eventId}/{userId}")
    Call<Boolean> getIfFavourite(@Path("eventId") String eventId, @Path("userId") String userId);

    @PUT("/login")
    Call<LoginResult> login(@Body HashMap<String, Object> requestBody);

    @PUT("/register")
    Call<LoginResult> register(@Body HashMap<String, Object> requestBody);

    @GET("/users/{userId}")
    Call<User> getUser(@Path("userId") String userId);

    @GET("/societies")
    Call<List<Society>> getSocieties();

    @GET("/hot")
    Call<List<MapEvent>> getHot();

    @PUT("/sub/{societyId}")
    Call<Boolean> sub(@Path("societyId") String socId, @Body HashMap<String, Object> requestBody);

    @GET("/societies/{socId}/{userId}")
    Call<Boolean> isSubbed(@Path("socId") String socId, @Path("userId")String userId);

    @GET("/events/subs/{userId}")
    Call<List<MapEvent>> getSubMapEvents(@Path("userId") String userId);
}
