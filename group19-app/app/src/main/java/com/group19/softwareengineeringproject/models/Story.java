package com.group19.softwareengineeringproject.models;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class Story {

    public class Segment {
        @SerializedName("url")
        private String url;
        @SerializedName("duration")
        private Long duration;

        public Segment(String url, Long duration) {
            this.url = url;
            this.duration = duration;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public Long getDuration() {
            return duration;
        }

        public void setDuration(Long duration) {
            this.duration = duration;
        }
    }

    @SerializedName("user")
    private String userId;
    @SerializedName("event")
    private String eventId;
    @SerializedName("post_time")
    private Date postDate;
    @SerializedName("segments")
    private List<Segment> segments;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public Date getPostDate() {
        return postDate;
    }

    public void setPostDate(Date postDate) {
        this.postDate = postDate;
    }

    public List<Segment> getSegments() {
        return segments;
    }

    public void setSegments(List<Segment> segments) {
        this.segments = segments;
    }
}

