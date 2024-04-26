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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.models.Question;
import org.example.models.Reponse;
import org.example.service.QuestionService;
import org.example.service.ReponseService;
import org.example.utils.Navigation;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ReponseContollerAd implements Initializable {
    private QuestionService questionService = new QuestionService();
    private ReponseService reponseService = new ReponseService();
    @FXML
    private TextField questionTextArea;
    @FXML
    private Label errorMessage;
    @FXML
    private Button goback;

    public static Question qes;

    @FXML
    private TableView<Reponse> questionsTable;

    @FXML
    private TableColumn<Reponse, String> questionColumn;

    @FXML
    private TableColumn<Reponse, Void> actions;

    ObservableList<Question> questionsList;
    ObservableList<Reponse> re;
    ObservableList<Reponse> reponseList;
    public List<Reponse> reponsess = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            initQuestions();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void initQuestions() throws SQLException {

        System.out.println(qes.getQuestion());
        try
        {
            reponseList = FXCollections.observableArrayList(reponseService.selectq(qes.getId()));

            questionsTable.setItems(reponseList );
            questionColumn.setCellValueFactory(new PropertyValueFactory<>("reponse"));


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
//                    viewButton.setStyle("-fx-background-color: transparent;");
                    updateButton.setStyle("-fx-background-color: transparent;");
                    deleteButton.setStyle("-fx-background-color: transparent;");


                    updateButton.setOnAction(event -> {
                        Reponse reponsee = getTableView().getItems().get(getIndex());
                        updateQuestion(reponsee);

                    });

                    deleteButton.setOnAction(event -> {
                        Reponse reponsee = getTableView().getItems().get(getIndex());

                        try {
                            showDeleteConfirmationDialog(reponsee);
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
                        HBox buttonsBox = new HBox( updateButton, deleteButton);
                        buttonsBox.setSpacing(5);
                        setGraphic(buttonsBox);
                    }
                }
            });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private void showDeleteConfirmationDialog(Reponse question) throws SQLException {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Are you sure you want to delete the reponse?");
        alert.setContentText("Reponse : " + question.getReponse());

        ButtonType confirmButton = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(confirmButton, cancelButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == confirmButton) {
            reponseService.delete(question.getId());
            reponseList.remove(question);
            questionsTable.refresh();
        }
    }

    private void updateQuestion(Reponse event) {
        Reponse selectedReponse = event;

        // A response is selected, show a dialog to update it
        TextInputDialog dialog = new TextInputDialog(selectedReponse.getReponse());
        dialog.setTitle("Update Response");
        dialog.setHeaderText("Update selected response");
        dialog.setContentText("Enter the updated response:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(updatedResponse -> {
            // Check if the updated response meets the length requirement
            if ( updatedResponse.isEmpty()) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error");
                errorAlert.setHeaderText("Validation Error");
                errorAlert.setContentText("Response must be not empty.");
                errorAlert.showAndWait();
                return; // Stop further processing if validation fails
            }

            // Update the selected response with the new text
            selectedReponse.setReponse(updatedResponse);

            // Call the update method in the ReponseService to update the response in the database
            try {
                reponseService.update(selectedReponse);
                // Refresh the TableView to reflect the changes
                initQuestions();
            } catch (SQLException e) {
                // Handle database errors
                e.printStackTrace();
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error");
                errorAlert.setHeaderText("Database Error");
                errorAlert.setContentText("An error occurred while updating the response.");
                errorAlert.showAndWait();
            }
        });
    }




    public void action()
    {
        try
        {
            // Load the new FXML file
            System.out.println("Action");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void addRuestion(ActionEvent event) {
        String questionText = questionTextArea.getText();
        if (!areFieldsValid()) {
            return; // Stop the registration process if validation fails
        }

        if (!questionText.isEmpty()) {

            try {
                // Create a new Question object
                String reponse = questionText; // Removed extra spaces
                Question question = qes;
                reponseService.add(new Reponse( reponse, question));



                // Refresh the TableView to display the new question
                initQuestions();
            } catch (SQLException e) {
                e.printStackTrace();
                // Handle database errors
            }
        }

    }
    private boolean areFieldsValid() {

        boolean isValid = true;
        StringBuilder errors = new StringBuilder();
        if ( questionTextArea.getText()=="") {
            errors.append("reponse must be filled in.\n");
            isValid = false;
        }
        errorMessage.setText(errors.toString());
        return isValid;
    }
    public void goback(ActionEvent actionEvent) throws IOException {
        Navigation.navigateTo("/fxml/Admin/Quize.fxml",goback);
    }
}