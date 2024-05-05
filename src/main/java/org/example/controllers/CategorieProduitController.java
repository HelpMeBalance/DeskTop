package org.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import org.example.models.CategorieProduit;
import org.example.service.CategorieProduitService;
import javafx.collections.FXCollections;
import java.sql.SQLException;
import java.util.List;


public class CategorieProduitController {
    @FXML
    private TableView<CategorieProduit> categoriesTable;
    @FXML
    private TableColumn<CategorieProduit, String> nomColumn;
    @FXML
    private TableColumn<CategorieProduit, Void> actionsColumn;
    @FXML
    private TextField searchField;
    @FXML
    private Pagination pagination;
    private int currentPageIndex = 0;
    private final int PAGE_SIZE = 3;
    private String searchText = "";

    private CategorieProduitService categorieProduitService = new CategorieProduitService();

    @FXML
    public void initialize() throws SQLException {
        setupColumns();
        loadCategories();
        pagination.currentPageIndexProperty().addListener((obs, oldPageIndex, newPageIndex) -> {
            currentPageIndex = newPageIndex.intValue();
            try {
                loadCategories();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

    }

    private void setupColumns() {
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final HBox container = new HBox(5, editButton, deleteButton);

            {
                editButton.setOnAction(e -> handleEdit(getTableRow().getItem()));
                deleteButton.setOnAction(e -> handleDelete(getTableRow().getItem()));
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
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterCategories(newValue);
        });
    }

    private void handleEdit(CategorieProduit categorie)  {
        if (categorie != null) {
            // Display dialog to edit and then refresh list
            TextInputDialog dialog = new TextInputDialog(categorie.getNom());
            dialog.setTitle("Edit Category");
            dialog.setHeaderText("Edit the category name:");
            dialog.setContentText("Category name:");
            dialog.showAndWait().ifPresent(name -> {
                if (!name.trim().isEmpty()) {
                    categorie.setNom(name);
                    try {
                        categorieProduitService.update(categorie);
                        loadCategories();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }

                }
            });
        }
    }

    private void handleDelete(CategorieProduit categorie) {
        if (categorie != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this category?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    try {
                        categorieProduitService.delete(categorie.getId());
                        loadCategories();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }

                }
            });
        }
    }

    private void loadCategories() throws SQLException  {
        categoriesTable.setItems(categorieProduitService.getAllCategories());

        int totalCategories;
        List<CategorieProduit> categories;
        if (currentPageIndex < 0) {
            // Gérer le cas où currentPageIndex n'est pas correctement initialisé
            System.err.println("currentPageIndex is not properly initialized");
            return;
        }
        try {
            categories = getPageCategories(currentPageIndex);
        } catch (SQLException e) {
            // Gérer l'exception, par exemple afficher un message d'erreur
            e.printStackTrace();
            return; // Sortir de la méthode pour éviter d'utiliser une liste potentiellement vide
        }
        categoriesTable.setItems(FXCollections.observableArrayList(categories));


        if (!searchText.isEmpty()) {
            categories = categorieProduitService.searchCategories(searchText);
            totalCategories = categories.size();
            pagination.setPageCount((totalCategories + PAGE_SIZE - 1) / PAGE_SIZE);
            categories = getPageCategories(currentPageIndex);
        } else {
            totalCategories = categorieProduitService.countCategories();
            pagination.setPageCount((totalCategories + PAGE_SIZE - 1) / PAGE_SIZE);
            categories = categorieProduitService.selectAdmin(currentPageIndex, PAGE_SIZE);
        }

        categoriesTable.setItems(FXCollections.observableArrayList(categories));

    }

    private List<CategorieProduit> getPageCategories(int pageIndex) throws SQLException {
        return categorieProduitService.selectAdmin(pageIndex, PAGE_SIZE);
    }

    private void filterCategories(String searchTerm) {
        categoriesTable.setItems(categorieProduitService.searchCategories(searchTerm));
    }
    @FXML
    private void handleAddCategory() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Category");
        dialog.setHeaderText(null);
        dialog.setContentText("Please enter the name of the new category:");

        dialog.showAndWait().ifPresent(name -> {
            if (!name.trim().isEmpty()) {
                CategorieProduit newCategory = new CategorieProduit();
                newCategory.setNom(name);
                try {
                    categorieProduitService.add(newCategory);
                    loadCategories();
                } catch (SQLException e) {
                    // Gérer l'exception SQLException ici
                    e.printStackTrace(); // Afficher la trace de la pile pour déboguer
                    // Afficher un message d'erreur à l'utilisateur si nécessaire
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Failed to add the new category. Please try again later.", ButtonType.OK);
                    errorAlert.showAndWait();
                }
            }
        });
    }
}
