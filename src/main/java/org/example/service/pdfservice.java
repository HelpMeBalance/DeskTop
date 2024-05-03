package org.example.service;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import com.google.zxing.*;
import com.google.zxing.client.j2se.*;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.example.models.RendezVous;

import java.io.*;
import java.sql.SQLException;
import java.util.Date;


/**
 *
 * @author Mouad
 */
public class pdfservice {
    public static int rv=164;
    private RendezVousService rs=new RendezVousService();
    private RendezVous rvt;







    public void generatePdf(String filename) throws FileNotFoundException, DocumentException, BadElementException, IOException, InterruptedException, SQLException {
        // Create a new PDF document
        rvt=rs.selectWhere(rv);
        System.out.println(rvt.getPatient().getFirstname());
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filename + ".pdf"));
        document.open();

        // Define fonts
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, BaseColor.BLACK);
        Font regularFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);

        // Add a header
        Paragraph header = new Paragraph("Medical Certificate", titleFont);
        header.setAlignment(Element.ALIGN_CENTER);
        document.add(header);

        // Add a subheader with the doctor's name and patient details
        Paragraph doctorDetails = new Paragraph("Doctor's Name:"+rvt.getPsy().getFirstname()+rvt.getPsy().getLastname(), regularFont);
        Paragraph patientDetails = new Paragraph("Patient:"+rvt.getPatient().getFirstname()+rvt.getPatient().getLastname(), regularFont);
        Paragraph consultationDate = new Paragraph("Consultation Date:"+rvt.getDateR(), regularFont);

        document.add(doctorDetails);
        document.add(patientDetails);
        document.add(consultationDate);

        // Add the body of the certificate
        Paragraph certificateBody = new Paragraph();
        certificateBody.add("I, the undersigned, Dr. "+rvt.getPsy().getFirstname()+rvt.getPsy().getLastname()+", clinical psychologist, hereby certify that I conducted "
                + "an online psychological consultation session with"+rvt.getPatient().getFirstname()+rvt.getPatient().getLastname()+"on"+rvt.getDateR()+
                 "During this consultation, I assessed her mental health and well-being through virtual communication. "
                + "Based on my observations,"+rvt.getPatient().getFirstname()+rvt.getPatient().getLastname()+" does not present any major psychological disorder or disability that "
                + "could compromise her daily or professional activities. I recommend continued online psychological support to "
                + "maintain her emotional and mental well-being.");
        certificateBody.setAlignment(Element.ALIGN_JUSTIFIED);

        document.add(certificateBody);

        // Add footer with the current date
        Paragraph footer = new Paragraph("Generated on: " + new Date(), regularFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);

        document.close();

        // Open the PDF file
        Process pro = Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + filename + ".pdf");
    }
    @FXML
    private ImageView qrCodeImageView;

    public byte[] generateQRCode(String text, int width, int height) throws Exception {
        if (text == null || text.trim().isEmpty()) {
            text = "https://www.example.com"; // Set a valid default QR code content
        }

        // Generate the QR code
        BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height);

        // Convert the QR code to a byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "png", baos);

        return baos.toByteArray(); // Return the QR code as a byte array
    }

    @FXML
    public void handleGenerateQRCode() {
        String qrText = "http://192.168.242.1:8000/exportpdf/102"; // Example QR code text
        int width = 200;
        int height = 200;

        try {
            // Generate the QR code
            byte[] qrCodeData = generateQRCode(qrText, width, height);

            // Convert the byte array to an Image and set it to the ImageView
            Image qrImage = new Image(new ByteArrayInputStream(qrCodeData));
            qrCodeImageView.setImage(qrImage);
        } catch (Exception e) {
            System.err.println("Error generating or displaying QR code: " + e.getMessage());
            e.printStackTrace(); // Log the stack trace for debugging
        }
    }


    // Ajoutez la méthode generateQRCode ici, ou importez-la depuis le service

@FXML
    public void exportPdf(javafx.event.ActionEvent actionEvent) throws SQLException, DocumentException, IOException, InterruptedException {
        generatePdf("Contrat");
    }
}