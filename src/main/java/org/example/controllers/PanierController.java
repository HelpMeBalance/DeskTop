package org.example.controllers;

import javafx.scene.control.Alert;
import org.example.models.Article;
import org.example.models.Panier;
import org.example.models.User;
import org.example.service.ArticleService;
import org.example.service.PanierService;
import org.example.service.UserService;
import org.example.utils.Session;


import java.sql.SQLException;
import java.util.List;

public class PanierController {
    private final PanierService panierService = new PanierService();
    private final ArticleService articleService = new ArticleService();
    private PanierController panierController;

    public void setPanierController(PanierController panierController) {
        this.panierController = panierController;
    }


    private UserService userService = new UserService();

    public void addToCart(int articleId, int id) {
        try {
            // Get the user ID from the session
            int userId = Session.getInstance().getUser() != null ? Session.getInstance().getUser().getId() : -1;

            if (Session.getInstance().getUser() == null) {
                // Display an alert message indicating the user needs to log in
                showAlert("Authentication Required", "Please log in to add items to your cart.");
                return; // Exit the method without adding the item to the cart
            }

            // Retrieve article details
            Article article = articleService.selectWhere(articleId);
            if (article != null) {
                // Create a new panier item
                Panier panierItem = new Panier();
                panierItem.setArticle_id(articleId);
                panierItem.setUser_id(userId);
                panierItem.setPrix_tot(article.getPrix());
                panierItem.setQuantity(1); // Assuming quantity is initially 1

                // Add the panier item to the database
                panierService.add(panierItem);
                System.out.println("Article added to cart successfully!");
            } else {
                System.out.println("Article not found!");
            }
        } catch (SQLException e) {
            System.err.println("Error adding article to cart: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private boolean isValidUserId(int userId) {
        // Assuming you have a UserService class to interact with the user table in the database
        // Check if a user with the given userId exists
        User user = userService.getUserById(userId);
        return user != null; // Return true if user exists, false otherwise
    }

    public List<Article> getPanierItems() throws SQLException {
        int userId = Session.getInstance().getUser().getId(); // Get current user ID
        return panierService.getPanierItems(userId);
    }

    public void removeFromCart(int articleId) throws SQLException {
        int userId = Session.getInstance().getUser().getId(); // Get current user ID
        panierService.removeFromCart(articleId, userId);
    }

    public void updateQuantity(int articleId, int newQuantity) throws SQLException {
        int userId = Session.getInstance().getUser().getId(); // Get current user ID
        panierService.updateQuantity(articleId, userId, newQuantity);
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Other methods for managing the cart can be added here as needed
}
