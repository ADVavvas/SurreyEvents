package com.group19.softwareengineeringproject.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.EventLog;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.FileProvider;

import com.group19.softwareengineeringproject.R;
import com.group19.softwareengineeringproject.helpers.RetrofitManager;
import com.group19.softwareengineeringproject.helpers.UserManager;
import com.group19.softwareengineeringproject.models.MapEvent;
import com.group19.softwareengineeringproject.models.Story;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.group19.softwareengineeringproject.activities.Map.REQUEST_IMAGE_CAPTURE;

public class EventDetailsActivity extends AppCompatActivity {
    private String eventId = "";
    private MapEvent event;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        eventId = getIntent().getStringExtra("eventId");

        TextView eventTitle, eventLocation, eventDate, eventTime, eventDescription;
        ImageView eventImage;

        AppCompatImageButton viewStoryButton, addStoryButton;
        ToggleButton favouriteButton;

        eventTitle = findViewById(R.id.event_title_text);
        eventLocation = findViewById(R.id.event_location_text);
        eventDate = findViewById(R.id.event_date_text);
        eventTime = findViewById(R.id.event_time_text);
        eventDescription = findViewById(R.id.description_body);
        eventImage = findViewById(R.id.event_image);
        viewStoryButton = findViewById(R.id.view_stories_button);
        addStoryButton = findViewById(R.id.add_story_button);
        favouriteButton = findViewById(R.id.favourite_event_button);

        viewStoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchEventStory(eventId);
            }
        });

        addStoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });


        ScaleAnimation scaleAnimation = new ScaleAnimation(0.7f, 1.0f, 0.7f, 1.0f, Animation.RELATIVE_TO_SELF, 0.7f, Animation.RELATIVE_TO_SELF, 0.7f);
        scaleAnimation.setDuration(500);
        BounceInterpolator bounceInterpolator = new BounceInterpolator();
        scaleAnimation.setInterpolator(bounceInterpolator);

        Call<MapEvent> eventCall = RetrofitManager.getInstance().api.getEvent(eventId);
        eventCall.enqueue(new Callback<MapEvent>() {
            @Override
            public void onResponse(Call<MapEvent> call, Response<MapEvent> response) {
                event = response.body();

                eventTitle.setText(event.getTitle());
                //TODO: location from latlng
                eventLocation.setText("University of Surrey");

                DateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy");
                DateFormat timeFormat = new SimpleDateFormat("hh:mm:ss");

                eventDate.setText(dateFormat.format(event.getDate()));
                eventTime.setText(timeFormat.format(event.getDate()));
                eventDescription.setText(event.getDescription());


                Picasso.get().load(event.getImgUrl()).into(eventImage);
                Log.d("RetrofitRequest", "Succ");


                HashMap<String, Object> map = new HashMap<>();
                map.put("userId", UserManager.getInstance().getUser().getId());
                favouriteButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        //animation
                        compoundButton.startAnimation(scaleAnimation);
                        Log.d("endlessloop", "lol");
                        map.put("favourite", isChecked );
                        Call<Boolean> favEventCall = RetrofitManager.getInstance().api.favouriteEvent(event.getId(), map);
                        favEventCall.enqueue(new Callback<Boolean>() {
                            @Override
                            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                                if(response.body() == null) return;
                            }

                            @Override
                            public void onFailure(Call<Boolean> call, Throwable t) {

                            }
                        });
                    }
                });

                Call<Boolean> favouriteCall = RetrofitManager.getInstance().api.getIfFavourite(event.getId(),UserManager.getInstance().getUser().getId());
                favouriteCall.enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                        if(response.body() == null) {
                            return;
                        }
                        favouriteButton.setChecked(response.body());
                    }

                    @Override
                    public void onFailure(Call<Boolean> call, Throwable t) {

                    }
                });
            }

            @Override
            public void onFailure(Call<MapEvent> call, Throwable t) {
                Log.d("RetrofitRequest", "Failed");

            }
        });


        /*
        RequestBody reqBody = RequestBody.create(MediaType.parse("text/plain"), "5cd2de1a6d8cfe73ec02dca9");
        Call<ResponseBody> incrementView = RetrofitManager.getInstance().api.incrementView(eventId, reqBody);
        incrementView.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                Log.d("increment_view", "nice");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("increment_view", "Not nice");
            }
        });
        */
    }

    private void launchEventStory(String eventId) {
        Intent stories = new Intent(this, StoryViewerActivity.class);
        stories.putExtra("eventId", eventId);
        Call<Story> storyCall = RetrofitManager.getInstance().api.getStoryByEventId(eventId);
        storyCall.enqueue(new Callback<Story>() {
            @Override
            public void onResponse(Call<Story> call, Response<Story> response) {

                Story story = (response.body());
                if(story == null || story.getSegments().isEmpty()) {
                    Toast toast = Toast.makeText(EventDetailsActivity.this, "No stories found", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else {
                    startActivity(stories);
                }
            }

            @Override
            public void onFailure(Call<Story> call, Throwable t) {
                Log.d("RetrofitRequest", "Failed");

                Toast toast = Toast.makeText(EventDetailsActivity.this, "There was a problem with stories. Try later.", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }


    private void dispatchTakePictureIntent() {

        if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            Toast toast = Toast.makeText(this, "No camera found!", Toast.LENGTH_SHORT);
            toast.show();
        }

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.d("takephoto", "error");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.group19.softwareengineeringproject.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    String currentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    //TODO: Create story object if 1st story, refactor story object in mongodb
    private void uploadToServer(String filePath) {
        if(eventId == null || eventId.equals("")) return;
        //Create a file object using file path
        File file = new File(filePath);
        // Create a request body with file and image media type
        RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), file);
        // Create MultipartBody.Part using file request-body,file name and part name
        MultipartBody.Part part = MultipartBody.Part.createFormData("photo", file.getName(), fileReqBody);
        //Create request body with text description and text media type
        RequestBody eventId = RequestBody.create(MediaType.parse("text/plain"), this.eventId);
        //
        Call<ResponseBody> call = RetrofitManager.getInstance().api.uploadImage(part, eventId);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("picture_upload", "worked");
                try {
                    Log.d("picture_upload", response.body().string());
                } catch (IOException e) {

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("picture_upload", "didnt work");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                uploadToServer(currentPhotoPath);
            }
        }
    }



}
