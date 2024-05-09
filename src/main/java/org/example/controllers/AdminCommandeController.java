package org.example.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import org.example.models.Commande;
import org.example.service.CommandeService;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class AdminCommandeController {

    @FXML
    private TableView<Commande> commandeTable;
    @FXML
    private TableColumn<Commande, Integer> idColumn;
    @FXML
    private TableColumn<Commande, Integer> userIdColumn;
    @FXML
    private TableColumn<Commande, String> usernameColumn;
    @FXML
    private TableColumn<Commande, String> addressColumn;
    @FXML
    private TableColumn<Commande, String> paymentMethodColumn;
    @FXML
    private TableColumn<Commande, Integer> statusColumn;
    @FXML
    private TableColumn<Commande, Double> totalPriceColumn;
    @FXML
    private TableColumn<Commande, String> saleCodeColumn;
    @FXML
    private Button updateStatusButton;
    @FXML
    private TextField newStatusField;
    @FXML
    private BarChart<String, Number> statisticsChart;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;

    private final CommandeService commandeService = new CommandeService();

    public void initialize() {
        setupTable();
        loadCommandes();
    }

    private void setupTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        paymentMethodColumn.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        totalPriceColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        saleCodeColumn.setCellValueFactory(new PropertyValueFactory<>("saleCode"));
        // Customize rendering of statusColumn
        statusColumn.setCellFactory(new Callback<TableColumn<Commande, Integer>, TableCell<Commande, Integer>>() {
            @Override
            public TableCell<Commande, Integer> call(TableColumn<Commande, Integer> param) {
                return new TableCell<Commande, Integer>() {
                    @Override
                    protected void updateItem(Integer status, boolean empty) {
                        super.updateItem(status, empty);
                        if (empty || status == null) {
                            setText(null);
                        } else {
                            if (status == 0) {
                                setText("Pending");
                            } else {
                                setText("Delivered");
                            }
                        }
                    }
                };
            }
        });
    }

    private void loadCommandes() {
        try {
            List<Commande> commandes = commandeService.getAllCommandes();
            ObservableList<Commande> observableList = FXCollections.observableArrayList(commandes);
            commandeTable.setItems(observableList);
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle error
        }
    }

    @FXML
    private void handleUpdateStatus() {
        Commande selectedCommande = commandeTable.getSelectionModel().getSelectedItem();
        if (selectedCommande == null) {
            // No commande selected, show error message
            showAlert("Error", "Please select a commande to update its status.");
            return;
        }

        String newStatusText = newStatusField.getText();
        if (newStatusText.isEmpty()) {
            // New status field is empty, show error message
            showAlert("Error", "Please enter a new status value.");
            return;
        }

        try {
            int newStatus = Integer.parseInt(newStatusText);
            commandeService.updateCommandeStatus(selectedCommande.getId(), newStatus);
            // Refresh the table after updating status
            loadCommandes();
            // Clear the new status field
            newStatusField.clear();
        } catch (NumberFormatException e) {
            // Invalid status format, show error message
            showAlert("Error", "Please enter a valid integer for the new status.");
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle database error
            showAlert("Error", "An error occurred while updating the status. Please try again.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleShowStatistics() {
        try {
            Map<String, Integer> statistics = commandeService.getCommandeStatisticsByPaymentMethod();
            displayStatisticsChart(statistics);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while retrieving statistics. Please try again.");
        }
    }

    private void displayStatisticsChart(Map<String, Integer> statistics) {
        xAxis.setLabel("Payment Method");
        yAxis.setLabel("Total Count");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (String paymentMethod : statistics.keySet()) {
            series.getData().add(new XYChart.Data<>(paymentMethod, statistics.get(paymentMethod)));
        }

        statisticsChart.getData().clear();
        statisticsChart.getData().add(series);
    }

}
