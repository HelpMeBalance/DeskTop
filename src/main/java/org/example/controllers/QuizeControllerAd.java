package org.example.controllers;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcons;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import org.example.models.Question;
import org.example.models.User;
import org.example.service.QuestionService;
import org.example.service.UserService;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class QuizeControllerAd implements Initializable {
    private QuestionService questionService = new QuestionService();

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            initQuestions();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void initQuestions() throws SQLException {

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
            });}

            private void setupActionsColumn() {
                actions.setCellFactory(param -> new TableCell<Question, Void>() {
                    private final HBox container = new HBox(10); // space between buttons
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
                            Question qes = getTableView().getItems().get(getIndex());
                            handleView(qes);
                        });
                        updateButton.setOnAction(event -> {
                            Question qes = getTableView().getItems().get(getIndex());
                            handleUpdate(qes);
                        });
                        deleteButton.setOnAction(event -> {
                            Question qes = getTableView().getItems().get(getIndex());
                            handleDelete(qes);
                        });

                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : container);
                    }
                });
            }

            private void handleView(Question user) {
                // Implement the view action
            }

            private void handleUpdate(Question user) {
                // Implement the update action
            }

            private void handleDelete(Question user) {
                // Implement the delete action
            }

            private void loadUsers() {
                UserService userService = new UserService();
                try {
                    questionsList = FXCollections.observableArrayList(questionService.select());
                    questionsTable.setItems(questionsList);
                } catch (SQLException e) {
                    e.printStackTrace(); // Proper error handling should be implemented
                }
            }

            public void action(ActionEvent actionEvent) {
            }
}
