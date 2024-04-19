package org.example.controllers;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcons;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.models.Categorie;
import org.example.models.Publication;
import org.example.models.SousCategorie;
import org.example.service.CategorieService;
import org.example.service.CommentaireService;
import org.example.service.PublicationService;
import org.example.service.SousCategorieService;
import org.example.utils.Navigation;
import org.example.utils.Session;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class PublicationController implements Initializable {
    private PublicationService pS=new PublicationService();
    private CommentaireService cS=new CommentaireService();
    private CategorieService catS=new CategorieService();
    private SousCategorieService souscatS=new SousCategorieService();
    @FXML
    private TableView Publications;
    @FXML
    private TableColumn<Publication, String> title;

    @FXML
    private TableColumn<Publication, String> content;

    @FXML
    private TableColumn<Publication, String> userName;

    @FXML
    private TableColumn<Publication, String> state;

    @FXML
    private TableColumn<Publication, Integer> views;

    @FXML
    private TableColumn<Publication, String> comments;

    @FXML
    private TableColumn<Publication, Void> viewcomments;

    @FXML
    private TableColumn<Publication, String> postedAt;

    @FXML
    private TableColumn<Publication, String> updatedAt;
    @FXML
    private TableColumn<Publication, Void> actions;
    ObservableList<Publication> publicationsList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initPublications();
    }
    public void initPublications() {
        // Start test

        try
        {
            publicationsList = FXCollections.observableArrayList(pS.select());
            Publications.setItems(publicationsList);
            title.setCellValueFactory(new PropertyValueFactory<>("titre"));
            content.setCellValueFactory(new PropertyValueFactory<>("contenu"));
            userName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUser().getLastname()+" "+cellData.getValue().getUser().getFirstname()));
            state.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getValide() ? "Approved" : "Under Review"));
            views.setCellValueFactory(new PropertyValueFactory<>("vues"));

            comments.setCellValueFactory(cellData -> {
                try {
                    //Navigation.navigateTo("/fxml/Admin/commentaire.fxml");
                    int count = cS.countComments(cellData.getValue());
                    String res = null;
                    if(cellData.getValue().getCom_ouvert())
                    {
                        if(count>0)res="\uD83D\uDCAC " +count; 
                        else res = "\uD83D\uDEAB comments";
                    }
                    else
                    {
                        if(count>0)res="\uD83D\uDD12 " +count;
                        else res = "\uD83D\uDD12 comments";
                    }
                    //🔒
                    return new SimpleStringProperty(res);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            viewcomments.setCellFactory(param -> new TableCell<>(){
                private final Button viewButton = new Button();
                {
                    FontAwesomeIcon viewIcon = new FontAwesomeIcon();
                    viewIcon.setIcon(FontAwesomeIcons.ELLIPSIS_H);
                    viewButton.setGraphic(viewIcon);
                    viewButton.setStyle("-fx-background-color: transparent;");
                    viewButton.setOnAction(event -> {
                        Publication publication = getTableView().getItems().get(getIndex());
                        // Handle view action here using publication.getId() or other relevant data
                        try {
                            if(cS.countComments(publication)==0 && !publication.getCom_ouvert())
                                showemptycommentsDialog();
                            else Navigation.navigateTo("/fxml/Admin/Commentaire.fxml", viewButton, publication.getId());
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
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
            postedAt.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDate_c().format(DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm:ss"))));
            updatedAt.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDate_m().format(DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm:ss"))));
            actions.setCellFactory(param -> new TableCell<>() {
                private final Button viewButton = new Button();
                private final Button updateButton = new Button();
                private final Button deleteButton = new Button();

                {
                    FontAwesomeIcon viewIcon = new FontAwesomeIcon();
                    viewIcon.setIcon(FontAwesomeIcons.EYE);
                    viewButton.setGraphic(viewIcon);

                    FontAwesomeIcon updateIcon = new FontAwesomeIcon();
                    updateIcon.setIcon(FontAwesomeIcons.PENCIL);
                    updateButton.setGraphic(updateIcon);

                    FontAwesomeIcon deleteIcon = new FontAwesomeIcon();
                    deleteIcon.setIcon(FontAwesomeIcons.TRASH);
                    deleteButton.setGraphic(deleteIcon);
                    viewButton.setStyle("-fx-background-color: transparent;");
                    updateButton.setStyle("-fx-background-color: transparent;");
                    deleteButton.setStyle("-fx-background-color: transparent;");

                    viewButton.setOnAction(event -> {
                        Publication publication = getTableView().getItems().get(getIndex());
                        // Handle view action here using publication.getId() or other relevant data
                        showPublicationDetails(publication);
                    });

                    updateButton.setOnAction(event -> {
                        Publication publication = getTableView().getItems().get(getIndex());
                        // Handle view action here using publication.getId() or other relevant data
                        showUpdatePublicationDialog(publication);
                    });

                    deleteButton.setOnAction(event -> {
                        Publication publication = getTableView().getItems().get(getIndex());
                        try {
                            showDeleteConfirmationDialog(publication);
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
                        HBox buttonsBox = new HBox(viewButton, updateButton, deleteButton);
                        buttonsBox.setSpacing(5);
                        setGraphic(buttonsBox);
                    }
                }
            });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private void showDeleteConfirmationDialog(Publication publication) throws SQLException {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Are you sure you want to delete the publication ?");
        alert.setContentText("Publication Title: " + publication.getTitre() + "\n Posted By : "+publication.getUser().getLastname()+" "+publication.getUser().getFirstname());

        ButtonType confirmButton = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(confirmButton, cancelButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == confirmButton) {
            pS.delete(publication.getId());
            publicationsList.remove(publication);
            Publications.refresh();
        }
    }
    private void showemptycommentsDialog() throws SQLException {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("No Comments Found");
        alert.setHeaderText(" The comments are empty and locked ");
        Optional<ButtonType> result = alert.showAndWait();
    }
    private void showPublicationDetails(Publication publication) {
        // Create a new Dialog
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("HelpMeBalance");

        // Create a label for the title
        Label titleLabel = new Label("Publication Details");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-alignment: center;");

        ImageView imageView = new ImageView();
        try {
            String imageFile = "file:///C:/Users/HP/Desktop/PiDev HelpMeBalance/Web_2/public/uploads/pub_pictures/" + publication.getImage();
            Image image = new Image(imageFile);
            imageView.setImage(image);
            imageView.setFitWidth(200);
            imageView.setFitHeight(200);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Set the content text with publication details
        Label contentLabel = new Label(
                "Categorie : "+publication.getCategorie().getNom() + "\n" +
                        "Sub-Categorie : "+publication.getSous_categorie().getNom() + "\n" +
                        "Title: " + publication.getTitre() + "\n" +
                        "Content: \n" + publication.getContenu() + "\n"
        );

        // Create an HBox to hold the title and image, and center them
        VBox titleImageBox = new VBox();
        titleImageBox.getChildren().addAll(titleLabel, imageView);
        titleImageBox.setAlignment(Pos.CENTER);
        titleImageBox.setSpacing(10);

        // Create a VBox to hold the content
        VBox contentBox = new VBox();
        contentBox.getChildren().addAll(titleImageBox, contentLabel);
        contentBox.setSpacing(10); // Adjust spacing as needed
        contentBox.setAlignment(Pos.CENTER_LEFT);
        String val=(publication.getValide())?"Invalidate":"Validate";
        // Create a button for validating the publication
                Button validateButton = new Button(val);
                HBox buttonBox = new HBox();
                buttonBox.getChildren().add(validateButton);
                buttonBox.setAlignment(Pos.CENTER);
        // Create an action for the validate button
                validateButton.setOnAction(event -> {
                    try {
                        pS.validate(publication.getId()); // Call the validate function
                        publicationsList.replaceAll(pub -> {
                            if (pub.getId() == publication.getId()) {
                                // Update the publication that was validated
                                pub.setValide(!pub.getValide()); // Assuming there's a property for validation status
                            }
                            return pub;
                        });
                        Publications.refresh();
                        dialog.close(); // Close the dialog after validation
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }

                });
        // Set the content of the dialog
        dialog.getDialogPane().setContent(new VBox(contentBox, buttonBox));
        // Set the button as the action for the dialog
               dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);
        dialog.getDialogPane().lookupButton(ButtonType.CLOSE).setVisible(false);
        // Show the dialog
        dialog.showAndWait();
    }

    private void showUpdatePublicationDialog(Publication publication) {
        // Create a new Dialog
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("HelpMeBalance");

        // Create a label for the title
        Label titleLabel = new Label("Edit Publication");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-alignment: center;");

        ImageView imageView = new ImageView();
        try {
            String imageFile = "file:///C:/Users/HP/Desktop/PiDev HelpMeBalance/Web_2/public/uploads/pub_pictures/" + publication.getImage();
            Image image = new Image(imageFile);
            imageView.setImage(image);
            imageView.setFitWidth(200);
            imageView.setFitHeight(200);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Button UpdatePictureButton = new Button("Update Picture");
        Label titlepublicationLabel = new Label("Title");
        Label contentLabel = new Label("Publication");
        TextField title = new TextField(publication.getTitre());
        TextArea content = new TextArea(publication.getContenu());
        content.setWrapText(true);
        CheckBox allowcoms = new CheckBox("Allow Comments");
        allowcoms.setSelected(publication.getCom_ouvert());
        CheckBox anonyme = new CheckBox("Post as Anonyme");
        anonyme.setSelected(publication.getAnonyme());
        anonyme.setDisable(true);
        // Create an HBox to hold the title and image, and center them
        VBox titleImageBox = new VBox();
        titleImageBox.getChildren().addAll(titleLabel, imageView);
        titleImageBox.setAlignment(Pos.CENTER);
        titleImageBox.setSpacing(10);

        // Create a VBox to hold the content
        VBox contentBox = new VBox();
        contentBox.getChildren().addAll(titleImageBox,UpdatePictureButton,titlepublicationLabel,title,contentLabel,content,allowcoms,anonyme);
        contentBox.setSpacing(10); // Adjust spacing as needed
        contentBox.setAlignment(Pos.CENTER_LEFT);
        Button updateButton = new Button("Update Publication");
        HBox buttonBox = new HBox();
        buttonBox.getChildren().add(updateButton);
        buttonBox.setAlignment(Pos.CENTER);
        // Create an action for the validate button
        updateButton.setOnAction(event -> {
            try {
                publication.setTitre(title.getText());
                publication.setContenu(content.getText());
                publication.setCom_ouvert(allowcoms.isSelected());
                pS.update(publication);
                publicationsList.replaceAll(pub -> {
                    if (pub.getId() == publication.getId()) {
                        pub=publication;
                    }
                    return pub;
                });
                Publications.refresh();
                dialog.close(); // Close the dialog after validation
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        });
        // Set the content of the dialog
        dialog.getDialogPane().setContent(new VBox(contentBox, buttonBox));
        // Set the button as the action for the dialog
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);
        dialog.getDialogPane().lookupButton(ButtonType.CLOSE).setVisible(false);
        // Show the dialog
        dialog.showAndWait();
    }


    public void action()
    {
        try {
            // Load the new FXML file
            System.out.println("Action");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addpublication(ActionEvent actionEvent)  {
        // Create a new Dialog
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("HelpMeBalance");

        // Create a label for the title
        Label titleLabel = new Label("Add Publication");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-alignment: center;");
        Button AddPictureButton = new Button("Add Picture");
        Label titlepublicationLabel = new Label("Title");
        Label contentLabel = new Label("Publication");
        TextField title = new TextField();
        TextArea content = new TextArea();
        CheckBox allowcoms = new CheckBox("Allow Comments");

        // Create an HBox to hold the title and image, and center them
        VBox titleImageBox = new VBox();
        titleImageBox.getChildren().addAll(titleLabel);
        titleImageBox.setAlignment(Pos.CENTER);
        titleImageBox.setSpacing(10);
        ComboBox<String> categoryComboBox = new ComboBox<>();
        ComboBox<String> subcategoryComboBox = new ComboBox<>();

        try {
            // Populate category ComboBox with names from categories
            List<Categorie> categories = catS.selectCategoriesWithSubcategories();

            for (Categorie categorie : categories) {
                categoryComboBox.getItems().add(categorie.getNom());
            }

            // Add listener to category ComboBox to populate subcategory ComboBox
            categoryComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    if (newValue != null && categories != null) {
                        try {
                            // Fetch selected category object
                            Categorie selectedCategory = categories.stream()
                                    .filter(c -> c.getNom().equals(newValue))
                                    .findFirst()
                                    .orElse(null);

                            // Populate subcategory ComboBox with names from selected category's subcategories
                            if (selectedCategory != null) {
                                List<SousCategorie> subcategories = souscatS.select(selectedCategory);
                                subcategoryComboBox.getItems().clear();
                                if (subcategories != null) {
                                    for (SousCategorie sousCategorie : subcategories) {
                                        subcategoryComboBox.getItems().add(sousCategorie.getNom());
                                    }
                                }
                                if (!subcategories.isEmpty()) {
                                    subcategoryComboBox.getSelectionModel().selectFirst();
                                }
                            }
                        } catch (SQLException e) {
                            e.printStackTrace(); // Handle the exception appropriately
                        }
                    }
                }
            });
            if (!categories.isEmpty()) {
                categoryComboBox.getSelectionModel().selectFirst();
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception appropriately
        }
        // Create a VBox to hold the content
        VBox contentBox = new VBox();
        contentBox.getChildren().addAll(titleImageBox,AddPictureButton,titlepublicationLabel,title,contentLabel,content,allowcoms,categoryComboBox,subcategoryComboBox);
        contentBox.setSpacing(10); // Adjust spacing as needed
        contentBox.setAlignment(Pos.CENTER_LEFT);
        Button addButton = new Button("Add Publication");
        HBox buttonBox = new HBox();
        buttonBox.getChildren().add(addButton);
        buttonBox.setAlignment(Pos.CENTER);
        // Create an action for the validate button
        addButton.setOnAction(event -> {
            try {
                Publication publication=new Publication(Session.getInstance().getUser(),catS.selectWhere(categoryComboBox.getValue()),souscatS.selectWhere(subcategoryComboBox.getValue()),title.getText(),content.getText(),allowcoms.isSelected(),false);
                pS.add(publication);
                publicationsList=FXCollections.observableArrayList(pS.select());
                Publications.setItems(publicationsList);
                dialog.close(); // Close the dialog after validation
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        });
        // Set the content of the dialog
        dialog.getDialogPane().setContent(new VBox(contentBox, buttonBox));
        // Set the button as the action for the dialog
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);
        dialog.getDialogPane().lookupButton(ButtonType.CLOSE).setVisible(false);
        // Show the dialog
        dialog.showAndWait();
    }
}
