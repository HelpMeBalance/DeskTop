package org.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.example.models.Publication;
import org.example.models.Categorie;
import org.example.service.PublicationService;
import org.example.service.CategorieService;

import java.util.List;

public class BlogController {
    @FXML
    private VBox publicationContainer;
    @FXML
    private VBox categoriesContainer;

    private PublicationService publicationService = new PublicationService();
    private CategorieService categorieService = new CategorieService();

    @FXML
    public void initialize() {
        loadPublications();
        loadCategories();
    }

    private void loadPublications() {
        try {
            List<Publication> publications = publicationService.select(1, 2); // Get top two publications
            for (Publication pub : publications) {
                Label nameLabel = new Label(pub.getTitre());
                ImageView imageView = new ImageView(new Image("file:" + pub.getImage()));
                imageView.setFitHeight(100);
                imageView.setFitWidth(100);
                publicationContainer.getChildren().addAll(imageView, nameLabel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadCategories() {
        try {
            List<Categorie> categories = categorieService.select(); // Fetch all categories
            for (Categorie cat : categories) {
                Label categoryLabel = new Label(cat.getNom());
                categoriesContainer.getChildren().add(categoryLabel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
