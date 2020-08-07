package com.group19.softwareengineeringproject.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.group19.softwareengineeringproject.R;
import com.group19.softwareengineeringproject.helpers.RetrofitManager;
import com.group19.softwareengineeringproject.models.MapEvent;
import com.group19.softwareengineeringproject.models.Story;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

import jp.shts.android.storiesprogressview.StoriesProgressView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StoryViewerActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener {

    //TODO: cache downloads int i = 0 ; final int j = i; or with ViewModel
    //TODO: (maybe) Implement multiple stories
    //TODO: scale image
    //TODO: remove url prefix from imgUrl

    private long pressTime = 0L;
    private long limit = 500L;
    private StoriesProgressView mStoriesProgressView;
    private ImageView mImageView;
    private int counter = 0;

    private Story story;

    Long[] durations;
    String[] urls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getWindow().addFlags(flags);

        setContentView(R.layout.activity_story_viewer);

        mStoriesProgressView = findViewById(R.id.stories);
        mImageView = (ImageView) findViewById(R.id.image);

        // bind reverse view
        View reverse = findViewById(R.id.reverse);
        reverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStoriesProgressView.resume();
                mStoriesProgressView.reverse();
            }
        });
        reverse.setOnTouchListener(onTouchListener);

        // bind skip view
        View skip = findViewById(R.id.skip);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStoriesProgressView.resume();
                mStoriesProgressView.skip();
            }
        });
        skip.setOnTouchListener(onTouchListener);

        Intent intent = getIntent();

        String id = intent.getStringExtra("eventId");

        Call<Story> storyCall = RetrofitManager.getInstance().api.getStoryByEventId(id);
        storyCall.enqueue(new Callback<Story>() {
            @Override
            public void onResponse(Call<Story> call, Response<Story> response) {

                story = (response.body());
                setup();
            }

            @Override
            public void onFailure(Call<Story> call, Throwable t) {
                Log.d("RetrofitRequest", "Failed");

            }
        });



    }

    @Override
    public void onNext() {
        mStoriesProgressView.pause();
        Picasso.get().load(urls[++counter]).into(mImageView, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                mStoriesProgressView.resume();
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    @Override
    public void onPrev() {
        if ((counter - 1) < 0) return;
        mStoriesProgressView.pause();
        Picasso.get().load(urls[--counter]).into(mImageView, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                mStoriesProgressView.resume();
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    @Override
    public void onComplete() {
        finish();
    }

    @Override
    protected void onDestroy() {
        mStoriesProgressView.destroy();
        super.onDestroy();
    }

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    pressTime = System.currentTimeMillis();
                    mStoriesProgressView.pause();
                    return false;
                case MotionEvent.ACTION_UP:
                    long now = System.currentTimeMillis();
                    mStoriesProgressView.resume();
                    return limit < now - pressTime;
            }
            return false;
        }
    };

    private void setup() {

        counter = 0;

        int segCount = story.getSegments().size();

        mStoriesProgressView.setStoriesCount(segCount);

        durations = new Long[segCount];
        urls = new String[segCount];

        int i = 0;
        for(Story.Segment seg : story.getSegments()) {
            durations[i] = seg.getDuration();
            urls[i++] = seg.getUrl();
        }

        mStoriesProgressView.setStoryDuration(3000L);
        mStoriesProgressView.setStoriesListener(this);

        Picasso.get().load(urls[0]).into(mImageView, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                mStoriesProgressView.startStories();
            }

            @Override
            public void onError(Exception e) {
                //TODO: fix error handling
                mStoriesProgressView.startStories(++counter);
            }
        });
    }
}
