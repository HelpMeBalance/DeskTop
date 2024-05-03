package org.example.controllers;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcons;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import org.example.models.*;
import org.example.service.*;
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

public class FormulaireController implements Initializable {
    private FormulairejService fs=new FormulairejService();
    public static int rv;
    private QuestionService questionService = new QuestionService();
    private likeeService l=new likeeService();


    private boolean like;


    @FXML
    private Label questionLabel;

    @FXML
    private Button yesButton;

    @FXML
    private Button noButton;

    @FXML
    private TableView form;
    @FXML
    private TableView forme;
    @FXML
    private TableColumn<String, String> quc;

    @FXML
    private TableColumn<String, Integer> rec;

    private ArrayList<Formulaireq> formulaireQuestions;
    private ArrayList<Formulairej> formulairej;


    ObservableList<Question> questionsList;
    ObservableList<Formulairej> formulairejList;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            initformulaire();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void initformulaire() throws SQLException {

        List<Formulairej> fetchedData = fs.selectWhere1(rv);
        formulairejList = FXCollections.observableArrayList(fetchedData);
        form.setItems(formulairejList);
        quc.setCellValueFactory(new PropertyValueFactory<>("question"));
        rec.setCellValueFactory(new PropertyValueFactory<>("reponse"));

    }
    @FXML
    private void handleNoButtonClick() throws SQLException {
        // Logic when "No" is clicked
        likee li = new likee(); // Example 'likee' instance
        l.addLikee(li,false); // Adds 'likee' to the database
        noButton.setDisable(true); // Disable the "No" button after clicking
        yesButton.setDisable(true); // Optionally, disable the "Yes" button as well
        questionLabel.setText("Vous n'aimez pas les questions.");
    }
    @FXML
    private void handleYesButtonClick() throws SQLException {
        // Logic when "Yes" is clicked
        likee li = new likee(); // Example 'likee' instance
        l.addLikee(li,true); // Adds 'likee' to the database
        yesButton.setDisable(true); // Disable the "Yes" button after clicking
        noButton.setDisable(true); // Optionally, disable the "No" button as well
        questionLabel.setText("Vous aimez les questions!");
    }
    @FXML
    private void record() throws IOException {
        Navigation.navigateTo("/fxml/Appointment/AppointmentDisplay.fxml", form);
    }


}
