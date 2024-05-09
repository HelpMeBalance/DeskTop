package org.example.models;

public class CategorieProduit {
    private int id;
    private String nom;

    // Constructors
    public CategorieProduit() {
    }

    public CategorieProduit(int id, String nom) {
        this.id = id;
        this.nom = nom;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getCategorieId() {
return id;
    }

    @Override
    public String toString() {
        return nom;


    }

    public double getPrix() {
return 0;
    }

    public String getArticlePicture() {
return null;
    }

    public int getQuantite() {
return 0;
    }

    public String getDescription() {
return null;
    }
}
