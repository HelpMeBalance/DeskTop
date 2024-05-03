package org.example.models;

public class likee {
    private int id;
    private boolean like;

    public likee(int id, boolean like) {
        this.id = id;
        this.like = like;
    }

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }

    public likee() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
