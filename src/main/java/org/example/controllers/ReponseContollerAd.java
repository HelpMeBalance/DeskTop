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
    private TextArea questionTextArea;
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
        Reponse selectedQuestion = event;

            // A question is selected, show a dialog to update it
            TextInputDialog dialog = new TextInputDialog(selectedQuestion.getReponse());
            dialog.setTitle("Update Reponse");
            dialog.setHeaderText("Update selected reponse");
            dialog.setContentText("Enter the updated reponse:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(updatedQuestion -> {
                // Update the selected question with the new text
                selectedQuestion.setReponse(updatedQuestion);

                // Call the update method in the QuestionService to update the question in the database
                try {
                    reponseService.update(selectedQuestion);
                    // Refresh the TableView to reflect the changes
                    initQuestions();
                } catch (SQLException e) {
                    // Handle database errors
                    e.printStackTrace();
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Error");
                    errorAlert.setHeaderText("Database Error");
                    errorAlert.setContentText("An error occurred while updating the question.");
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
        } else {
            // Show an error message if the question text area is empty
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Question cannot be empty");
            alert.showAndWait();
        }
    }
}