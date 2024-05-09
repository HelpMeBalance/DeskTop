package org.example.controllers;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcons;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.models.Article;
import org.example.models.CategorieProduit;
import org.example.service.ArticleService;
import org.example.service.CategorieProduitService;
import java.util.HashSet;
import java.util.Set;
import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import javafx.util.Callback;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;




public class ArticleController {
    @FXML
    private TableView<Article> articlesTable;
    @FXML
    private TableColumn<Article, String> nomColumn;
    @FXML
    private TableColumn<Article, String> descriptionColumn;
    @FXML
    private TableColumn<Article, Double> prixColumn;
    @FXML
    private TableColumn<Article, Integer> quantiteColumn;
    @FXML
    private TableColumn<Article, String> categorieColumn;  // Updated type
    @FXML
    private TableColumn<Article, Void> actionsColumn;
    @FXML
    private TextField searchField;


    private static final int ITEMS_PER_PAGE = 4;
    private List<Article> allArticles;

    private final ArticleService articleService = new ArticleService();
    private final CategorieProduitService categorieProduitService = new CategorieProduitService();

    @FXML
    public void initialize() {

        initializeTableColumns();
        loadArticles();
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterArticles(newValue);});
        try {
            allArticles = articleService.selectAll();
        } catch (SQLException e) {
            showAlert("Error Loading Articles", "Could not load articles from the database: " + e.getMessage(), Alert.AlertType.ERROR);
            return;
        }


    }


    private void filterArticles(String searchTerm) {
        try {
            // Recherche par nom
            List<Article> articlesByNom = articleService.searchArticles(searchTerm, "nom");
            // Recherche par description
            List<Article> articlesByDescription = articleService.searchArticles(searchTerm, "description");

            // Fusionner les deux listes sans duplications
            Set<Article> mergedArticles = new HashSet<>(articlesByNom);
            mergedArticles.addAll(articlesByDescription);

            // Mettre à jour la TableView avec les articles filtrés
            articlesTable.setItems(FXCollections.observableArrayList(new ArrayList<>(mergedArticles)));
        } catch (SQLException e) {
            showAlert("Error Loading Articles", "Could not load articles from the database: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void initializeTableColumns() {
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        prixColumn.setCellValueFactory(new PropertyValueFactory<>("prix"));
        quantiteColumn.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        categorieColumn.setCellValueFactory(new PropertyValueFactory<>("categorieId"));  // Assume there's a getCategorieNom() in Article that returns the name of the category

        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button();
            private final Button deleteButton = new Button();
            private final HBox container = new HBox(5, editButton, deleteButton);

            {
                FontAwesomeIcon editIcon = new FontAwesomeIcon();
                editIcon.setIcon(FontAwesomeIcons.PENCIL);
                editButton.setGraphic(editIcon);

                FontAwesomeIcon deleteIcon = new FontAwesomeIcon();
                deleteIcon.setIcon(FontAwesomeIcons.TRASH);
                deleteButton.setGraphic(deleteIcon);

                editButton.setStyle("-fx-background-color: transparent;");
                deleteButton.setStyle("-fx-background-color: transparent;");

                editButton.setOnAction(e -> handleEditArticle(getTableRow().getItem()));
                deleteButton.setOnAction(e -> handleDeleteArticle(getTableRow().getItem()));

            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    setGraphic(container);
                }
            }
        });
    }

    private void handleEditArticle(Article article) {
        if (article != null) {
            TextInputDialog dialog = new TextInputDialog(article.getNom());
            dialog.setTitle("Edit Article");
            dialog.setHeaderText("Edit the article details:");
            dialog.setContentText("Name:");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(name -> {
                if (!name.trim().isEmpty()) {
                    article.setNom(name);
                    try {
                        articleService.update(article);
                        loadArticles();  // Refresh list after update
                    } catch (SQLException e) {
                        showAlert("Error", "Failed to update the article: " + e.getMessage(), Alert.AlertType.ERROR);
                    }
                }
            });
        }
    }

    private void handleDeleteArticle(Article article) {
        if (article != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this article?", ButtonType.YES, ButtonType.NO);
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.YES) {
                try {
                    articleService.delete(article);
                    articlesTable.getItems().remove(article);  // Remove from table after deletion
                } catch (SQLException e) {
                    showAlert("Error", "Failed to delete the article: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        }
    }

    private void loadArticles() {
        try {
            List<Article> articles = articleService.selectAll();
            articlesTable.setItems(FXCollections.observableArrayList(articles));
        } catch (SQLException e) {
            showAlert("Error Loading Articles", "Could not load articles from the database: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleAddArticle() throws SQLException {
        Dialog<Article> dialog = new Dialog<>();
        dialog.setTitle("Add New Article");

        dialog.setHeaderText("Enter the details of the new article.");

        Button addImageButton = new Button("Add Image");
        TextField imagePathField = new TextField();
        imagePathField.setEditable(false);
        addImageButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Image File");
            File selectedFile = fileChooser.showOpenDialog(dialog.getOwner());
            if (selectedFile != null) {
                imagePathField.setText(selectedFile.getAbsolutePath());
            }
        });
        // Set the button types.
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        // Ajoutez les autres champs du formulaire à grid
        grid.add(new Label("Image:"), 0, 5);
        grid.add(imagePathField, 1, 5);
        grid.add(addImageButton, 2, 5);


        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nom = new TextField();
        nom.setPromptText("Name");
        TextField description = new TextField();
        description.setPromptText("Description");
        TextField prix = new TextField();
        prix.setPromptText("Price");
        TextField quantite = new TextField();
        quantite.setPromptText("Quantity");
        ComboBox<CategorieProduit> categorie = new ComboBox<>();
        categorie.setItems(FXCollections.observableArrayList(categorieProduitService.selectAllCategories())); // Load categories
        categorie.setPromptText("Select Category");

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nom, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(description, 1, 1);
        grid.add(new Label("Price:"), 0, 2);
        grid.add(prix, 1, 2);
        grid.add(new Label("Quantity:"), 0, 3);
        grid.add(quantite, 1, 3);
        grid.add(new Label("Category:"), 0, 4);
        grid.add(categorie, 1, 4);

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(nom::requestFocus);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                if (!nom.getText().isEmpty() && !description.getText().isEmpty() && !prix.getText().isEmpty() && !quantite.getText().isEmpty() && categorie.getValue() != null) {
                    try {
                        Article newArticle = new Article();
                        newArticle.setNom(nom.getText());
                        newArticle.setDescription(description.getText());
                        newArticle.setPrix(Double.parseDouble(prix.getText()));
                        newArticle.setQuantite(Integer.parseInt(quantite.getText()));
                        newArticle.setCategorie(categorie.getValue().getId());
                        return newArticle;
                    } catch (NumberFormatException e) {
                        showAlert("Input Error", "Please enter valid numbers for price and quantity.", Alert.AlertType.ERROR);
                    }
                } else {
                    showAlert("Input Error", "Please complete all fields.", Alert.AlertType.ERROR);
                }
            }
            return null;
        });

        Optional<Article> result = dialog.showAndWait();
        result.ifPresent(article -> {
            String imagePath = imagePathField.getText();
            if (!imagePath.isEmpty()) {
                article.setArticlePicture(imagePath);
            }
            try {
                articleService.add(article);
                loadArticles();
            } catch (SQLException e) {
                showAlert("Database Error", "Failed to add a new article to the database: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }


    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleViewStatistics() {
        try {
            List<CategorieProduit> categories = categorieProduitService.selectAllCategories();

            CategoryAxis xAxis = new CategoryAxis();
            NumberAxis yAxis = new NumberAxis();
            BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
            barChart.setTitle("Product Statistics by Category");
            xAxis.setLabel("Category");
            yAxis.setLabel("Number of Products");

            XYChart.Series<String, Number> series = new XYChart.Series<>();

            for (CategorieProduit category : categories) {
                int count = articleService.selectByCategory(category).size();
                series.getData().add(new XYChart.Data<>(category.toString(), count));
            }

            barChart.getData().add(series);

            // Create a new stage to display the chart
            Stage stage = new Stage();
            stage.setTitle("Product Statistics");
            Scene scene = new Scene(barChart, 800, 600);
            stage.setScene(scene);
            stage.show();

        } catch (SQLException e) {
            showAlert("Error", "Failed to load statistics: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

}