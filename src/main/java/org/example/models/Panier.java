package org.example.models;

import java.util.ArrayList;
import java.util.List;

public class Panier {
    private int id;
    private double prix_tot;
    private int quantity;
    private int article_id;
    private int user_id;
    private List<Article> items;

    public Panier() {
        this.items = new ArrayList<>();
    }

    // Getters and setters for the attributes
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getPrix_tot() {
        return prix_tot;
    }

    public void setPrix_tot(double prix_tot) {
        this.prix_tot = prix_tot;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getArticle_id() {
        return article_id;
    }

    public void setArticle_id(int article_id) {
        this.article_id = article_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

// Method to add an article to the cart or update its quantity if already added
// Method to add an article to the cart or update its quantity if already added
public void addItem(Article article) {
    boolean found = false;
    for (Article item : items) {
        if (item.getId() == article.getId()) {
            item.setQuantite(item.getQuantite() + 1);
            found = true;
            break;
        }
    }
    if (!found) {
        article.setQuantite(1);
        items.add(article);
    }
}

    // Method to remove an article from the cart
    public void removeItem(Article article) {
        items.remove(article);
    }

    // Method to get the total price of items in the cart
    public double getTotalPrice() {
        double totalPrice = 0.0;
        for (Article article : items) {
            totalPrice += article.getPrix() * article.getQuantite();
        }
        return totalPrice;
    }

    // Method to clear all items from the cart
    public void clear() {
        items.clear();
    }

    // Method to get the total number of items in the cart
    public int getItemCount() {
        return items.size();
    }

    // Method to get the list of items in the cart
    public List<Article> getItems() {
        return items;
    }


    // You can add more methods as needed, such as updating quantities, etc.
}
