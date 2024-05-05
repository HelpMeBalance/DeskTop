package org.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.example.models.Article;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
public class ArticleDetailsController {
    @FXML
    private Label titleLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Label priceLabel;
    @FXML
    private Label quantityLabel;
    @FXML
    private ImageView articleImageView;

    private Article article;


    public void setArticle(Article article) {
        this.article = article;
        updateArticleDetails();
        loadArticleImage();
    }

    private void updateArticleDetails() {
        titleLabel.setText(article.getNom());
        descriptionLabel.setText(article.getDescription());
        priceLabel.setText(String.format("$%.2f", article.getPrix()));
        quantityLabel.setText(String.valueOf(article.getQuantite()) + " pcs");
    }
    private void loadArticleImage() {
        if (article.getArticlePicture() != null && !article.getArticlePicture().isEmpty()) {
            Image image = new Image("file:" + article.getArticlePicture());
            articleImageView.setImage(image);
        }
}}
