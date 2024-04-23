package org.example.controllers;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcons;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import org.example.models.RendezVous;
import org.example.models.User;
import org.example.service.RendezVousService;
import org.example.utils.AppointmentDisplayBox;
import org.example.utils.Navigation;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;

public class AppointmentAdminController implements Initializable {
    @FXML
    private TableView<RendezVous> appointments;
    @FXML
    private TableColumn<RendezVous, String> patient, psychiatrist, datetime, service;
    @FXML
    private TableColumn<RendezVous, Void> actions;
    private RendezVousService appServ = new RendezVousService();
    private ObservableList<RendezVous> appointList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        AppointInit();
    }

    public void AppointInit(){
        try {
            appointList = FXCollections.observableArrayList(appServ.select());
            appointments.setItems(appointList);

            patient.setCellValueFactory(cellData -> {
                User patient = cellData.getValue().getPatient();
                return new SimpleStringProperty(patient.getFirstname());
            });
            psychiatrist.setCellValueFactory(cellData -> {
                User psy = cellData.getValue().getPsy();
                return new SimpleStringProperty(psy.getFirstname());
            });
            datetime.setCellValueFactory(cellData -> {
                LocalDateTime dateTime = cellData.getValue().getDateR();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm");
                String formattedDateTime = dateTime.format(formatter);
                return new SimpleStringProperty(formattedDateTime);
            });
            service.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNomService()));

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
                            showAppointmentDetails(getTableView().getItems().get(getIndex()));
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
                        new AppointmentDisplayBox().showUpdateDialog(getTableView().getItems().get(getIndex()), appointments, "/fxml/Admin/Appointment.fxml");
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

    private void showAppointmentDetails(RendezVous app) throws SQLException {
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
        headerLabel.setAlignment(javafx.geometry.Pos.TOP_CENTER);
        headerLabel.setContentDisplay(javafx.scene.control.ContentDisplay.CENTER);
        headerLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        dialogPane.setHeader(headerLabel);

        // Create VBox for content
        VBox contentVBox = new VBox();
        contentVBox.setPadding(new Insets(10.0));
        contentVBox.setSpacing(5.0);

        // Create and add labels for date, service, status, patient, and psychiatrist
        LocalDateTime dateTime = app.getDateR();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm");
        String formattedDateTime = dateTime.format(formatter);
        contentVBox.getChildren().add(new Label("Date: "+ formattedDateTime));
        contentVBox.getChildren().add(new Label("Service: "+ app.getNomService()));
        contentVBox.getChildren().add(new Label("Status: "+ (app.isStatut()? "Confirmed":"Not Confirmed")));
        contentVBox.getChildren().add(new Label("Patient: "+ app.getPatient().getFirstname()+ " " +app.getPatient().getLastname()));
        contentVBox.getChildren().add(new Label("Psychiatrist: "+ app.getPsy().getFirstname()+ " " +app.getPsy().getLastname()));

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

    private void showDeleteConfirmationDialog(RendezVous app) throws SQLException {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Are you sure you want to delete this appointment ?");

        ButtonType confirmButton = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(confirmButton, cancelButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == confirmButton) {
            appServ.delete(app.getId());
            try {
                Navigation.navigateTo("/fxml/Admin/Appointment.fxml", appointments);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
