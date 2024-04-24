package org.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.example.models.Article;

public class ArticleDetailsController {
    @FXML
    private Label titleLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Label priceLabel;
    @FXML
    private Label quantityLabel;

    private Article article;

    public void setArticle(Article article) {
        this.article = article;
        updateArticleDetails();
    }

    private void updateArticleDetails() {
        titleLabel.setText(article.getNom());
        descriptionLabel.setText(article.getDescription());
        priceLabel.setText(String.format("$%.2f", article.getPrix()));
        quantityLabel.setText(String.valueOf(article.getQuantite()) + " pcs");
    }
}
