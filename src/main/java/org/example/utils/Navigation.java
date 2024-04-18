package org.example.utils; // Adjust the package name to fit your project structure

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

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
}
