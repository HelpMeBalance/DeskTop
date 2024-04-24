package org.example.controllers;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcons;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.models.Categorie;
import org.example.service.CategorieService;
import org.example.service.SousCategorieService;
import org.example.utils.Navigation;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class CategorieController implements Initializable {
    @FXML
    public TableView Categories;
    @FXML
    public TableColumn <Categorie,String> title;
    @FXML
    public TableColumn <Categorie,String>  description;
    @FXML
    public TableColumn <Categorie,Integer> subcategories;
    @FXML
    public TableColumn <Categorie,Void> viewcategories;
    @FXML
    public TableColumn <Categorie,Void>  actions;
    private CategorieService cS=new CategorieService();
    private SousCategorieService scS=new SousCategorieService();
    ObservableList<Categorie> categoriesList= FXCollections.observableArrayList();
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            initCategories();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void initCategories() throws SQLException {
        List<Categorie> categories=new ArrayList<>(cS.select());
        categoriesList.setAll(categories);
        Categories.setItems(categoriesList);
        title.setCellValueFactory(new PropertyValueFactory<>("nom"));
        description.setCellValueFactory(new PropertyValueFactory<>("description"));
        subcategories.setCellValueFactory(cellData -> {
            try {
                int count = scS.countSubcategories(cellData.getValue());
                return new SimpleIntegerProperty(count).asObject();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        viewcategories.setCellFactory(param -> new TableCell<>(){
            private final Button viewButton = new Button();
            {
                FontAwesomeIcon viewIcon = new FontAwesomeIcon();
                viewIcon.setIcon(FontAwesomeIcons.LIST);
                viewButton.setGraphic(viewIcon);
                viewButton.setStyle("-fx-background-color: transparent;");
                viewButton.setOnAction(event -> {
                    Categorie categorie = getTableView().getItems().get(getIndex());
                    try {
                        Navigation.navigateTo("/fxml/Admin/SousCategorie.fxml", viewButton, categorie.getId());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttonsBox = new HBox(viewButton);
                    buttonsBox.setSpacing(5);
                    setGraphic(buttonsBox);
                }
            }
        });
        actions.setCellFactory(param -> new TableCell<>() {
            private final Button updateButton = new Button();
            private final Button deleteButton = new Button();
            {
                FontAwesomeIcon updateIcon = new FontAwesomeIcon();
                updateIcon.setIcon(FontAwesomeIcons.PENCIL);
                updateButton.setGraphic(updateIcon);
                FontAwesomeIcon deleteIcon = new FontAwesomeIcon();
                deleteIcon.setIcon(FontAwesomeIcons.TRASH);
                deleteButton.setGraphic(deleteIcon);
                updateButton.setStyle("-fx-background-color: transparent;");
                deleteButton.setStyle("-fx-background-color: transparent;");
                updateButton.setOnAction(event -> {
                    Categorie categorie = getTableView().getItems().get(getIndex());
                    showUpdatecategorieDialog(categorie);
                });
                deleteButton.setOnAction(event -> {
                    Categorie categorie = getTableView().getItems().get(getIndex());
                    try {
                        showDeleteConfirmationDialog(categorie);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttonsBox = new HBox(updateButton, deleteButton);
                    buttonsBox.setSpacing(5);
                    setGraphic(buttonsBox);
                }
            }
        });

    }

    private void showDeleteConfirmationDialog(Categorie categorie) throws SQLException {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Are you sure you want to delete the category ?");
        alert.setContentText(" Title : "+categorie.getNom());
        ButtonType confirmButton = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(confirmButton, cancelButton);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == confirmButton) {
            cS.delete(categorie.getId());
            initCategories();
        }
    }

    private void showUpdatecategorieDialog(Categorie categorie)  {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("HelpMeBalance");
        Label titleLabel = new Label("Edit Category");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-alignment: center;");
        VBox titleBox = new VBox();
        titleBox.getChildren().addAll(titleLabel);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setSpacing(10);
        Label nomLabel = new Label("Title");
        TextField nom = new TextField(categorie.getNom());
        Label contentLabel = new Label("Description");
        TextField content = new TextField(categorie.getDescription());
        VBox contentBox = new VBox();
        contentBox.getChildren().addAll(titleBox,nomLabel,nom,contentLabel,content);
        contentBox.setSpacing(10);
        contentBox.setAlignment(Pos.CENTER_LEFT);
        Button updateButton = new Button("Update Category");
        HBox buttonBox = new HBox();
        buttonBox.getChildren().add(updateButton);
        buttonBox.setAlignment(Pos.CENTER);
        VBox.setMargin(buttonBox, new Insets(20, 0, 0, 0));
        updateButton.setOnAction(event -> {
            try {
                categorie.setNom(nom.getText());
                categorie.setDescription(content.getText());
                cS.update(categorie);
                categoriesList.replaceAll(cat -> {
                    if ( cat.getId() == categorie.getId()) {
                        cat=categorie;
                    }
                    return cat;
                });
                Categories.refresh();
                dialog.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        dialog.getDialogPane().setContent(new VBox(contentBox, buttonBox));
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);
        dialog.getDialogPane().lookupButton(ButtonType.CLOSE).setVisible(false);
        dialog.showAndWait();
    }
    public void addcategorie() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("HelpMeBalance");
        Label titleLabel = new Label("Add Category");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-alignment: center;");
        VBox titleBox = new VBox();
        titleBox.getChildren().addAll(titleLabel);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setSpacing(10);
        Label nomLabel = new Label("Title");
        TextField nom = new TextField();
        Label contentLabel = new Label("Description");
        TextField content = new TextField();
        VBox contentBox = new VBox();
        contentBox.getChildren().addAll(titleBox,nomLabel,nom,contentLabel,content);
        contentBox.setSpacing(10);
        contentBox.setAlignment(Pos.CENTER_LEFT);
        Button updateButton = new Button("Add Category");
        HBox buttonBox = new HBox();
        buttonBox.getChildren().add(updateButton);
        buttonBox.setAlignment(Pos.CENTER);
        VBox.setMargin(buttonBox, new Insets(20, 0, 0, 0));
        updateButton.setOnAction(event -> {
            try {
                Categorie categorie = new Categorie(nom.getText(),content.getText());
                cS.add(categorie);
                initCategories();
                dialog.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        dialog.getDialogPane().setContent(new VBox(contentBox, buttonBox));
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);
        dialog.getDialogPane().lookupButton(ButtonType.CLOSE).setVisible(false);
        dialog.showAndWait();
    }
}
