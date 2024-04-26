package org.example.controllers;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcons;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.controlsfx.control.Rating;
import org.example.models.Consultation;
import org.example.models.RendezVous;
import org.example.models.User;
import org.example.service.ConsultationService;
import org.example.service.RendezVousService;
import org.example.service.UserService;
import org.example.utils.AppointmentStringConverter;
import org.example.utils.Navigation;
import org.example.utils.UserStringConverter;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

public class ConsultationAdminController implements Initializable {
    @FXML
    public TableView<Consultation> consultations;
    @FXML
    private TableColumn<Consultation, String> patient, psychiatrist, note, Recommandation_suivi;
    @FXML
    private TableColumn<Consultation, Void> actions;
    @FXML
    private Button addConsultation;
    private ObservableList<Consultation> conList;
    private ConsultationService conServ = new ConsultationService();
    private RendezVousService appServ = new RendezVousService();
    ComboBox<User> psyComboBox, patientComboBox;
    ComboBox<RendezVous> appComboBox;
    TextField noteed = new TextField();
    Rating rating;
    Label errorMessage = new Label();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try{
            ConInit();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void ConInit(){
        try {
            conList = FXCollections.observableArrayList(conServ.select());
            consultations.setItems(conList);

            patient.setCellValueFactory(cellData -> {
                User patient = cellData.getValue().getPatient();
                return new SimpleStringProperty(patient.getFirstname());
            });
            psychiatrist.setCellValueFactory(cellData -> {
                User psy = cellData.getValue().getPsy();
                return new SimpleStringProperty(psy.getFirstname());
            });
            note.setCellValueFactory(cellData -> {
                String note = cellData.getValue().getNote();
                return new SimpleStringProperty(note);
            });
            Recommandation_suivi.setCellValueFactory(cellData -> {
                return new SimpleStringProperty(cellData.getValue().isRecommandation_suivi()? "Recommand":"Do Not Recommand");
            });

            actions.setCellFactory(param -> new TableCell<>() {

                final Button viewButton = new Button();
                final Button updateButton = new Button();
                final Button deleteButton = new Button();

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
                        try {
                            showConsultationDetails(getTableView().getItems().get(getIndex()));
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    deleteButton.setOnAction(event -> {
                        try {
                            showDeleteConfirmationDialog(getTableView().getItems().get(getIndex()));
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    updateButton.setOnAction(event -> {
                        showUpdateDialog(getTableView().getItems().get(getIndex()), consultations, "/fxml/Admin/Consultation.fxml");
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

    public void showUpdateDialog(Consultation con, Node node, String fxml) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("HelpMeBalance");
        dialog.getDialogPane().setContent(createForm(con, dialog, node, fxml));
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);
        dialog.getDialogPane().lookupButton(ButtonType.CLOSE).setVisible(false);
        dialog.showAndWait();
    }

    public VBox createForm(Consultation con, Dialog dialog, Node node, String fxml) {
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(20.0);
        vbox.setStyle("-fx-background-color: #f4fcfa;");
        vbox.getStyleClass().add("form-container");

        HBox hbox1 = new HBox();
        hbox1.setAlignment(Pos.CENTER);
        hbox1.setPrefHeight(62.0);
        hbox1.setPrefWidth(579.0);
        hbox1.setSpacing(15);

        VBox vbox1 = new VBox();
        vbox1.setSpacing(10);
        Label label1 = new Label("Psychiatrist");
        label1.getStyleClass().add("combo-label");
        psyComboBox = new ComboBox<>();
        ArrayList<User> users = new ArrayList<>();
        try {
            UserService ps = new UserService();
            users = new ArrayList<>();
            for(var u: ps.select()){
                if (u.getRoles().contains("psy"))
                    users.add(u);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        psyComboBox.setItems(FXCollections.observableArrayList(users));
        psyComboBox.setConverter(new UserStringConverter());

        if(con.getPsy() != null){
            try {
                psyComboBox.setValue(new UserService().selectWhere(con.getPsy().getId()));
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }

        VBox vbox2 = new VBox();
        vbox2.setSpacing(10);
        Label label2 = new Label("Patient");
        label2.getStyleClass().add("combo-label");
        patientComboBox = new ComboBox<>();
        patientComboBox.setItems(FXCollections.observableArrayList(users));
        patientComboBox.setConverter(new UserStringConverter());
        vbox2.getChildren().addAll(label2, patientComboBox);
        if(con.getPatient() != null){
            try {
                patientComboBox.setValue(new UserService().selectWhere(con.getPatient().getId()));
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }

        psyComboBox.setPromptText("Psychiatrist");
        psyComboBox.getStyleClass().add("combo-box");
        vbox1.getChildren().addAll(label1, psyComboBox);

        HBox hbox3 = new HBox();
        hbox3.setAlignment(Pos.CENTER);
        hbox3.setSpacing(10);
        Label label3 = new Label("Appointment");
        label3.getStyleClass().add("combo-label");
        appComboBox = new ComboBox<>();
        try {
            ArrayList<RendezVous> appList = new ArrayList<>();
            for(var app: new RendezVousService().select() ){
                if (!app.isStatut())
                    appList.add(app);
            }
            appComboBox.setItems(FXCollections.observableArrayList(appList));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        appComboBox.setConverter(new AppointmentStringConverter());
        if(con.getPatient() != null){
            try {
                patientComboBox.setValue(new UserService().selectWhere(con.getPatient().getId()));
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        appComboBox.setPromptText("Appointment");
        if(con.getAppointment() != null){
            appComboBox.setValue(con.getAppointment());
        }
        appComboBox.getStyleClass().add("combo-box");
        hbox3.getChildren().addAll(label3, appComboBox);

        hbox1.getChildren().addAll(vbox1, vbox2);

        HBox hbox2 = new HBox();
        hbox2.setAlignment(Pos.CENTER);
        hbox2.setSpacing(15);
        Label label4 = new Label("Note");
        label4.getStyleClass().add("combo-label");
        noteed = new TextField();
        noteed.setPrefSize(250,60);
        if(con.getNote() != null){
            noteed.setText(con.getNote());
        }
        noteed.getStyleClass().add("date-picker");
        hbox2.getChildren().addAll(label4, noteed);

        HBox hBox4 = new HBox();
        hBox4.setAlignment(Pos.CENTER);
        hBox4.setSpacing(50.0);

        Label label5 = new Label("Rating");
        label4.getStyleClass().add("combo-label");
        rating = new Rating(5,0);
        if(con.getRating() != null){
            rating = new Rating(5, con.getRating().intValue());
        }

        hBox4.getChildren().addAll(label5, rating);

        Button addButton = new Button("Save");
        Rating finalRating = rating;
        addButton.setOnMouseClicked(e -> {
            try {
                if(!areFieldsValid())
                    return;
                RendezVous oldApp = con.getAppointment();
                con.update(appComboBox.getValue(), patientComboBox.getValue(), psyComboBox.getValue(), noteed.getText(), finalRating.getRating());
                if(node == addConsultation) {
                    conServ.add(con);
                }
                else {
                    conServ.update(con);
                    oldApp.setStatut(false);
                    appServ.update(oldApp);
                }
                RendezVous app = con.getAppointment();
                app.setPsy(con.getPsy());
                app.setPatient(con.getPatient());
                app.setStatut(true);
                appServ.update(app);
                dialog.close();
                Navigation.navigateTo(fxml, node);
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        addButton.getStyleClass().add("save-button");

        vbox.setPadding(new Insets(20.0));
        vbox.getChildren().addAll(hbox1, hbox3, hbox2, hBox4, addButton, errorMessage);

        return vbox;
    }

    private void showConsultationDetails(Consultation con) throws SQLException {
        // Create a new Dialog
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("HelpMeBalance");

        // Create a label for the title
        Label titleLabel = new Label("Appointment Details");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-alignment: center;");

        // Create an HBox to hold the button
        DialogPane dialogPane = new DialogPane();

        // Set header label
        Label headerLabel = new Label("Appointment Details");
        headerLabel.setFont(Font.font("System Bold", 12.0));
        headerLabel.setAlignment(Pos.TOP_CENTER);
        headerLabel.setContentDisplay(ContentDisplay.CENTER);
        headerLabel.setTextAlignment(TextAlignment.CENTER);
        dialogPane.setHeader(headerLabel);

        // Create VBox for content
        VBox contentVBox = new VBox();
        contentVBox.setPadding(new Insets(10.0));
        contentVBox.setSpacing(5.0);

        // Create and add labels for date, service, status, patient, and psychiatrist
        contentVBox.getChildren().add(new Label("Appointment ID: "+ con.getAppointment().getId()));
        contentVBox.getChildren().add(new Label("Patient: "+ con.getPatient().getFirstname()+ " " +con.getPatient().getLastname()));
        contentVBox.getChildren().add(new Label("Psychiatrist: "+ con.getPsy().getFirstname()+ " " +con.getPsy().getLastname()));
        contentVBox.getChildren().add(new Label("Note: "+ con.getNote()));
        contentVBox.getChildren().add(new Label("Rating: "+ con.getRating()));
        contentVBox.getChildren().add(new Label("follow-up: "+ (con.isRecommandation_suivi()? "Need follow-up":"Doesnt need follow-up")));

        // Set content to the VBox
        dialogPane.setContent(contentVBox);


        // Set the content of the dialog
        dialog.getDialogPane().setContent(dialogPane);

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

    private void showDeleteConfirmationDialog(Consultation con) throws SQLException {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Are you sure you want to delete this consultation ?");

        ButtonType confirmButton = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(confirmButton, cancelButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == confirmButton) {
            conServ.delete(con.getId());
            RendezVous app = con.getAppointment();
            app.setStatut(false);
            appServ.update(app);
            try {
                Navigation.navigateTo("/fxml/Admin/Consultation.fxml", consultations);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void handleAddConsultation(ActionEvent actionEvent) {
        try{
            showUpdateDialog(new Consultation(), addConsultation, "/fxml/Admin/Consultation.fxml");
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private boolean areFieldsValid() {
        boolean isValid = true;
        StringBuilder errors = new StringBuilder();

        if (Objects.equals(noteed.getText(), "")) {
            errors.append("incorrect Note.\n");
            isValid = false;
        }

        if (appComboBox.getValue()==null) {
            errors.append("Appointment must be filled.\n");
            isValid = false;
        }

        if (psyComboBox.getValue()==null) {
            errors.append("psy must be filled.\n");
            isValid = false;
        }

        if (patientComboBox.getValue()==null) {
            errors.append("Patient must be filled.\n");
            isValid = false;
        }

        if (rating.getRating()>5 || rating.getRating()<0) {
            errors.append("rating must be between 1 and 5.\n");
            isValid = false;
        }

        errorMessage.setText(errors.toString());
        return isValid;
    }
}
