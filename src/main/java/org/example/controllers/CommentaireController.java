package org.example.controllers;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcons;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.models.Commentaire;
import org.example.models.Publication;
import org.example.service.CommentaireService;
import org.example.service.PublicationService;
import org.example.utils.Navigation;
import org.example.utils.Session;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class CommentaireController implements Initializable {
    public TextField SearchComment;
    public Hyperlink hlFirst;
    public Pagination pagination;
    public Hyperlink hlLast;
    // Define the page size
    private static final int PAGE_SIZE = 3;
    public Label paginationLegend;
    private CommentaireService cS=new CommentaireService();
    private PublicationService pS=new PublicationService();
    @FXML
    private TableView Comments;

    @FXML
    private TableColumn<Commentaire, String> content;

    @FXML
    private TableColumn<Commentaire, String> userName;

    @FXML
    private TableColumn<Commentaire, String> state;

    @FXML
    private TableColumn<Commentaire, String> postedAt;

    @FXML
    private TableColumn<Commentaire, String> updatedAt;
    @FXML
    private TableColumn<Commentaire, Void> validate;

    @FXML
    private TableColumn<Commentaire, Void> actions;
    @FXML
    private Button goback;

    @FXML
    private Label publicationtitle;

    @FXML
    private Button addcomment;
    ObservableList<Commentaire> commentsList=FXCollections.observableArrayList();
    private int publicationId;
    private Publication pub;
    String searchText=null;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
    private void loadComments(int pageIndex) throws SQLException {
        List<Commentaire> commentaires=new ArrayList<>();
        int totalcoms=cS.countComments(pub);
        if(searchText != null && !searchText.isEmpty()) {
            commentaires = cS.search(searchText,publicationId, pagination.getCurrentPageIndex(), PAGE_SIZE);
            totalcoms=cS.search(searchText,publicationId).size();
            pagination.setPageCount((totalcoms + PAGE_SIZE - 1) / PAGE_SIZE);
        }else {
            pagination.setPageCount((cS.countComments(pub) + PAGE_SIZE - 1) / PAGE_SIZE);
            commentaires = cS.selectAdmin(publicationId,pagination.getCurrentPageIndex(), PAGE_SIZE);

        }
        commentsList.setAll(commentaires);
        updatePaginationLegend(pageIndex,PAGE_SIZE,totalcoms);
    }
    private void updatePaginationLegend(int pageIndex, int pageSize, int totalcoms) {
        int start = pageIndex * pageSize + 1;
        int end = Math.min(start + pageSize - 1, totalcoms);
        paginationLegend.setText("Showing " + start + " to " + end + " of " + totalcoms + " comments");
    }
    private void initComments() {
        try
        {
           pub=pS.selectWhere(publicationId);
            publicationtitle.setText(pub.getTitre());
            addcomment.setVisible(pub.getCom_ouvert());
            pagination.setPageCount((cS.countComments(pub) + PAGE_SIZE - 1) / PAGE_SIZE);

            //commentsList = FXCollections.observableArrayList(cS.select(publicationId));
            //Comments.setItems(commentsList);
            loadComments(pagination.getCurrentPageIndex());
            Comments.setItems(commentsList);
            content.setCellValueFactory(new PropertyValueFactory<>("contenu"));
            userName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUser().getLastname()+" "+cellData.getValue().getUser().getFirstname()));
            state.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getValide() ? "Approved" : "Under Review"));
            postedAt.setCellValueFactory(cellData -> {
                LocalDateTime dateTime = cellData.getValue().getDate_c();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm:ss");
                String formattedDateTime = dateTime.format(formatter);
                return new SimpleStringProperty(formattedDateTime);
            });
            updatedAt.setCellValueFactory(cellData -> {
                LocalDateTime dateTime = cellData.getValue().getDate_m();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm:ss");
                String formattedDateTime = dateTime.format(formatter);
                return new SimpleStringProperty(formattedDateTime);
            });
            validate.setCellFactory(param -> new TableCell<Commentaire, Void>() {
                Button validateButton = new Button();

                {
                    validateButton.setStyle("-fx-background-color: transparent;");
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                        setGraphic(null);
                    } else {
                        Commentaire comment = getTableRow().getItem(); // Retrieve the Commentaire associated with the current row
                        String stateValue = comment.getValide() ? "Unvalidate" : "Validate";
                        validateButton.setText(stateValue); // Set the text of the button based on the state value
                        validateButton.setOnAction(event -> {
                            if (comment != null) {
                                try {
                                    // Proceed with your validation logic
                                    cS.validate(comment.getId());
                                    commentsList.replaceAll(com -> {
                                        if (com.getId() == comment.getId()) {
                                            com.setValide(!com.getValide());
                                        }
                                        return com;
                                    });
                                    Comments.refresh();
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        });
                        setGraphic(validateButton);
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
                        Commentaire comment = getTableView().getItems().get(getIndex());
                        // Handle view action here using publication.getId() or other relevant data
                        showUpdatecommentDialog(comment);
                    });

                    deleteButton.setOnAction(event -> {
                        Commentaire comment = getTableView().getItems().get(getIndex());
                        try {
                            showDeleteConfirmationDialog(comment);
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
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private void showUpdatecommentDialog(Commentaire comment) {
        // Create a new Dialog
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("HelpMeBalance");

        // Create a label for the title
        Label titleLabel = new Label("Edit Comment");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-alignment: center;");

        Label contentLabel = new Label("Content");
        TextField content = new TextField(comment.getContenu());
        // Create an HBox to hold the title and image, and center them
        VBox titleImageBox = new VBox();
        titleImageBox.getChildren().addAll(titleLabel);
        titleImageBox.setAlignment(Pos.CENTER);
        titleImageBox.setSpacing(10);
        // Create a VBox to hold the content
        VBox contentBox = new VBox();
        contentBox.getChildren().addAll(titleImageBox,contentLabel,content);
        contentBox.setSpacing(10); // Adjust spacing as needed
        contentBox.setAlignment(Pos.CENTER_LEFT);
        Button updateButton = new Button("Update Comment");
        HBox buttonBox = new HBox();
        buttonBox.getChildren().add(updateButton);
        buttonBox.setAlignment(Pos.CENTER);
        // Create an action for the validate button
        updateButton.setOnAction(event -> {
            try {
                comment.setContenu(content.getText());
                cS.update(comment);
                 commentsList.replaceAll(com -> {
                    if ( com.getId() == comment.getId()) {
                        com=comment;
                    }
                    return com;
                });
                Comments.refresh();
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


    private void showDeleteConfirmationDialog(Commentaire comment) throws SQLException {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Are you sure you want to delete the comment ?");
        alert.setContentText(" Posted By : "+comment.getUser().getLastname()+" "+comment.getUser().getFirstname());

        ButtonType confirmButton = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(confirmButton, cancelButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == confirmButton) {
            cS.delete(comment.getId());
            pagination.setPageCount((cS.countComments(pub) + PAGE_SIZE - 1) / PAGE_SIZE);
            pagination.setCurrentPageIndex(0);
            loadComments(0);
        }
    }
    public int getPublicationId() {
        return publicationId;
    }

    public void setPublicationId(int publicationId) {
        this.publicationId = publicationId;
        initComments();
        pagination.currentPageIndexProperty().addListener((obs, oldPageIndex, newPageIndex) -> {
            try {
                loadComments(newPageIndex.intValue());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void goback(ActionEvent actionEvent) throws IOException {
        Navigation.navigateTo("/fxml/Admin/Publication.fxml",goback);
    }

    public void addcomment(ActionEvent actionEvent) {
        // Create a new Dialog
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("HelpMeBalance");

        // Create a label for the title
        Label titleLabel = new Label("Add Comment");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-alignment: center;");

        Label contentLabel = new Label("Content");
        TextField content = new TextField();
        // Create an HBox to hold the title and image, and center them
        VBox titleImageBox = new VBox();
        titleImageBox.getChildren().addAll(titleLabel);
        titleImageBox.setAlignment(Pos.CENTER);
        titleImageBox.setSpacing(10);
        // Create a VBox to hold the content
        VBox contentBox = new VBox();
        contentBox.getChildren().addAll(titleImageBox,contentLabel,content);
        contentBox.setSpacing(10); // Adjust spacing as needed
        contentBox.setAlignment(Pos.CENTER_LEFT);
        Button addButton = new Button("Post Comment");
        HBox buttonBox = new HBox();
        buttonBox.getChildren().add(addButton);
        buttonBox.setAlignment(Pos.CENTER);
        // Create an action for the validate button
        addButton.setOnAction(event -> {
            try {
                Commentaire commentaire =new Commentaire(Session.getInstance().getUser(),pub,content.getText(),false);
                cS.add(commentaire);
                pagination.setCurrentPageIndex(0);
                loadComments(0);
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

    public void handleSearch() throws SQLException {
        searchText = SearchComment.getText().toLowerCase(); // Get the text from the TextField
        pagination.setCurrentPageIndex(0);
        loadComments(0);
    }
    public void handleFirstPage() {
        pagination.setCurrentPageIndex(0);
    }
    public void handleLastPage() {
        pagination.setCurrentPageIndex(pagination.getPageCount() - 1);
    }
}
