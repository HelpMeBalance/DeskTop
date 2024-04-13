package org.example.models;

import java.time.LocalDateTime;

public class Commentaire {
    private int id;
    private User user;
    private Publication publication;
    private String contenu;
    private Boolean anonyme;
    private int likes;
    private Boolean valide;
    private LocalDateTime date_c;
    private LocalDateTime date_m;
    public Commentaire(){}
    public Commentaire(int id, User user, Publication publication, String contenu, Boolean anonyme, int likes, Boolean valide, LocalDateTime dateC, LocalDateTime dateM) {
        this.id = id;
        this.user = user;
        this.publication = publication;
        this.contenu = contenu;
        this.anonyme = anonyme;
        this.likes = likes;
        this.valide = valide;
        date_c = dateC;
        date_m = dateM;
    }
    public Commentaire(User user, Publication publication, String contenu, Boolean anonyme) {
        this.user = user;
        this.publication = publication;
        this.contenu = contenu;
        this.anonyme = anonyme;
        this.likes = 0;
        this.valide = false;
        date_c = LocalDateTime.now();
        date_m = LocalDateTime.now();
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

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public Boolean getAnonyme() {
        return anonyme;
    }

    public void setAnonyme(Boolean anonyme) {
        this.anonyme = anonyme;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public Boolean getValide() {
        return valide;
    }

    public void setValide(Boolean valide) {
        this.valide = valide;
    }

    public LocalDateTime getDate_c() {
        return date_c;
    }

    public void setDate_c(LocalDateTime date_c) {
        this.date_c = date_c;
    }

    public LocalDateTime getDate_m() {
        return date_m;
    }

    public void setDate_m(LocalDateTime date_m) {
        this.date_m = date_m;
    }

    @Override
    public String toString() {
        return "Commentaire{" +
                "id=" + id +
                ", user=" + user +
                ", publication=" + publication +
                ", contenu='" + contenu + '\'' +
                ", anonyme=" + anonyme +
                ", likes=" + likes +
                ", valide=" + valide +
                ", date_c=" + date_c +
                ", date_m=" + date_m +
                '}';
    }
}
