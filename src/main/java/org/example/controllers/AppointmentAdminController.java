package org.example.controllers;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcons;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.example.models.Consultation;
import org.example.models.RendezVous;
import org.example.models.User;
import org.example.service.ConsultationService;
import org.example.service.RendezVousService;
import org.example.service.UserService;
import org.example.utils.Navigation;
import org.example.utils.UserStringConverter;
import org.w3c.dom.ls.LSOutput;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class AppointmentAdminController implements Initializable {
    @FXML
    private TableView<RendezVous> appointments;
    @FXML
    private TableColumn<RendezVous, String> patient, psychiatrist, datetime, service;
    @FXML
    private TableColumn<RendezVous, Void> actions, chart;
    @FXML
    private Button addAppointment;
    @FXML
    private Pagination pagination;
    @FXML
    private TextField searchField;
    private final RendezVousService appServ = new RendezVousService();
    private ObservableList<RendezVous> appointList;
    ComboBox<User> psyComboBox, patientComboBox;
    ComboBox<String> serviceComboBox;
    DatePicker datePicker;
    CheckBox statusCheckBox, certificatCheckBox;
    Label errorMessage = new Label();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        AppointInit();
        PaginationInit();
        updateTableData(0);
    }

    public void AppointInit(){
        try {
            appointList = FXCollections.observableArrayList(appServ.select());
            appointments.setItems(appointList);

            patient.setCellValueFactory(cellData -> {
                User patient = cellData.getValue().getPatient();
                return new SimpleStringProperty(patient.getFirstname()+ " " + patient.getLastname());
            });
            psychiatrist.setCellValueFactory(cellData -> {
                User psy = cellData.getValue().getPsy();
                return new SimpleStringProperty(psy.getFirstname()+ " " + psy.getLastname());
            });
            chart.setCellFactory(param -> new TableCell<>() {
                final Button chart = new Button();
                {
                    FontAwesomeIcon viewIcon = new FontAwesomeIcon();
                    viewIcon.setIcon(FontAwesomeIcons.PIE_CHART);
                    chart.setGraphic(viewIcon);

                    chart.setStyle("-fx-background-color: transparent;");
                    chart.setOnAction(event -> {
                        try {
                            ArrayList<Integer> rating = new ArrayList<>(List.of(0,0,0,0,0));
                            int totale=0;
                            for(var c: new ConsultationService().select()){
                                if(c.getPsy().getId() == getTableView().getItems().get(getIndex()).getPsy().getId() && c.getRating()>=0 && c.getRating()!=null){
                                    rating.set(c.getRating().intValue()-1, rating.get(c.getRating().intValue()-1)+1);
                                    totale++;
                                }
                            }
                            System.out.println(rating);

                            if(totale!=0){

                                PieChart pieChart = new PieChart();
                                pieChart.setAnimated(true);
                                pieChart.setTitle("Rating By Psy");

                                List<PieChart.Data> pieChartData = new ArrayList<>();

                                pieChartData.add(new PieChart.Data("1 STAR: "+String.format("%.1f", (double)100*rating.get(0)/totale)+"%", rating.get(0)));
                                pieChartData.add(new PieChart.Data("2 STAR: "+String.format("%.1f", (double)100*rating.get(1)/totale)+"%", rating.get(1)));
                                pieChartData.add(new PieChart.Data("3 STAR: "+String.format("%.1f", (double)100*rating.get(2)/totale)+"%", rating.get(2)));
                                pieChartData.add(new PieChart.Data("4 STAR: "+String.format("%.1f", (double)100*rating.get(3)/totale)+"%", rating.get(3)));
                                pieChartData.add(new PieChart.Data("5 STAR: "+String.format("%.1f", (double)100*rating.get(4)/totale)+"%", rating.get(4)));

                                pieChart.getData().addAll(pieChartData);

                                Dialog<Void> dialog = new Dialog<>();
                                dialog.setTitle("HelpMeBalance");
                                dialog.getDialogPane().setContent(pieChart);
                                dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);
                                dialog.getDialogPane().lookupButton(ButtonType.CLOSE).setVisible(false);
                                dialog.showAndWait();
                            }
                        } catch (SQLException e) {
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
                        HBox buttonsBox = new HBox(chart);
                        buttonsBox.setSpacing(5);
                        setGraphic(buttonsBox);
                    }
                }

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
                        try {
                            showUpdateDialog(getTableView().getItems().get(getIndex()), appointments, "/fxml/Admin/Appointment.fxml");
                        } catch (SQLException e) {
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

    public void showUpdateDialog(RendezVous app, Node node, String fxml) throws SQLException {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("HelpMeBalance");
        dialog.getDialogPane().setContent(createForm(app, dialog, node, fxml));
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);
        dialog.getDialogPane().lookupButton(ButtonType.CLOSE).setVisible(false);
        dialog.showAndWait();
    }

    public VBox createForm(RendezVous app, Dialog dialog, Node node, String fxml) throws SQLException {
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

        if(app.getPsy() != null){
            try {
                psyComboBox.setValue(new UserService().selectWhere(app.getPsy().getId()));
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }

        VBox vbox2 = new VBox();
        vbox2.setSpacing(10);
        Label label2 = new Label("Patient");
        label2.getStyleClass().add("combo-label");
        patientComboBox = new ComboBox<>();
        patientComboBox.setItems(FXCollections.observableArrayList(new UserService().select()));
        patientComboBox.setConverter(new UserStringConverter());
        vbox2.getChildren().addAll(label2, patientComboBox);
        if(app.getPatient() != null){
            try {
                patientComboBox.setValue(new UserService().selectWhere(app.getPatient().getId()));
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
        Label label3 = new Label("Service");
        label3.getStyleClass().add("combo-label");
        serviceComboBox = new ComboBox<>();
        serviceComboBox.setItems(FXCollections.observableArrayList("Individual", "Couple", "Child"));
        serviceComboBox.setValue(app.getNomService());
        serviceComboBox.setPromptText("Service");
        serviceComboBox.getStyleClass().add("combo-box");
        hbox3.getChildren().addAll(label3, serviceComboBox);

        hbox1.getChildren().addAll(vbox1, vbox2);

        HBox hbox2 = new HBox();
        hbox2.setAlignment(Pos.CENTER);
        hbox2.setSpacing(15);
        Label label4 = new Label("Date");
        label4.getStyleClass().add("combo-label");
        datePicker = new DatePicker();
        if(app.getDateR() != null){
            datePicker.setValue(app.getDateR().toLocalDate());
        }
        datePicker.getStyleClass().add("date-picker");
        hbox2.getChildren().addAll(label4, datePicker);

        HBox hBox4 = new HBox();
        hBox4.setAlignment(Pos.CENTER);
        hBox4.setSpacing(50.0);

        // Create the Checkboxes
        statusCheckBox = new CheckBox("Status");
        statusCheckBox.setSelected(app.isStatut());
        certificatCheckBox = new CheckBox("Certificat");
        certificatCheckBox.setSelected(app.isCertificat());

        // Add Checkboxes to the HBox
        hBox4.getChildren().addAll(statusCheckBox, certificatCheckBox);

        Button addButton = new Button("Save");
        addButton.setOnMouseClicked(e -> {
            if(!areFieldsValid())
                return;
            try {
                RendezVousService rs = new RendezVousService();
                ConsultationService cs = new ConsultationService();
                boolean oldStatus = app.isStatut();
                app.update(datePicker.getValue().atStartOfDay(), serviceComboBox.getValue(), statusCheckBox.isSelected(), certificatCheckBox.isSelected(), psyComboBox.getValue(), patientComboBox.getValue());
                app.setCertificat(certificatCheckBox.isSelected());
                if(node == addAppointment) {
                    try {
                        System.out.println("appointment to add: "+app);
                        rs.add(app);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                else {
                    if(!oldStatus) {
                        if (app.isStatut()) {
                            Consultation c = new Consultation(app, app.getPsy(), app.getPatient());
                            c.setRating(null);
                            cs.add(c);
                        }
                    }
                    else{
                        if (!app.isStatut()){
                            for(var c: cs.select()){
                                if(app.getId() == c.getAppointment().getId()){
                                    cs.delete(c.getId());
                                }
                            }
                        }
                        else {
                            System.out.println(app.getId());
                            Consultation c = null;
                            for(var con: cs.select()){
                                if(con.getAppointment().getId()==app.getId())
                                    c = con;
                            }
                            assert c != null;
                            c.update(app,app.getPsy(),app.getPatient(),c.getNote(),c.getRating());
                            cs.update(c);
                        }
                    }
                    rs.update(app);
                }

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
        headerLabel.setAlignment(Pos.TOP_CENTER);
        headerLabel.setContentDisplay(ContentDisplay.CENTER);
        headerLabel.setTextAlignment(TextAlignment.CENTER);
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

    public void handleAddAppointment() {
        try{
            showUpdateDialog(new RendezVous(), addAppointment, "/fxml/Admin/Appointment.fxml");
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
    private boolean areFieldsValid() {
        boolean isValid = true;
        StringBuilder errors = new StringBuilder();

        if (datePicker.getValue().isBefore(LocalDate.now())) {
            errors.append("incorrect Date.\n");
            isValid = false;
        }

        if (serviceComboBox.getValue()==null) {
            errors.append("service must be filled.\n");
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

        errorMessage.setText(errors.toString());
        return isValid;
    }

    public void update() {
        try {
            appointList = FXCollections.observableArrayList(appServ.select());

            String searchText = searchField.getText().trim().toLowerCase();
            appointList = appointList.filtered(app ->
                    app.getPsy().getFirstname().toLowerCase().contains(searchText) ||
                            app.getPsy().getLastname().toLowerCase().contains(searchText) ||
                            app.getPatient().getFirstname().toLowerCase().contains(searchText) ||
                            app.getPatient().getLastname().toLowerCase().contains(searchText) ||
                            app.getNomService().toLowerCase().contains(searchText)
            );

            appointments.setItems(appointList);
            appointments.refresh();

            PaginationInit();
            updateTableData(0);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateTableData(int pageIndex) {
        try {
            int startIndex = pageIndex * 4;
            int endIndex = Math.min(startIndex + 4, appointList.size());

            // Ensure endIndex is within the bounds of the appointList
            endIndex = Math.min(endIndex, appointList.size());

            ObservableList<RendezVous> pageData = FXCollections.observableArrayList(appointList.subList(startIndex, endIndex));

            // Set the data to the table
            appointments.setItems(pageData);
            appointments.refresh();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void PaginationInit() {
        pagination.setMaxPageIndicatorCount((int) Math.ceil((double) appointList.size() / 4));
        pagination.currentPageIndexProperty().addListener((observable, oldValue, newValue) -> updateTableData(newValue.intValue()));
    }
}
