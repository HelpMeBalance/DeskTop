package org.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import org.example.models.CategorieProduit;
import org.example.service.CategorieProduitService;

import java.sql.SQLException;

public class CategorieProduitController {
    @FXML
    private TableView<CategorieProduit> categoriesTable;
    @FXML
    private TableColumn<CategorieProduit, String> nomColumn;
    @FXML
    private TableColumn<CategorieProduit, Void> actionsColumn;

    private CategorieProduitService categorieProduitService = new CategorieProduitService();

    @FXML
    public void initialize() {
        setupColumns();
        loadCategories();
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
    }

    private void handleEdit(CategorieProduit categorie) {
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
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    loadCategories();
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
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    loadCategories();
                }
            });
        }
    }

    private void loadCategories() {
        categoriesTable.setItems(categorieProduitService.getAllCategories());
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
                categorieProduitService.add(newCategory);
                loadCategories();
            }
        });
    }
}
