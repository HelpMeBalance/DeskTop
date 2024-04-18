package org.example.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.models.Question;
import org.example.service.QuestionService;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
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

    ObservableList<Question> questionsList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initQuestions();
    }

    public void initQuestions() {
        try {
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Add any additional methods for handling user interactions here
}
