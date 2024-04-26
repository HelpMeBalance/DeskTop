package org.example.models;

import java.time.LocalDateTime;

public class PasswordResetToken {
    private int id;
    private int userId;
    private String token;
    private LocalDateTime expiresAt;

    public PasswordResetToken() {
    }

    public PasswordResetToken(int id, int userId, String token, LocalDateTime expiresAt) {
        this.id = id;
        this.userId = userId;
        this.token = token;
        this.expiresAt = expiresAt;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    // toString method for debugging
    @Override
    public String toString() {
        return "PasswordResetToken{" +
                "id=" + id +
                ", userId=" + userId +
                ", token='" + token + '\'' +
                ", expiresAt=" + expiresAt +
                '}';
    }
}
