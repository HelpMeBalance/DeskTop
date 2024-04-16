package org.example.test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.models.Question;
import org.example.service.QuestionService;

import java.sql.SQLException;

public class Main extends Application {

    public void start(Stage primaryStage) throws Exception{
            Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("Quiz/quiz.fxml"));
            primaryStage.setTitle("HelpMeBalance");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
    }

    public static void main(String[] args) throws SQLException {
        QuestionService q=new QuestionService();
        Question q1 = new QuestionService().selectWhere(12);
        q1.setQuestion("suuuu");
        q.update(q1);
        launch(args);
    }

}
