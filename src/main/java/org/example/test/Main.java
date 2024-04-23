package org.example.test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
// for test in terminal
import org.example.models.User;
import org.example.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Main extends Application {

    public void start(Stage primaryStage) throws Exception{
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Admin/Publication.fxml"));

            primaryStage.setTitle("HelpMeBalance");
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true); // Ensures the stage is maximized

        Image logoImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/style/img/iconlogo.png")));
        // Set the logo image as the application icon
        primaryStage.getIcons().add(logoImage);
            primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}

// for test in terminal
/* class Main {
    public static void main(String[] args) {
        try {
            UserService uS= new UserService();
            User user= new User("test@hash.tn","password","firstname","lastname");
            uS.add(user);
            System.out.println(uS.selectWhere("test@hash.tn","password"));
            User user =uS.selectWhere(5);
            user.setRoles(Arrays.asList("psy"));
            user.setPassword("hehe not crypted again");
            uS.updatePassword(user);
            System.out.println(uS.selectWhere(5));
            uS.isBanned(5,20);
            System.out.println(uS.selectWhere(5));
            uS.unBanned(5);
            System.out.println(uS.selectWhere(5));
            System.out.println(uS.selectWhere(2));
            List<User> users = uS.select("email","desc",1,2);
            for (User user : users) {
                System.out.println(user.toString());
            }
            for(String role: Arrays.asList("ROLE_ADMIN","psy","ROLE_USER")) {
                System.out.println(" List Des "+role);
                users = uS.select(role);
                for (User user : users) {
                    System.out.println(user.toString());
                }
            }
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}*/
