package org.example.models;

public class Like {
    private int id;
    private User user;
    private Publication publication;
    public Like(){}
    public Like(int id, User user, Publication publication) {
        this.id = id;
        this.user = user;
        this.publication = publication;
    }
    public Like(User user, Publication publication) {
        this.user = user;
        this.publication = publication;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Publication getPublication() {
        return publication;
    }

    public void setPublication(Publication publication) {
        this.publication = publication;
    }

    @Override
    public String toString() {
        return "Like{" +
                "id=" + id +
                ", user=" + user +
                ", publication=" + publication +
                '}';
    }
}
