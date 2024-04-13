package org.example.models;

import java.time.LocalDateTime;

public class Publication {
    private int id;
    private User user;
    private Categorie categorie;
    private SousCategorie sousCategorie;
    private String contenu;
    private Boolean com_ouvert;
    private Boolean anonyme;
    private Boolean valide;

    private LocalDateTime date_c;
    private LocalDateTime date_m;
    private int vues;
    private String titre;
    private String image;

    public Publication() {}
    public Publication(int id, User user, Categorie categorie, SousCategorie sousCategorie, String contenu, Boolean comOuvert, Boolean anonyme, Boolean valide, LocalDateTime dateC, LocalDateTime dateM, int vues, String titre, String image) {
        this.id = id;
        this.user = user;
        this.categorie = categorie;
        this.sousCategorie = sousCategorie;
        this.contenu = contenu;
        com_ouvert = comOuvert;
        this.anonyme = anonyme;
        this.valide = valide;
        date_c = dateC;
        date_m = dateM;
        this.vues = vues;
        this.titre = titre;
        this.image = image;
    }
    public Publication(User user, Categorie categorie, SousCategorie sousCategorie, String titre, String contenu , Boolean comOuvert, Boolean anonyme,String image) {
        this.user = user;
        this.categorie = categorie;
        this.sousCategorie = sousCategorie;
        this.contenu = contenu;
        com_ouvert = comOuvert;
        this.anonyme = anonyme;
        this.valide = false;
        date_c = LocalDateTime.now();
        date_m = LocalDateTime.now();
        this.vues = 0;
        this.titre = titre;
        this.image = image;
    }
    public Publication(User user, Categorie categorie, SousCategorie sousCategorie, String titre, String contenu , Boolean comOuvert, Boolean anonyme) {
        this.user = user;
        this.categorie = categorie;
        this.sousCategorie = sousCategorie;
        this.contenu = contenu;
        com_ouvert = comOuvert;
        this.anonyme = anonyme;
        this.valide = false;
        date_c = LocalDateTime.now();
        date_m = LocalDateTime.now();
        this.vues = 0;
        this.titre = titre;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public Boolean getCom_ouvert() {
        return com_ouvert;
    }

    public void setCom_ouvert(Boolean com_ouvert) {
        this.com_ouvert = com_ouvert;
    }

    public Boolean getAnonyme() {
        return anonyme;
    }

    public void setAnonyme(Boolean anonyme) {
        this.anonyme = anonyme;
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

    public int getVues() {
        return vues;
    }

    public void setVues(int vues) {
        this.vues = vues;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Categorie getCategorie() {
        return categorie;
    }

    public void setCategorie(Categorie categorie) {
        this.categorie = categorie;
    }

    public SousCategorie getSous_categorie() {
        return sousCategorie;
    }

    public void setSous_categorie(SousCategorie sousCategorie) {
        this.sousCategorie = sousCategorie;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    @Override
    public String toString() {
        return "Publication{" +
                "id=" + id +
                ", user_id=" + user +
                ", categorie_id=" + categorie +
                ", sousCategorie_id=" + sousCategorie +
                ", contenu='" + contenu + '\'' +
                ", com_ouvert=" + com_ouvert +
                ", anonyme=" + anonyme +
                ", valide=" + valide +
                ", date_c=" + date_c +
                ", date_m=" + date_m +
                ", vues=" + vues +
                ", titre='" + titre + '\'' +
                ", image='" + image + '\'' +
                '}';
    }

}
