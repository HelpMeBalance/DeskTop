package org.example.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.controllers.ArticleDetailsController;
import org.example.models.Article;
import org.example.models.CategorieProduit;
import org.example.models.User;
import org.example.service.ArticleService;
import org.example.service.CategorieProduitService;
import org.example.controllers.PanierController; // Import PanierController or PanierService
import org.example.utils.Navigation;
import org.example.utils.Session;


import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class StoreController {
    @FXML
    private VBox articlesContainer;
    @FXML
    private ListView<String> categoryList;
    @FXML
    private ListView<String> recommendedArticlesList;
    @FXML
    private ListView<String> tagList;
    @FXML
    private StackPane contentArea;

    private ArticleService articleService = new ArticleService();
    private CategorieProduitService categorieProduitService = new CategorieProduitService();
    @FXML
    private PanierController panierController;
    @FXML
    private VBox panierItemsContainer;
    @FXML
    private Label totalPriceLabel;

    public StoreController() {
        // Default constructor
    }
    public StoreController(PanierController panierController) {
        this.panierController = panierController;
    }


    @FXML
    public void initialize() {
        // Initialize panierController
        this.panierController = new PanierController();
        Session.getInstance().setAttribute("storeController", this);

        // Load articles and categories
        loadArticles();
        loadCategories();
      //  loadPanierItems(); // Load cart items

    }

    private void loadArticles() {
        try {
            List<Article> articles = articleService.selectAll();
            articles.forEach(article -> {
                VBox articleBox = new VBox(5);
                Label titleLabel = new Label(article.getNom());
                titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

                Label priceLabel = new Label("Price: $" + article.getPrix());
                Label descriptionLabel = new Label(article.getDescription());
                descriptionLabel.setWrapText(true);

                ImageView imageView = new ImageView();
                if (article.getArticlePicture() != null && !article.getArticlePicture().isEmpty()) {
                    imageView.setImage(new Image("file:" + article.getArticlePicture()));
                    imageView.setFitHeight(100);
                    imageView.setFitWidth(100);
                }

                Button addToCartButton = new Button("Add to Cart");
                addToCartButton.setOnAction(e -> addToCart(article));

                Button viewMoreButton = new Button("View More");
                viewMoreButton.setOnAction(e -> displayArticleDetails(article));

                articleBox.getChildren().addAll(imageView, titleLabel, priceLabel, descriptionLabel, addToCartButton, viewMoreButton);
                articlesContainer.getChildren().add(articleBox);
            });
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exceptions, perhaps show an alert dialog
        }
    }

    private void addToCart(Article article) {
        try {
            int userId = getCurrentUserId();
            int articleId = article.getId();

            panierController.addToCart(articleId, userId);
            loadPanierItems(); // Refresh cart display after adding the article
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exceptions, perhaps show an alert dialog
        }
    }

    private int getCurrentUserId() {
        User currentUser = Session.getInstance().getCurrentUser();
        if (currentUser != null) {
            return currentUser.getId();
        } else {
            return -1; // Or any other appropriate value
        }
    }

    private void loadCategories() {
        try {
            List<CategorieProduit> categories = categorieProduitService.selectAllCategories();
            categories.forEach(categorie -> categoryList.getItems().add(categorie.getNom()));
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exceptions, perhaps show an alert dialog
        }
    }

    private void openArticleDetails(Article article) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Store/ArticleDetails.fxml"));
            BorderPane root = loader.load();
            ArticleDetailsController controller = loader.getController();
            controller.setArticle(article);

            Stage stage = new Stage();
            stage.setTitle(article.getNom());
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading ArticleDetails.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void displayArticleDetails(Article article) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Store/ArticleDetails.fxml"));
            Node articleDetails = loader.load();
            ArticleDetailsController controller = loader.getController();
            controller.setArticle(article);

            contentArea.getChildren().setAll(articleDetails); // Replace current content
        } catch (Exception e) {
            e.printStackTrace();
            // Handle errors possibly with an alert or log
        }
    }
    private void loadPanierItems() {
        panierItemsContainer.getChildren().clear(); // Clear previous items
        double totalPrice = 0.0; // Initialize total price
        try {
            List<Article> panierItems = panierController.getPanierItems();
            for (Article article : panierItems) {
                VBox itemBox = new VBox(5);
                Label titleLabel = new Label(article.getNom());
                titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

                Label priceLabel = new Label("Price: $" + article.getPrix());
                Label quantityLabel = new Label("Quantity: " + article.getQuantite());
                Label totalLabel = new Label("Total: $" + (article.getPrix() * article.getQuantite()));

                Button removeFromCartButton = new Button("Remove from Cart");
                removeFromCartButton.setOnAction(e -> removeFromCart(article.getId()));

                itemBox.getChildren().addAll(titleLabel, priceLabel, quantityLabel, totalLabel, removeFromCartButton);
                panierItemsContainer.getChildren().add(itemBox);
                totalPrice += article.getPrix() * article.getQuantite();
            }
            totalPriceLabel.setText("Total Price: $" + totalPrice);

            // Store the total price in the session
            Session.getInstance().setAttribute("totalPrice", totalPrice);

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions, perhaps show an alert dialog
        }
    }

    private void removeFromCart(int articleId) {
        try {
            panierController.removeFromCart(articleId);
            loadPanierItems(); // Reload panier items after removal
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions, perhaps show an alert dialog
        }
    }

    @FXML
    private void handleCommanderButtonClick() {
        Navigation.navigateTo("/fxml/Commande/CommandeForm.fxml");
    }
}

