package com.group19.softwareengineeringproject.models;

import com.google.gson.annotations.SerializedName;

public class Society {

    @SerializedName("_id")
    private String id;

    @SerializedName("name")
    private String title;

    public Society() {

    }

    public Society(String title) {
        this.title=title;
    }

    public void setTitle(String title) { this.title=title;}
    public void setId(String id) { this.id=id;}

    public String getTitle() { return title;}
    public String getId() { return id;}
}
