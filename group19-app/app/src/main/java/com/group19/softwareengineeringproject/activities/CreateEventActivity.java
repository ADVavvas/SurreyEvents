package com.group19.softwareengineeringproject.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.maps.model.LatLng;
import com.group19.softwareengineeringproject.R;
import com.group19.softwareengineeringproject.helpers.RetrofitManager;
import com.group19.softwareengineeringproject.helpers.UserManager;
import com.group19.softwareengineeringproject.models.EventType;
import com.group19.softwareengineeringproject.models.MapEvent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

/**
 * Created by sam on 2019/4/25.
 */

public class CreateEventActivity extends AppCompatActivity {

    private ImageButton mic;

    private final int REQ_CODE_SPEECH_INPUT = 100;

    private EditText name_input,location_input,description_input;

    private static Calendar eventDate;
    private boolean hasCreatedEvent = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        eventDate = Calendar.getInstance();

        name_input=(EditText) findViewById(R.id.e_title);
        description_input = (EditText) findViewById(R.id.e_description);

        hasCreatedEvent = false;


        Button createEvent = (Button)findViewById(R.id.make_event_button);

        createEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getNewEventInfo();
            }
        });
    }

    private void promptSpeechInput() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,

                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,

                getString(R.string.speech_prompt));

        try {

            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);

        } catch (ActivityNotFoundException a) {

            Toast.makeText(getApplicationContext(),

                    getString(R.string.speech_not_supported),

                    Toast.LENGTH_SHORT).show();

        }

    }



    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    name_input.setText(result.get(0));
                }
                break;
            }
        }
    }

    public void getNewEventInfo(){
        if(hasCreatedEvent) {
            Toast.makeText(getApplicationContext(), "Event already created", Toast.LENGTH_SHORT).show();
            return;
        }
        String eventName =  name_input.getText().toString().trim();
        String eventDescription = description_input.getText().toString().trim();
        LatLng location = (LatLng) getIntent().getParcelableExtra("location");

        if(eventName.isEmpty() || eventDescription.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Missing inputs", Toast.LENGTH_SHORT).show();
            return;
        } else if(Calendar.getInstance().getTimeInMillis() > eventDate.getTimeInMillis()) {
            Toast.makeText(getApplicationContext(), "Cannot create event on past date", Toast.LENGTH_SHORT).show();
            return;
        }

        MapEvent newEvent = new MapEvent();

        newEvent.setTitle(eventName);
        //TODO: get host from username
        newEvent.setHost("UoS");
        //TODO: dropdown
        newEvent.setCategory(EventType.MUSIC.getEventType());
        newEvent.setDescription(eventDescription);
        newEvent.setDate(eventDate.getTime());
        newEvent.setLocation(location);
        //newEvent.setImgUrl("http://10.0.2.2:3000/event1.jpg");
        newEvent.setImgUrl("https://storage.googleapis.com/group19-stories/event1.jpg");


        HashMap<String, Object> map = new HashMap<>();
        hasCreatedEvent = true;
        //RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), file);
        // Create MultipartBody.Part using file request-body,file name and part name
        //MultipartBody.Part part = MultipartBody.Part.createFormData("photo", file.getName(), fileReqBody);
        //Create request body with text description and text media type
        //RequestBody eventId = RequestBody.create(MediaType.parse("text/plain"), "5cd2df4d6d8cfe73ec02dcaa");

        map.put("eventObj", newEvent);
        map.put("userId", UserManager.getInstance().getUser().getId());
        //
        Call<ResponseBody> call = RetrofitManager.getInstance().api.createEvent(map);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("event_upload", "worked");
                finish();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("event_upload", "didnt work");
                hasCreatedEvent=false;
            }
        });
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            eventDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
            eventDate.set(Calendar.MINUTE, minute);
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            eventDate.set(year, month, day);
        }
    }

}