package org.example.utils;
public class TempUserStorage {
    private static TempUserStorage instance;
    private String userEmail;

    private TempUserStorage() {}

    public static TempUserStorage getInstance() {
        if (instance == null) {
            instance = new TempUserStorage();
        }
        return instance;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String email) {
        userEmail = email;
    }

    public void clear() {
        userEmail = null;
    }
}

