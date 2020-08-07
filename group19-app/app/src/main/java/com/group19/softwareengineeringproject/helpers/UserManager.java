package com.group19.softwareengineeringproject.helpers;

import com.group19.softwareengineeringproject.models.User;

public class UserManager {

    private static UserManager instance;
    private User user;

    public static UserManager getInstance() {
        if(instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    private UserManager() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User u) {
        user = u;
    }
}
