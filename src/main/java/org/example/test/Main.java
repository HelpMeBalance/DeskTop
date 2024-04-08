package org.example.test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public void start(Stage primaryStage) throws Exception{
            Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("AppointmentCreation.fxml"));
            primaryStage.setTitle("HelpMeBalance");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
