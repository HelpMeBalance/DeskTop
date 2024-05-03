package org.example.utils;
import org.example.models.User;

import javax.mail.Authenticator;
import java.util.Properties;

public class Session {
    private static Session instance;
    private boolean loggedIn = false;
    private User user;

    private Session() {}

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
}
