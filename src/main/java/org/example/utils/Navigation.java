package org.example.utils; // Adjust the package name to fit your project structure

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Stage;
import org.example.controllers.CommentaireController;
import org.example.controllers.SousCategorieController;

import java.io.IOException;

public class Navigation {

    // Utility method to navigate to a different screen within the same Stage
    public static void navigateTo(String fxmlFile, Node currentNode) throws IOException {
        // Load the new FXML file
        Parent newContent = FXMLLoader.load(Navigation.class.getResource(fxmlFile));

        // Get the current stage using the node from the current scene
        Stage stage = (Stage) currentNode.getScene().getWindow();

        // Replace the scene's root with the new content
        stage.getScene().setRoot(newContent);
    }

    public static void navigateTo(String fxmlFile, Node currentNode, int Id) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Navigation.class.getResource(fxmlFile));
        Parent newContent = fxmlLoader.load();
        Object controller = fxmlLoader.getController();
        if (controller instanceof CommentaireController) {
            CommentaireController commentaireController = (CommentaireController) controller;
            commentaireController.setPublicationId(Id);
        }
        else if (controller instanceof SousCategorieController) {
            SousCategorieController sousCategorieController = (SousCategorieController) controller;
            sousCategorieController.setCategorieId(Id);
        }
        Stage stage = (Stage) currentNode.getScene().getWindow();
        stage.getScene().setRoot(newContent);
    }
}
