package org.example.models;

public class Article {
    private int id;
    private int categorieId;
    private double prix;
    private String nom;
    private String description;
    private int quantite;
    private String articlePicture;

    // Constructors
    public Article() {
    }

    public Article(int id, int categorieId, double prix, String nom, String description, int quantite, String articlePicture) {
        this.id = id;
        this.categorieId = categorieId;
        this.prix = prix;
        this.nom = nom;
        this.description = description;
        this.quantite = quantite;
        this.articlePicture = articlePicture;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCategorieId() {
        return categorieId;
    }

    public void setCategorie(int categorieId) {
        this.categorieId = categorieId;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
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

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public String getArticlePicture() {
        return articlePicture;
    }

    public void setArticlePicture(String articlePicture) {
        this.articlePicture = articlePicture;
    }


}
