package org.example.controllers;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcons;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.models.User;
import org.example.service.UserService;
import javafx.scene.control.Pagination;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Optional;

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
    @FXML
    private TextField searchField;
    @FXML
    private Pagination pagination;

    private FilteredList<User> filteredData;
    private static final int ROWS_PER_PAGE = 4;

    public void initialize() {
        colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstname"));
        colLastName.setCellValueFactory(new PropertyValueFactory<>("lastname"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colRoles.setCellValueFactory(new PropertyValueFactory<>("roles")); // Make sure this property exists in User
        colIs_banned.setCellValueFactory(new PropertyValueFactory<>("is_banned"));
        colCreated_at.setCellValueFactory(new PropertyValueFactory<>("created_at"));
        setupActionsColumn();
        loadUsers();
        setupTable();
        setupSearchFilter();
    }

    private void setupTable() {
        loadUsers(); // Now call loadUsers() after pagination initialization
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
                container.getChildren().addAll(viewButton, updateButton, deleteButton);

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
        Alert infoDialog = new Alert(Alert.AlertType.INFORMATION);
        infoDialog.setTitle("User Details");
        infoDialog.setHeaderText(null);
        infoDialog.getDialogPane().setPrefSize(480, 320);

        VBox content = new VBox(10);
        content.setAlignment(Pos.CENTER_LEFT);
        content.setPadding(new Insets(10));

        String userDetails = String.format(
                "First Name: %s\n" +
                        "Last Name: %s\n" +
                        "Email: %s\n" +
                        "Roles: %s\n" +
                        "Is Banned: %s\n" +
                        "Created At: %s",
                user.getFirstname(), user.getLastname(), user.getEmail(),
                String.join(", ", user.getRoles()), user.getIs_banned() ? "Yes" : "No",
                user.getCreated_at().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );

        Label detailsLabel = new Label(userDetails);
        detailsLabel.setWrapText(true);
        content.getChildren().add(detailsLabel);

        infoDialog.getDialogPane().setContent(content);
        infoDialog.showAndWait();
    }

    private void handleUpdate(User user) {
        Dialog<ButtonType> updateDialog = new Dialog<>();
        updateDialog.setTitle("Update User");
        updateDialog.setHeaderText("Edit the details and confirm:");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        updateDialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField firstNameField = new TextField(user.getFirstname());
        TextField lastNameField = new TextField(user.getLastname());
        TextField emailField = new TextField(user.getEmail());
        ComboBox<String> roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll("ROLE_USER", "ROLE_ADMIN", "psy");
        roleComboBox.setValue(user.getRoles().stream().findFirst().orElse("ROLE_USER"));

        CheckBox banCheckBox = new CheckBox("Ban User");
        banCheckBox.setSelected(user.getIs_banned());
        TextField banDurationField = new TextField();
        banDurationField.setPromptText("Duration in days");
        banDurationField.setDisable(!banCheckBox.isSelected());

        banCheckBox.setOnAction(event -> banDurationField.setDisable(!banCheckBox.isSelected()));

        grid.add(new Label("First Name:"), 0, 0);
        grid.add(firstNameField, 1, 0);
        grid.add(new Label("Last Name:"), 0, 1);
        grid.add(lastNameField, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(emailField, 1, 2);
        grid.add(new Label("Role:"), 0, 3);
        grid.add(roleComboBox, 1, 3);
        grid.add(banCheckBox, 0, 4);
        grid.add(banDurationField, 1, 4);

        updateDialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = updateDialog.showAndWait();
        if (result.isPresent() && result.get() == saveButtonType) {
            user.setFirstname(firstNameField.getText());
            user.setLastname(lastNameField.getText());
            user.setEmail(emailField.getText());
            user.setRoles(Collections.singletonList(roleComboBox.getValue()));
            user.setIs_banned(banCheckBox.isSelected());

            // Calculate the expiration date based on the duration
            if (banCheckBox.isSelected() && !banDurationField.getText().isEmpty()) {
                int durationDays = Integer.parseInt(banDurationField.getText());
                LocalDateTime expirationDate = LocalDateTime.now().plusDays(durationDays);
                user.setBan_expires_at(expirationDate);
            } else {
                user.setBan_expires_at(null); // Remove the ban expiration if not banned
            }

            try {
                UserService userService = new UserService();
                userService.update(user);
                loadUsers(); // Refresh the user table
            } catch (SQLException e) {
                e.printStackTrace(); // Proper error handling should be implemented
            }
        }
    }



    private void handleDelete(User user) {
        // Confirm deletion and remove the user
        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this user?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = confirmationDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            try {
                new UserService().delete(user.getId());
                userTable.getItems().remove(user);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadUsers() {
        UserService userService = new UserService();
        try {
            ObservableList<User> users = FXCollections.observableArrayList(userService.select());
            filteredData = new FilteredList<>(users, p -> true);
            SortedList<User> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(userTable.comparatorProperty());
            userTable.setItems(sortedData);
            updatePagination();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Node createPage(int pageIndex) {
        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, filteredData.size());
        userTable.setItems(FXCollections.observableArrayList(filteredData.subList(fromIndex, toIndex)));
        return new VBox(userTable); // Make sure this returns a Node
    }

    private void setupSearchFilter() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(user -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return user.getFirstname().toLowerCase().contains(lowerCaseFilter)
                        || user.getLastname().toLowerCase().contains(lowerCaseFilter)
                        || user.getEmail().toLowerCase().contains(lowerCaseFilter);
            });
            updatePagination();

        });
    }
    private void updatePagination() {
        int totalPageCount = (int) Math.ceil(filteredData.size() / (double) ROWS_PER_PAGE);
        pagination.setPageCount(totalPageCount);
        pagination.setCurrentPageIndex(0); // Reset to first page
        pagination.setPageFactory(this::createPage);
    }


    public void action(ActionEvent actionEvent) {
    }
}
