package org.example.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import org.example.models.Formulaire;
import org.example.models.Question;
import org.example.models.Reponse;
import org.example.service.FormulaireService;
import org.example.service.FormulairejService;
import org.example.service.QuestionService;
import org.example.service.ReponseService;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class QuizeController implements Initializable {

    private QuestionService questionService;
    private FormulaireService formulaireService = new FormulaireService();
    private FormulairejService formulairejService = new FormulairejService();
    private ReponseService reponseService;

    @FXML
    private ComboBox<String> combo;

    @FXML
    private Label idq;
    int idr;

    @FXML
    private Button nextButton;

    private int currentQuestionIndex;
    private List<Question> questions;
    private List<Reponse> reponses;
    public List<Reponse> reponsess = new ArrayList<>();

    List<String> re = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        questionService = new QuestionService();
        reponseService = new ReponseService();
        currentQuestionIndex = -1;

        try {
            questions = questionService.select();
            loadNextQuestion();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleNextButtonClick(ActionEvent event) throws SQLException {
        loadNextQuestion();
    }



   @FXML
   private int jet() throws SQLException {
        if (currentQuestionIndex < questions.size()) {
            Question nextQuestion = questions.get(currentQuestionIndex);
            try {
                reponsess = reponseService.select();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            String selectedResponse = combo.getValue();
            System.out.println(selectedResponse);
            for (Reponse reponse : reponsess) {
                if ( (Objects.equals(reponse.getReponse(), selectedResponse))) {
                    idr=reponse.getId();
                }

            }
            System.out.println(idr);
        }
 return idr;   }
    private void loadNextQuestion() throws SQLException {
        currentQuestionIndex++;
        reponsess.clear();
        combo.getItems().clear();
        if (currentQuestionIndex < questions.size()) {
            Formulaire formulaire = new Formulaire();
            Question nextQuestion = questions.get(currentQuestionIndex);
            idq.setText(nextQuestion.getQuestion());
            combo.getItems().clear();
            reponsess.clear();

            try {
                reponsess = reponseService.select();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            re.clear();
            for (Reponse reponse : reponsess) {
                if (reponse.get$question().getId() == questions.get(currentQuestionIndex).getId()) {
                    re.add(reponse.getReponse());
                    // Add to answer VBox
                }
            }
            for (var repos : re) {
                combo.getItems().add(repos); // Renamed from answerComboBox
            }

            System.out.println(idr);
            int idqu = nextQuestion.getId();
            String qj= nextQuestion.getQuestion();
            String rj="";
            for (Reponse reponse : reponsess) {
                if ( reponse.getId()==idr ) {
                    rj=reponse.getReponse();
                }}
            System.out.println(idqu);
            formulaireService.add1(formulaire, idqu, idr);
            formulairejService.add1(formulaire.getId(),qj,rj);
        }
        else
        {
            // No more questions
            idq.setText("End of Quiz");
            combo.getItems().clear();
            nextButton.setDisable(true);
        }
    }
}

