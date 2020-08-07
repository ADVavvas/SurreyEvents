package com.group19.softwareengineeringproject.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class MapEvent {

    @SerializedName("_id")
    private String id;
    @SerializedName("title")
    private String title;
    @SerializedName("host")
    private String host;
    @SerializedName("thumbnail")
    private String imgUrl;
    @SerializedName("category")
    private String category;
    @SerializedName("date")
    private Date date;
    @SerializedName("description")
    private String description;
    @SerializedName("loc")
    private LatLng location;
    @SerializedName("society")
    private String socId;


    public MapEvent() {

    }

    public MapEvent(String title, String host, String imgUrl, String category) {
        this.host = host;

        this.title = title;
        this.imgUrl = imgUrl;
        this.category = category;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public String getHost() {
        return host;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getCategory() {
        return category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public String getSocId() {
        return socId;
    }

    public void setSocId(String socId) {
        this.socId = socId;
    }
}
