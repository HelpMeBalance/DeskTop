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
import org.example.models.Article;
import org.example.models.CategorieProduit;
import org.example.service.ArticleService;
import org.example.service.CategorieProduitService;

import java.io.IOException;
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

    private ArticleService articleService = new ArticleService();
    private CategorieProduitService categorieProduitService = new CategorieProduitService();
    @FXML
    private StackPane contentArea; // This pane will be used to load content dynamically

    @FXML
    public void initialize() {
        loadArticles();
        loadCategories();
    }

    private void loadArticles() {
        try {
            List<Article> articles = articleService.selectAll();
            articles.forEach(article -> {
                VBox articleBox = new VBox(5);  // Vertical spacing between elements
                Label titleLabel = new Label(article.getNom());
                titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

                Label priceLabel = new Label("Price: $" + article.getPrix());
                Label descriptionLabel = new Label(article.getDescription());
                descriptionLabel.setWrapText(true);

                ImageView imageView = new ImageView();
                if (article.getArticlePicture() != null && !article.getArticlePicture().isEmpty()) {
                    imageView.setImage(new Image("file:" + article.getArticlePicture()));
                    imageView.setFitHeight(100);  // Set the image height
                    imageView.setFitWidth(100);   // Set the image width
                }

                Button viewMoreButton = new Button("View More");
                viewMoreButton.setOnAction(e -> displayArticleDetails(article));

                articleBox.getChildren().addAll(imageView, titleLabel, priceLabel, descriptionLabel, viewMoreButton);
                articlesContainer.getChildren().add(articleBox);  // Add each article in a VBox
            });
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exceptions, perhaps show an alert dialog
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Store/ArticleDetails.fxml"));  // Adjust path as necessary
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

}
