package org.example.controllers;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcons;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import org.example.models.Publication;
import org.example.models.User;
import org.example.service.UserService;

import java.sql.SQLException;

public class AdminUsersController {

    @FXML
    private TableView<User> userTable;
    @FXML
    private TableColumn<User, String> colFirstName;
    @FXML
    private TableColumn<User, String> colLastName;
    @FXML
    private TableColumn<User, String> colEmail;
    @FXML
    private TableColumn<User, String> colRoles;
    @FXML
    private TableColumn<User, Boolean> colIs_banned;
    @FXML
    private TableColumn<User, String> colCreated_at;
    @FXML
    private TableColumn<User, Void> actions;
    public void initialize() {
        colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstname"));
        colLastName.setCellValueFactory(new PropertyValueFactory<>("lastname"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colRoles.setCellValueFactory(new PropertyValueFactory<>("roles")); // Make sure this property exists in User
        colIs_banned.setCellValueFactory(new PropertyValueFactory<>("is_banned"));
        colCreated_at.setCellValueFactory(new PropertyValueFactory<>("created_at"));
        setupActionsColumn();
        loadUsers();
    }

    private void setupActionsColumn() {
        actions.setCellFactory(param -> new TableCell<User, Void>() {
            private final HBox container = new HBox(10); // space between buttons
            private final Button viewButton = new Button();
            private final Button updateButton = new Button();
            private final Button deleteButton = new Button();

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
                    User user = getTableView().getItems().get(getIndex());
                    handleView(user);
                });
                updateButton.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    handleUpdate(user);
                });
                deleteButton.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    handleDelete(user);
                });

            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : container);
            }
        });
    }

    private void handleView(User user) {
        // Implement the view action
    }

    private void handleUpdate(User user) {
        // Implement the update action
    }

    private void handleDelete(User user) {
        // Implement the delete action
    }

    private void loadUsers() {
        UserService userService = new UserService();
        try {
            ObservableList<User> users = FXCollections.observableArrayList(userService.select());
            userTable.setItems(users);
        } catch (SQLException e) {
            e.printStackTrace(); // Proper error handling should be implemented
        }
    }

    public void action(ActionEvent actionEvent) {
    }
}
