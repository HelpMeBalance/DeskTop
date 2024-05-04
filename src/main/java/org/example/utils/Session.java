package org.example.utils;
import org.example.controllers.StoreController;
import org.example.models.Article;
import org.example.models.User;

import javax.mail.Authenticator;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Session {
    private static Session instance;
    private final Map<String, Object> attributes;

    private boolean loggedIn = false;
    private User user;
    private User currentUser;


    private Session() {   attributes = new HashMap<>();
    }

    // Static method to get the instance of the Session class
    public static synchronized Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }



    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn, User user) {
        this.loggedIn = loggedIn;
        this.user = user; // user is the User object of the logged-in user
    }

    public User getUser() {
        return user;
    }

    public void setCurrentUser(User user) {
        currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    public Object getAttribute(String key) {
        return attributes.get(key);
    }
}
