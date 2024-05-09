package org.example.controllers;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.models.Consultation;
import org.example.models.RendezVous;
import org.example.service.ConsultationService;
import org.example.service.RendezVousService;
import org.example.utils.Navigation;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

public class ConsultationDisplay implements Initializable {
    public static Consultation con;
    @FXML
    public Button certifButton;
    @FXML
    private Label noteLabel, recommandationLabel;
    @FXML
    private FontAwesomeIcon delete, edit;
    private ConsultationService conServ = new ConsultationService();
    private RendezVousService appServ = new RendezVousService();
    TextField noteed = new TextField();
    CheckBox followUp;
    Label errorMessage = new Label();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        InitCon(con);
    }

    public void InitCon(Consultation con){
        noteLabel.setText(con.getNote());
        recommandationLabel.setText(con.isRecommandation_suivi()? "Recommend a follow-up":"Do not need a follow-up");
        if(con.getAppointment().isCertificat())
            certifButton.setDisable(true);
    }

    public void handleDelete() {
        try {
            showDeleteConfirmationDialog(con);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
                Navigation.navigateTo("/fxml/Appointment/PsyAppointmentDisplay.fxml", noteLabel);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void handleEdit() {
        showUpdateDialog(con, noteLabel, "/fxml/Appointment/PsyConsultationDisplay.fxml");
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
        hbox1.setSpacing(15);
        Label label1 = new Label("Follow-Up");
        label1.getStyleClass().add("combo-label");
        followUp = new CheckBox();
        followUp.setSelected(con.isRecommandation_suivi());
        followUp.getStyleClass().add("date-picker");
        hbox1.getChildren().addAll(label1, followUp);

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

        Button addButton = new Button("Save");

        addButton.setOnMouseClicked(e -> {
            try {
                if(!areFieldsValid())
                    return;
                RendezVous oldApp = con.getAppointment();
                con.update(noteed.getText(), followUp.isSelected());

                conServ.update(con);
                oldApp.setStatut(false);
                appServ.update(oldApp);

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
        vbox.getChildren().addAll(hbox2, hbox1, addButton, errorMessage);

        return vbox;
    }

    private boolean areFieldsValid() {
        boolean isValid = true;
        StringBuilder errors = new StringBuilder();

        if (Objects.equals(noteed.getText(), "")) {
            errors.append("incorrect Note.\n");
            isValid = false;
        }

        errorMessage.setText(errors.toString());
        return isValid;
    }

    public void RedirectToConsultations() {
        try {
            Navigation.navigateTo("/fxml/Appointment/PsyAppointmentDisplay.fxml", noteLabel);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void ActivateCertificate() {
        con.getAppointment().setCertificat(true);
        try {
            appServ.update(con.getAppointment());
            certifButton.setDisable(true);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
