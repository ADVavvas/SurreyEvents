package com.group19.softwareengineeringproject.models;

import android.util.EventLog;

public enum EventType {


    MUSIC("Music"), EDUCATION("Educational"), WHATEVER("Whatever");

    private String type;

    public String getEventType() {
        return this.type;
    }

    private EventType(String type) {
        this.type = type;
    }
}

