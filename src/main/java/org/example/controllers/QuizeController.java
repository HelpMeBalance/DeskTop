package org.example.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.example.models.Formulaire;
import org.example.models.Question;
import org.example.models.RendezVous;
import org.example.models.Reponse;
import org.example.service.FormulaireService;
import org.example.service.FormulairejService;
import org.example.service.QuestionService;
import org.example.service.ReponseService;
import org.example.utils.Navigation;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;



public class QuizeController implements Initializable {

    private QuestionService questionService;
    public static int rv;
    private FormulaireService formulaireService = new FormulaireService();
    private FormulairejService formulairejService = new FormulairejService();
    private ReponseService reponseService;
    @FXML
    private VBox radioButtonGroup;

    private ToggleGroup toggleGroup;

    @FXML
    private ComboBox<String> combo;

    @FXML
    private Label idq;

    @FXML
    private Button nextButton;

    private int currentQuestionIndex;
    private List<Question> questions;
    private List<Reponse> reponses;
    private List<Reponse> allReponses;
    private List<String> selectedAnswers; // to store selected answers for each question

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        questionService = new QuestionService();
        reponseService = new ReponseService();
        currentQuestionIndex = 0;
        selectedAnswers = new ArrayList<>();

        try {
            questions = questionService.select();
            allReponses = reponseService.select();
            loadQuestionAndResponses();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void loadQuestionAndResponses() {
        if (currentQuestionIndex < questions.size()) {
            Question currentQuestion = questions.get(currentQuestionIndex);
            idq.setText(currentQuestion.getQuestion());
            radioButtonGroup.getChildren().clear();
            toggleGroup = new ToggleGroup();

            for (Reponse response : allReponses) {
                if (response.get$question().getId() == currentQuestion.getId()) {
                    RadioButton radioButton = new RadioButton(response.getReponse());
                    radioButton.setToggleGroup(toggleGroup);
                    radioButtonGroup.getChildren().add(radioButton);
                }
            }
        }
    }

    @FXML
    private void loadNextQuestion() throws SQLException, IOException {
        if (currentQuestionIndex < questions.size()) {
            RadioButton selectedRadioButton = (RadioButton) toggleGroup.getSelectedToggle();
            if (selectedRadioButton == null) {
                // Show alert if the user skips the question
                showAlert("Please select an answer before moving to the next question!");
                return; // Do not proceed further until the user selects an answer
            }

            // Proceed to load the next question
            String selectedResponse = selectedRadioButton.getText();
            selectedAnswers.add(selectedResponse);

            currentQuestionIndex++;
            loadQuestionAndResponses();
        } else {
            // Record answers and navigate
            recordAnswersAndNavigate();
        }
    }
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Input Validation Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void recordAnswersAndNavigate() throws SQLException, IOException {
        Formulaire formulaire = new Formulaire();
        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            String selectedAnswer = selectedAnswers.get(i);
            int questionId = question.getId();
            int responseId = findResponseId(questionId, selectedAnswer);

            formulaireService.add1(formulaire, questionId, responseId,rv);
            formulairejService.add1(formulaire.getId(), question.getQuestion(), selectedAnswer,rv);
        }
        FormulaireController.rv=rv;
        Navigation.navigateTo("/fxml/Quiz/Formulaire.fxml", idq);
    }

    private int findResponseId(int questionId, String selectedAnswer) {
        for (Reponse response : allReponses) {
            if (response.get$question().getId() == questionId && Objects.equals(response.getReponse(), selectedAnswer)) {
                return response.getId();
            }
        }
        return -1; // Return -1 if no response found (which should not happen ideally)
    }
}
