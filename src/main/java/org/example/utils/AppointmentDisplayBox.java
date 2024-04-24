package org.example.utils;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.transform.Scale;
import org.controlsfx.control.Rating;
import org.example.models.Consultation;
import org.example.models.RendezVous;
import org.example.models.User;
import org.example.service.ConsultationService;
import org.example.service.RendezVousService;
import org.example.service.UserService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class AppointmentDisplayBox {
    VBox vBox = new VBox();
    ConsultationService conServ = new ConsultationService();
    public VBox AppointmentDisplayBox(String date, String confirmation, String serviceName, RendezVous app) throws SQLException {
        // Create VBox
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setStyle("-fx-background-color: #f4fcfa;");
        vBox.getStyleClass().add("form-container");

        // Create HBox
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.TOP_CENTER);

        // Create ImageView
        ImageView imageView = new ImageView(new Image("assets/appointDisplay.png"));
        imageView.setFitHeight(50);
        imageView.setFitWidth(50);

        // Create VBox inside HBox
        VBox innerVBox = new VBox();
        innerVBox.setPrefHeight(VBox.USE_COMPUTED_SIZE);
        innerVBox.setPrefWidth(VBox.USE_COMPUTED_SIZE);

        // Create Labels inside inner VBox
        Label dateLabel = new Label(date);
        dateLabel.setFont(new Font(dateLabel.getFont().getSize() * 1.3)); // 20% bigger
        Label confirmationLabel = new Label(confirmation);
        confirmationLabel.setFont(new Font(confirmationLabel.getFont().getSize() * 1.3)); // 20% bigger
        innerVBox.getChildren().addAll(dateLabel, confirmationLabel);
        innerVBox.setPadding(new Insets(10, 5, 0, 5));

        // Create Rating
        Consultation con = new Consultation();
        if (app.isStatut()){
            for(var c: conServ.select()){
                if(c.getAppointment().getId() == app.getId() && c.getRating()!=null && c.getRating()>=0){
                    con = c;
                }
            }
        }

        Rating rating = new Rating(5, 0);
        rating.setDisable(true);
        rating.setOpacity(1.0);
        if (con.getRating()!=null) {
            rating = new Rating(5, con.getRating().intValue());
            rating.setDisable(true);
            rating.setOpacity(1.0);
        }
        rating.getTransforms().add(new Scale(0.5, 0.5));
        rating.setPadding(new Insets(10, 0, 0, 0));
        rating.setMinWidth(10);
        rating.setMaxWidth(10);

        //the edit and delete button icons
        ImageView deleteIcon = new ImageView(new Image("assets/bin.png"));
        deleteIcon.setFitWidth(20);
        deleteIcon.setFitHeight(20);

        // Set event handler for clicking on the delete icon
        deleteIcon.setOnMouseClicked(e -> {
            try {
                new RendezVousService().delete(app.getId());
                Navigation.navigateTo("/fxml/Appointment/AppointmentDisplay.fxml", deleteIcon);
            } catch (SQLException | IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        //
        ImageView editIcon = new ImageView(new Image("assets/edit.png"));
        editIcon.setFitWidth(20);
        editIcon.setFitHeight(20);

        // Set event handler for clicking on the edit icon
        editIcon.setOnMouseClicked(e -> {
            showUpdateDialog(app, vBox, "/fxml/Appointment/AppointmentDisplay.fxml");
        });

        // Add icons to HBox
        HBox editDeleteHBox = new HBox(editIcon, deleteIcon);
        editDeleteHBox.setAlignment(Pos.TOP_CENTER);

        VBox editDeleteVBox = new VBox(editDeleteHBox);
        if(app.isStatut())
            try {
                for (var c : new ConsultationService().select()){
                    if(c.getAppointment().getId() == app.getId()){
                        if (c.getRating() < 0){
                            editDeleteVBox.getChildren().add(new Label("add a slider or a popup to add rating"));
                        }
                        else
                            editDeleteVBox.getChildren().add(rating);
                    }
                }
            }catch(SQLException e){
                System.out.println(e.getMessage());
            }
        editDeleteVBox.setPadding(new Insets(10, 30, 0, 30));
        editDeleteVBox.setAlignment(Pos.TOP_CENTER);

        // Add children to HBox
        hBox.getChildren().addAll(imageView, innerVBox, editDeleteVBox);
        hBox.setPadding(new Insets(10, 10, 10, 10));

        // Create Label outside HBox with custom service name
        Label serviceNameLabel = new Label(serviceName);
        serviceNameLabel.setPadding(new Insets(0, 0, 10, 0));
        serviceNameLabel.setFont(new Font(serviceNameLabel.getFont().getSize() * 1.3)); // 20% bigger

        // Add children to VBox
        vBox.getChildren().addAll(hBox, serviceNameLabel);
        return vBox;
    }
    public void showUpdateDialog(RendezVous app, Node node, String fxml) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("HelpMeBalance");
        dialog.getDialogPane().setContent(createForm(app, dialog, node, fxml));
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);
        dialog.getDialogPane().lookupButton(ButtonType.CLOSE).setVisible(false);
        dialog.showAndWait();
    }
    public VBox createForm(RendezVous app, Dialog dialog, Node node,String fxml) {
        VBox vbox = new VBox();
        vbox.setAlignment(javafx.geometry.Pos.CENTER);
        vbox.setSpacing(20.0);
        vbox.setStyle("-fx-background-color: #f4fcfa;");
        vbox.getStyleClass().add("form-container");

        HBox hbox1 = new HBox();
        hbox1.setAlignment(javafx.geometry.Pos.CENTER);
        hbox1.setPrefHeight(62.0);
        hbox1.setPrefWidth(579.0);
        hbox1.setSpacing(15);

        VBox vbox1 = new VBox();
        vbox1.setSpacing(10);
        Label label1 = new Label("Psychiatrist");
        label1.getStyleClass().add("combo-label");
        ComboBox<User> psyComboBox = new ComboBox<>();
        ArrayList<User> users = new ArrayList<>();
        try{
            UserService ps = new UserService();
            users = new ArrayList<>(ps.select());
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        // End test

        psyComboBox.setItems(FXCollections.observableArrayList(users));
        psyComboBox.setConverter(new UserStringConverter());

        try{
            psyComboBox.setValue(new UserService().selectWhere(app.getPsy().getId()));
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }

        psyComboBox.setPromptText("Psychiatrist");
        psyComboBox.getStyleClass().add("combo-box");
        vbox1.getChildren().addAll(label1, psyComboBox);

        VBox vbox2 = new VBox();
        vbox2.setSpacing(10);
        Label label2 = new Label("Service");
        label2.getStyleClass().add("combo-label");
        ComboBox<String> serviceComboBox = new ComboBox<>();
        serviceComboBox.setItems(FXCollections.observableArrayList("Individual","Couple","Child"));
        serviceComboBox.setValue(app.getNomService());
        serviceComboBox.setPromptText("Service");
        serviceComboBox.getStyleClass().add("combo-box");
        vbox2.getChildren().addAll(label2, serviceComboBox);

        hbox1.getChildren().addAll(vbox1, vbox2);

        HBox hbox2 = new HBox();
        hbox2.setAlignment(javafx.geometry.Pos.CENTER);
        hbox2.setSpacing(15);
        Label label3 = new Label("Date");
        label3.getStyleClass().add("combo-label");
        DatePicker datePicker = new DatePicker();
        datePicker.setValue(app.getDateR().toLocalDate());
        datePicker.getStyleClass().add("date-picker");
        hbox2.getChildren().addAll(label3, datePicker);

        Button addButton = new Button("Save");
        addButton.setOnMouseClicked(e -> {
            try{
                RendezVousService rs = new RendezVousService();
                app.update(datePicker.getValue().atStartOfDay(), serviceComboBox.getValue(), app.isStatut(), app.isCertificat(), psyComboBox.getValue(), app.getPatient());
                rs.update(app);

                dialog.close();
                Navigation.navigateTo(fxml, node);
            }catch (SQLException ex){
                System.out.println(ex.getMessage());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

        });
        addButton.getStyleClass().add("save-button");

        vbox.setPadding(new Insets(20.0));

        vbox.getChildren().addAll(hbox1, hbox2, addButton);

        return vbox;
    }
}
