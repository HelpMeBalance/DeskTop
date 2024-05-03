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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.models.Publication;
import org.example.models.Question;
import org.example.models.Reponse;
import org.example.models.User;
import org.example.service.QuestionService;
import org.example.service.ReponseService;
import org.example.service.UserService;
import org.example.utils.Navigation;

import javax.imageio.IIOParam;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class QuizeControllerAd implements Initializable {
    private QuestionService questionService = new QuestionService();
    @FXML
    private TextField questionTextArea;
    @FXML
    private Label errorMessage; // Add a label in your FXML to display errors

    private ReponseService reponseService = new ReponseService();
    private ArrayList<Reponse> reponseListe ;
    List<String> re = new ArrayList<>();
    @FXML
    private TableView<Question> questionsTable;

    @FXML
    private TableColumn<Question, String> questionColumn;

    @FXML
    private TableColumn<Question, String> dateColumn;

    @FXML
    private TableColumn<Question, String> activeColumn;

    @FXML
    private TableColumn<Question, Void> actions;

    ObservableList<Question> questionsList;
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


        try
        {
            questionsList = FXCollections.observableArrayList(questionService.select());
            questionsTable.setItems(questionsList);

            questionColumn.setCellValueFactory(new PropertyValueFactory<>("question"));

            dateColumn.setCellValueFactory(cellData -> {
                LocalDateTime dateTime = cellData.getValue().getDate();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm:ss");
                String formattedDateTime = dateTime.format(formatter);
                return new SimpleStringProperty(formattedDateTime);
            });

            activeColumn.setCellValueFactory(cellData -> {
                boolean active = cellData.getValue().getActive();
                return new SimpleStringProperty(active ? "Active" : "Inactive");
            });
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
                Question question = getTableView().getItems().get(getIndex());

                try {
                    showQuestionDetails(question);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });

            updateButton.setOnAction(event -> {
                Question question = getTableView().getItems().get(getIndex());
                updateQuestion(question);
            });

            deleteButton.setOnAction(event -> {
                Question question = getTableView().getItems().get(getIndex());

                try {
                    showDeleteConfirmationDialog(question);
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
        private void showDeleteConfirmationDialog(Question question) throws SQLException {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Confirm Deletion");
            alert.setHeaderText("Are you sure you want to delete the question ?");
            alert.setContentText("Queston : " + question.getQuestion());

            ButtonType confirmButton = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(confirmButton, cancelButton);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == confirmButton) {
                questionService.delete(question.getId());
                questionsList.remove(question);
                questionsTable.refresh();
            }
        }

    private void showQuestionDetails(Question question) throws SQLException {
        // Create a new Dialog
        reponsess = reponseService.select();
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("HelpMeBalance");

        // Create a label for the title
        Label titleLabel = new Label("Question Details");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-alignment: center;");
        re.clear();
        for (Reponse reponse : reponsess) {
            if (reponse.get$question().getId() == question.getId()) {
                re.add(reponse.getReponse());
                // Add to answer VBox
            }
        }

        // Set the content text with publication details
        Label contentLabel = new Label(
                "question : " + question.getQuestion() + "\n" +
                        "reponse : " + re + "\n"
        );

        // Create a button for validating the publication
         Button validateButton = new Button("edt");
        validateButton.setOnAction(event -> {

            // Get the selected question
            Question selectedQuestion = question;
            ReponseContollerAd.qes=selectedQuestion;
            // Check if a question is selected

                try {
                    // Navigate to the ReponseControllerAd and pass the selected question's ID as a parameter
                     Navigation.navigateTo("/fxml/Admin/reponse.fxml", questionsTable);

                    // Pass the question ID to the ReponseControllerAd
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            dialog.close(); // Close the dialog after validation
        });
        // Create an HBox to hold the button
        HBox buttonBox = new HBox(validateButton);
        buttonBox.setAlignment(Pos.CENTER);

        // Set the content of the dialog
        dialog.getDialogPane().setContent(new VBox(contentLabel, buttonBox));

        // Set the button as the action for the dialog
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);
        dialog.getDialogPane().lookupButton(ButtonType.CLOSE).setVisible(false);
        dialog.getDialogPane().addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (!dialog.getDialogPane().getBoundsInLocal().contains(event.getX(), event.getY())) {
                dialog.close();
            }
        });

        // Show the dialog
        dialog.showAndWait();
    }
    private void updateQuestion(Question event) {
        Question selectedQuestion = event;


        // A question is selected, show a dialog to update it
        TextInputDialog dialog = new TextInputDialog(selectedQuestion.getQuestion());

        dialog.setTitle("Update Question");
        dialog.setHeaderText("Update selected question");
        dialog.setContentText("Enter the updated question:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(updatedQuestion -> {
            // Check if the updated question meets the length requirement
            if (updatedQuestion.length() < 6 || updatedQuestion.isEmpty()||updatedQuestion.charAt(updatedQuestion.length()-1)!='?') {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error");
                errorAlert.setHeaderText("Validation Error");
                errorAlert.setContentText("Question must be longer than 6 characters and not empty and end with '?'.");
                errorAlert.showAndWait();
                return; // Stop further processing if validation fails
            }

            // Update the selected question with the new text
            selectedQuestion.setQuestion(updatedQuestion);

            // Call the update method in the QuestionService to update the question in the database
            try {
                questionService.update(selectedQuestion);
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
    public void addQuestion(ActionEvent event) {
        String questionText = questionTextArea.getText();

        if (!areFieldsValid()) {
            return; // Stop the registration process if validation fails
        }
        if (!questionText.isEmpty()) {
            System.out.println("g,ddbdbb");
            try {
                // Create a new Question object
                Question question = new Question();
                question.setQuestion(questionText);

                // Add the question to the database
                questionService.add(question);

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

        if (questionTextArea.getText().length()<6 || questionTextArea.getText()=="") {
            errors.append("queston must be longer than 6\n");
            isValid = false;
        }
        if ( questionTextArea.getText()=="") {
            errors.append("queston must be filled in.\n");
            isValid = false;
        }
        if ( questionTextArea.getText().charAt(questionTextArea.getText().length()-1)!='?') {
            errors.append("queston must end with ?.\n");
            isValid = false;
        }



        errorMessage.setText(errors.toString());
        return isValid;
    }
    }

