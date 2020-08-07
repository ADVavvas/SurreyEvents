package com.group19.softwareengineeringproject.models;

import com.google.gson.annotations.SerializedName;

public class LoginResult{

    @SerializedName("result")
    String result;

    @SerializedName("id")
    String id;

    public LoginResult() {}


    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
