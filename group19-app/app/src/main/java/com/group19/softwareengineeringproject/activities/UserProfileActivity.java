package com.group19.softwareengineeringproject.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.group19.softwareengineeringproject.R;
import com.squareup.picasso.Picasso;

import java.net.URL;

import org.json.JSONObject;


//TODO: Make nice (:
public class UserProfileActivity extends AppCompatActivity {


    private final static String URL = "http://10.0.2.2:3000/users/5caa38501e0e36145f87dcaa";
    private ImageView mProfileImageView;
    private TextView mUsernameView;
    private TextView mEventNumberView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);


        mProfileImageView = findViewById(R.id.profile_user_imageview);
        mUsernameView = findViewById(R.id.profile_username_textview);
        mEventNumberView = findViewById(R.id.profile_events_number);

        getRequest();
    }


    //TODO: Async and in helper
    private void getRequest(){

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        Log.d("omgomg", "Posting a request");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
            (Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {

                    try {
                        mUsernameView.setText(response.getString("userName"));
                        mEventNumberView.setText(Integer.toString(response.getInt("eventsAttended")));
                        Log.d("omgomg", "image: "  + response.getString("image"));
                        Picasso.get().load(response.getString("image")).into(mProfileImageView);

                    } catch (Exception e) {
                        Log.d("omgomg", "error " + e.getMessage());
                    }

                    Log.d("omgomg", "We got a response bois");

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("omgomg", error.getMessage());

                    Log.d("omgomg", "rip");
                }
            });


        // Request a string response from the provided URL.
        queue.add(jsonObjectRequest);
	}
}
