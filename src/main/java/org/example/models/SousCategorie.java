package org.example.models;

public class SousCategorie {
    private int id;
    private Categorie categorie;
    private String nom;
    private String description;
    public SousCategorie(){}
    public SousCategorie(int id, Categorie categorie, String nom, String description) {
        this.id = id;
        this.categorie = categorie;
        this.nom = nom;
        this.description = description;
    }
    public SousCategorie(Categorie categorie, String nom, String description) {
        this.categorie = categorie;
        this.nom = nom;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Categorie getCategorie() {
        return categorie;
    }

    public void setCategorie(Categorie categorie) {
        this.categorie = categorie;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "SousCategorie{" +
                "id=" + id +
                ", categorie=" + categorie +
                ", nom='" + nom + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
