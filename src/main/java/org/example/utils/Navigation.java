package org.example.utils; // Adjust the package name to fit your project structure

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
//import org.example.controllers.QuizeControllerAd;
import org.example.models.Question;

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
    public static int navigateToAndReturnId(String fxmlFile, Node currentNode) throws IOException {
        // Load the new FXML file
        FXMLLoader loader = new FXMLLoader(Navigation.class.getResource(fxmlFile));
        Parent newContent = loader.load();

        // Get the controller associated with the loaded FXML file
        Object controller = loader.getController();
        if (controller instanceof Initializable) {
            ((Initializable) controller).initialize(null, null); // Initialize the controller if it implements Initializable
        }

        // Get the current stage using the node from the current scene
        Stage stage = (Stage) currentNode.getScene().getWindow();

        // Replace the scene's root with the new content
        stage.getScene().setRoot(newContent);

        // Return the ID of the loaded FXML file
        return loader.<Question>getController().getId(); // Replace YourControllerClass with the actual name of your controller class
    }


}
