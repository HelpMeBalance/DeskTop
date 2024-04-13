package org.example.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class User {
    private int id;
    private String email;

    private List<String> roles;
    private String password;
    private String firstname;
    private String lastname;
    private String profile_picture;
    private Boolean is_banned;
    private LocalDateTime ban_expires_at;
    private LocalDateTime created_at;
    private String google_id;

    public User(int id, String email, List<String> roles, String password, String firstname, String lastname, String profilePicture, Boolean isBanned, LocalDateTime banExpiresAt, LocalDateTime createdAt, String googleId) {
        this.id = id;
        this.email = email;
        this.roles = roles;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        profile_picture = profilePicture;
        is_banned = isBanned;
        ban_expires_at = banExpiresAt;
        created_at = createdAt;
        google_id = googleId;
    }
    public User(String email,String password, String firstname, String lastname) {
        this.email = email;
        this.roles = Arrays.asList("ROLE_USER");
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        is_banned = false;
        created_at = LocalDateTime.now();
    }

    public User() {
    }
    public User(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getRoles() {
        return roles;
    }
    public void setRoles(List<String> roles) {
        this.roles = new ArrayList<>(roles);
        if(!this.roles.contains("ROLE_USER"))this.roles.add("ROLE_USER");
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
    }

    public Boolean getIs_banned() {
        return is_banned;
    }

    public void setIs_banned(Boolean is_banned) {
        this.is_banned = is_banned;
    }

    public LocalDateTime getBan_expires_at() {
        return ban_expires_at;
    }

    public void setBan_expires_at(LocalDateTime ban_expires_at) {
        this.ban_expires_at = ban_expires_at;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public String getGoogle_id() {
        return google_id;
    }

    public void setGoogle_id(String google_id) {
        this.google_id = google_id;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", roles=" + roles +
                ", password='" + password + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", profile_picture='" + profile_picture + '\'' +
                ", is_banned=" + is_banned +
                ", ban_expires_at=" + ban_expires_at +
                ", created_at=" + created_at +
                ", google_id='" + google_id + '\'' +
                '}';
    }
}
