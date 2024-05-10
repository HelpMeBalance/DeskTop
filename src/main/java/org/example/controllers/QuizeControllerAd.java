package org.example.controllers;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcons;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.example.models.Formulairej;
import org.example.models.Question;
import org.example.models.Reponse;
import org.example.models.likee;
import org.example.service.FormulairejService;
import org.example.service.QuestionService;
import org.example.service.ReponseService;
import org.example.service.likeeService;
import org.example.utils.Navigation;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class QuizeControllerAd implements Initializable {
    private QuestionService questionService = new QuestionService();
    private FormulairejService fo= new FormulairejService();
    @FXML
    private TextField questionTextArea;

    @FXML
    private Label errorMessage; // Add a label in your FXML to display errors

    private ReponseService reponseService = new ReponseService();

    private likeeService li=new likeeService();

    @FXML
    private TextField searchField;
    private ArrayList<Reponse> reponseListe ;
    private List<likee> l ;
    @FXML
    private Stage primaryStage;



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
    @FXML
    private VBox pieChartContainer;

    private FilteredList<Question> filteredData;
    public List<Reponse> reponsess = new ArrayList<>();
    public List<Formulairej> form = new ArrayList<>();
    private static final int ROWS_PER_PAGE = 4;
    @FXML
    private Pagination pagination;
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            initQuestions();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            PieChart pieChart = showStatisticsForfor();

            // Ensure the VBox exists and add the PieChart to it
            if (pieChartContainer != null) {
                pieChartContainer.getChildren().add(pieChart); // Add the PieChart to the specified VBox
            } else {
                System.out.println("pieChartContainer is null, cannot add PieChart.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        loadUsers();
        setupTable();
        setupSearchFilter();
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
                try {
                    reponsess = reponseService.select();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                re.clear();
                for (Reponse reponse : reponsess) {
                    if (reponse.get$question().getId() == cellData.getValue().getId()) {
                        re.add(reponse.getReponse());
                        // Add to answer VBox
                    }
                }
                if(re.size()>1)
                {
                    cellData.getValue().setActive(true);
                }
                else  {cellData.getValue().setActive(false);}
                System.out.println(re.size());

                try {
                    questionService.update(cellData.getValue());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
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

                Question selectedQuestion = getTableView().getItems().get(getIndex());
                try {
                    showQuestionDetails(selectedQuestion);
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
                } catch (SQLException | IOException e) {
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
        private void showDeleteConfirmationDialog(Question question) throws SQLException, IOException {
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
                Navigation.navigateTo("/fxml/Admin/Quize.fxml",questionsTable);
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
        titleLabel.setFont(new Font("Arial", 16));

        re.clear();
        for (Reponse reponse : reponsess) {
            if (reponse.get$question().getId() == question.getId()) {
                re.add(reponse.getReponse());
            }
        }

        // Set the content text with question details
        Label contentLabel = new Label(
                "Question: " + question.getQuestion() + "\n" +
                        "Responses: " + re.toString() + "\n"
        );

        // Create a PieChart to show statistics
        PieChart pieChart = showStatisticsForQuestion(question);

        // Create a button for editing
        Button validateButton = new Button("Edit");
        validateButton.setOnAction(event -> {
            // Get the selected question
            Question selectedQuestion = question;
            ReponseContollerAd.qes=selectedQuestion;
            // Navigate to a different page or take appropriate action
            try {
                Navigation.navigateTo("/fxml/Admin/reponse.fxml", questionsTable);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            dialog.close(); // Close the dialog after validation
        });

        // Create an HBox to hold the button
        HBox buttonBox = new HBox(validateButton);
        buttonBox.setAlignment(Pos.CENTER);

        // Add all the components to a VBox
        VBox dialogContent = new VBox(10, titleLabel, contentLabel, pieChart, buttonBox);
        dialogContent.setPadding(new Insets(10));

        // Set the content of the dialog
        dialog.getDialogPane().setContent(dialogContent);

        // Add the close button
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);

        // Show the dialog and wait for it to close
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
                Navigation.navigateTo("/fxml/Admin/Quize.fxml",questionsTable);
            } catch (SQLException e) {
                // Handle database errors
                e.printStackTrace();
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error");
                errorAlert.setHeaderText("Database Error");
                errorAlert.setContentText("An error occurred while updating the question.");
                errorAlert.showAndWait();
            } catch (IOException e) {
                throw new RuntimeException(e);
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

            try {
                // Create a new Question object
                Question question = new Question();
                question.setQuestion(questionText);

                // Add the question to the database
                questionService.add(question);

                // Refresh the TableView to display the new question
                initQuestions();
                Navigation.navigateTo("/fxml/Admin/Quize.fxml",questionsTable);
            } catch (SQLException e) {
                e.printStackTrace();
                // Handle database errors
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private boolean areFieldsValid() {
        boolean isValid = true;
        StringBuilder errors = new StringBuilder();
        if (questionTextArea.getText().length()<6 ) {
            errors.append("queston must be longer than 6\n");
            isValid = false;
        }
        if (questionTextArea.getText().equals("")) {
            errors.append("question must be filled in.\n");
            isValid = false;
        }
        if (!questionTextArea.getText().endsWith("?")) {
            errors.append("question must end with '?'.\n");
            isValid = false;
        }
        errorMessage.setText(errors.toString());
        return isValid;
    }
    private void setupSearchFilter() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(user -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return user.getQuestion().toLowerCase().contains(lowerCaseFilter)
                        ;
            });
            updatePagination();

        });
    }
    private Node createPage(int pageIndex) {
        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, filteredData.size());
        questionsTable.setItems(FXCollections.observableArrayList(filteredData.subList(fromIndex, toIndex)));
        return new VBox(questionsTable); // Make sure this returns a Node
    }
    private void updatePagination() {
        int totalPageCount = (int) Math.ceil(filteredData.size() / (double) ROWS_PER_PAGE);
        pagination.setPageCount(totalPageCount);
        pagination.setCurrentPageIndex(0); // Reset to first page
        pagination.setPageFactory(this::createPage);
    }
    private void loadUsers() {
        QuestionService userService = new QuestionService();
        try {
            ObservableList<Question> users = FXCollections.observableArrayList(userService.select());
            filteredData = new FilteredList<>(users, p -> true);
            SortedList<Question> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(questionsTable.comparatorProperty());
            questionsTable.setItems(sortedData);
            updatePagination();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void setupTable() {
        loadUsers(); // Now call loadUsers() after pagination initialization
    }


    private PieChart showStatisticsForQuestion(Question question) throws SQLException {
        re.clear(); // Clear previous responses
        form = fo.select(); // Fetch form data

        // Create a map to count occurrences of each response
        Map<String, Integer> responseCounts = new HashMap<>();

        // Initialize the response count map and add unique responses to `re`
        for (Reponse reponse : reponsess) {
            if (reponse.get$question().getId() == question.getId()) {
                String responseText = reponse.getReponse();
                if (!re.contains(responseText)) {
                    re.add(responseText);
                    responseCounts.put(responseText, 0); // Initialize the count
                }
            }
        }

        // Count the occurrences of each response in the form data
        int totalResponses = 0; // To calculate total count for percentage
        for (Formulairej entry : form) {
            String responseText = entry.getReponse();
            if (responseCounts.containsKey(responseText)) {
                // Increment the count for the corresponding response
                int newCount = responseCounts.get(responseText) + 1;
                responseCounts.put(responseText, newCount);
                totalResponses++; // Increment the total response count
            }
        }

        // Create PieChart data from the response counts with percentages
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        for (Map.Entry<String, Integer> entry : responseCounts.entrySet()) {
            String responseText = entry.getKey();
            int count = entry.getValue();

            // Calculate the percentage
            double percentage = ((double) count / totalResponses) * 100;

            // Create a label with the response text and percentage
            String label = String.format("%s (%.1f%%)", responseText, percentage);

            pieChartData.add(new PieChart.Data(label, count)); // Use the count for pie chart
        }

        // Create the pie chart
        PieChart pieChart = new PieChart(pieChartData);
        pieChart.setTitle("Response Distribution");

        return pieChart; // Return the pie chart
    }
    @FXML
    private PieChart showStatisticsForfor() throws SQLException {
        // Clear previous PieChart data and calculate new statistics
        List<likee> likeeList = li.getAllLikees();

        int likeCount = 0;
        int dislikeCount = 0;

        for (likee le : likeeList) {
            if (le.isLike()) {
                likeCount++;
            } else {
                dislikeCount++;
            }
        }
        System.out.println(likeCount);
        double percentage1 = ((double) likeCount / likeeList.size()) * 100;
        double percentage2 = ((double) dislikeCount / likeeList.size()) * 100;

        String label = String.format("%s (%.1f%%)", "Like", percentage1);
        String label1 = String.format("%s (%.1f%%)", "DisLike",percentage2);
        // Create PieChart data based on the calculated statistics
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data(label, likeCount),
                new PieChart.Data(label1, dislikeCount)
        );

        // Create the PieChart with the computed data
        PieChart pieChart = new PieChart(pieChartData);
        pieChart.setTitle("Like/Dislike Statistics");

        // Optional: customize the PieChart's appearance
        pieChart.setLabelsVisible(true);
        pieChart.setClockwise(true);
        pieChart.setStartAngle(90);

        return pieChart; // Return the PieChart for further use
    }




}

