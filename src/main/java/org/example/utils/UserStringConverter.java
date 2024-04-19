package org.example.utils;

import javafx.util.StringConverter;
import org.example.models.User;

public class UserStringConverter extends StringConverter<User> {
    @Override
    public String toString(User user) {
        if (user != null) {
            return user.getFirstname() + " " + user.getLastname();
        }
        return null;
    }

    @Override
    public User fromString(String s) {
        return null;
    }
}
