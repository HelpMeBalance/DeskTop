package org.example.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.example.models.Commande;
import org.example.service.CommandeService;
import org.example.utils.Session;

import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class CommandeHistoryController {

    private final CommandeService commandeService = new CommandeService();

    @FXML
    private TableView<Commande> commandeTable;
    @FXML
    private TableColumn<Commande, Integer> idColumn;
    @FXML
    private TableColumn<Commande, String> addressColumn;
    @FXML
    private TableColumn<Commande, String> paymentMethodColumn;
    @FXML
    private TableColumn<Commande, Double> totalPriceColumn;
    @FXML
    private TableColumn<Commande, String> statusColumn;

    public void initialize() {
        setupTable();
        loadCommandes();
    }

    private void setupTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        paymentMethodColumn.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        totalPriceColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void loadCommandes() {
        try {
            int userId = Session.getInstance().getUser().getId();
            List<Commande> commandes = commandeService.getCommandHistoryForUser(userId);
            ObservableList<Commande> observableList = FXCollections.observableArrayList(commandes);
            commandeTable.setItems(observableList);
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle error
        }
    }
    public void generateCommandePDF(Commande commande, String logoPath, String filePath) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);
            int status = commande.getStatus();

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Set border color
                contentStream.setStrokingColor(Color.BLACK);
                // Draw a border around the page
                contentStream.addRect(50, 50, page.getMediaBox().getWidth() - 100, page.getMediaBox().getHeight() - 100);
                contentStream.stroke();

                // Load the logo image
                PDImageXObject logo = PDImageXObject.createFromFile(logoPath, document);

                // Draw the logo at a specific position on the page
                contentStream.drawImage(logo, 300, 550, logo.getWidth() / 2, logo.getHeight() / 2);

                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.newLineAtOffset(100, 700);

                // Write header
                contentStream.showText("Command Details");
                contentStream.newLineAtOffset(0, -40); // Move to the next line
                contentStream.newLine();

                // Write command details
                contentStream.showText("ID: " + commande.getId());
                contentStream.newLineAtOffset(0, -20); // Move to the next line
                contentStream.showText("Address: " + commande.getAddress());
                contentStream.newLineAtOffset(0, -20); // Move to the next line
                contentStream.showText("Payment Method: " + commande.getPaymentMethod());
                contentStream.newLineAtOffset(0, -20); // Move to the next line
                contentStream.showText("Total Price: $" + commande.getTotalPrice());
                contentStream.newLineAtOffset(0, -20); // Move to the next line
                contentStream.showText("Sale Code: " + commande.getSaleCode());
                contentStream.newLineAtOffset(0, -20); // Move to the next line
                contentStream.newLine(); // Move to the next line
                if (status == 0) {
                    contentStream.showText("Status: " + "Pending");
                    contentStream.newLineAtOffset(0, -20); // Move to the next line
                } else {
                    contentStream.showText("Status: " + "Delivered");
                    contentStream.newLineAtOffset(0, -20); // Move to the next line
                }

                contentStream.endText();
            }

            document.save(filePath);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle PDF generation error
        }
    }

    @FXML
    private void handleGeneratePDF() {
        Commande selectedCommande = commandeTable.getSelectionModel().getSelectedItem();
        if (selectedCommande != null) {
            String filePath = "C:/Users/MSI/Desktop/pdf/commande.pdf"; // Change this to your desired file path
            String logoPath = "C:/Users/MSI/Desktop/HelpMeBalance_DesktopFinal/DeskTop/src/main/resources/assets/logo.png";
            generateCommandePDF(selectedCommande, logoPath, filePath);
            showAlert("PDF Generated", "Command PDF has been generated successfully at: " + filePath);
        } else {
            showAlert("Selection Error", "Please select a commande from the table.");
        }
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void showAlertConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
