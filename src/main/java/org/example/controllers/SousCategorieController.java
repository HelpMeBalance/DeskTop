package org.example.controllers;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcons;
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
import org.example.models.SousCategorie;
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

public class SousCategorieController implements Initializable {
    @FXML
    public TableView SubCategories;
    @FXML
    public TableColumn<SousCategorie,String> title;
    @FXML
    public TableColumn <SousCategorie,String>  description;

    @FXML
    public TableColumn <SousCategorie,Void>  actions;
    @FXML
    private Button goback;
    @FXML
    public Label souscategorietitle;

    private CategorieService cS=new CategorieService();
    private SousCategorieService scS=new SousCategorieService();
    ObservableList<SousCategorie> souscategoriesList= FXCollections.observableArrayList();
    private int categorieId;
    private Categorie cat;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    private void initSubCategories() throws SQLException {
        cat=cS.selectWhere(categorieId);
        souscategorietitle.setText(cat.getNom());
        List<SousCategorie> sousCategories=new ArrayList<>(scS.select(cat));
        souscategoriesList.setAll(sousCategories);
        SubCategories.setItems(souscategoriesList);
        title.setCellValueFactory(new PropertyValueFactory<>("nom"));
        description.setCellValueFactory(new PropertyValueFactory<>("description"));
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
                    SousCategorie sousCategorie = getTableView().getItems().get(getIndex());
                    showUpdatesouscategorieDialog(sousCategorie);
                });
                deleteButton.setOnAction(event -> {
                    SousCategorie sousCategorie = getTableView().getItems().get(getIndex());
                    try {
                        showDeleteConfirmationDialog(sousCategorie);
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
    private void showDeleteConfirmationDialog(SousCategorie sousCategorie)  throws SQLException{
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Are you sure you want to delete the Sub-category ?");
        alert.setContentText(" Name : "+sousCategorie.getNom());
        ButtonType confirmButton = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(confirmButton, cancelButton);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == confirmButton) {
            scS.delete(sousCategorie.getId());
            initSubCategories();
        }
    }
    private void showUpdatesouscategorieDialog(SousCategorie sousCategorie)  {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("HelpMeBalance");
        Label titleLabel = new Label("Edit Sub-Category");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-alignment: center;");
        VBox titleBox = new VBox();
        titleBox.getChildren().addAll(titleLabel);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setSpacing(10);
        Label nomLabel = new Label("Title");
        TextField nom = new TextField(sousCategorie.getNom());
        Label contentLabel = new Label("Description");
        TextField content = new TextField(sousCategorie.getDescription());
        VBox contentBox = new VBox();
        contentBox.getChildren().addAll(titleBox,nomLabel,nom,contentLabel,content);
        contentBox.setSpacing(10);
        contentBox.setAlignment(Pos.CENTER_LEFT);
        Button updateButton = new Button("Update Sub-Category");
        HBox buttonBox = new HBox();
        buttonBox.getChildren().add(updateButton);
        buttonBox.setAlignment(Pos.CENTER);
        VBox.setMargin(buttonBox, new Insets(20, 0, 0, 0));
        updateButton.setOnAction(event -> {
            try {
                sousCategorie.setNom(nom.getText());
                sousCategorie.setDescription(content.getText());
                scS.update(sousCategorie);
                souscategoriesList.replaceAll(souscat -> {
                    if ( souscat.getId() == sousCategorie.getId()) {
                        souscat=sousCategorie;
                    }
                    return souscat;
                });
                SubCategories.refresh();
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

    public void addsouscategorie() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("HelpMeBalance");
        Label titleLabel = new Label("Add Sub-Category");
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
        Button updateButton = new Button("Add Sub-Category");
        HBox buttonBox = new HBox();
        buttonBox.getChildren().add(updateButton);
        buttonBox.setAlignment(Pos.CENTER);
        VBox.setMargin(buttonBox, new Insets(20, 0, 0, 0));
        updateButton.setOnAction(event -> {
            try {
                SousCategorie sousCategorie = new SousCategorie(cat,nom.getText(),content.getText());
                scS.add(sousCategorie);
                initSubCategories();
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
    public void setCategorieId(int categorieId) {
       this.categorieId=categorieId;
        try {
            initSubCategories();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void goback() throws IOException {
        Navigation.navigateTo("/fxml/Admin/Categorie.fxml",goback);
    }
}
