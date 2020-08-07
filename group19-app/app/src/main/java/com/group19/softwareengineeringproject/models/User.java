package com.group19.softwareengineeringproject.models;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("_id")
    String id;

    @SerializedName("username")
    String username;

    @SerializedName("userType")
    String type;

    public User() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
