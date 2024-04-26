package org.example.controllers;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcons;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import org.example.models.Like;
import org.example.models.Publication;
import org.example.service.*;
import org.example.utils.Navigation;
import org.example.utils.Session;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import static java.lang.String.valueOf;
public class BlogController implements Initializable {
    public Text pagetitle;
    public Text pagesubtitle;
    public VBox Publications;

    @FXML
    public Hyperlink hlFirst;
    @FXML
    public Pagination pagination;
    @FXML
    public Hyperlink hlLast;
    @FXML
    public Label paginationLegend;
    public Hyperlink hyperlinktitle;

    private static final String UPLOAD_ROOT="src/uploads/pub_pictures";
    private static final int PAGE_SIZE = 4;


    private PublicationService pS=new PublicationService();
    private CommentaireService cS=new CommentaireService();
    private CategorieService catS=new CategorieService();
    private LikeService lS=new LikeService();
    private SousCategorieService souscatS=new SousCategorieService();
    ObservableList<Publication> publicationsList= FXCollections.observableArrayList();

    private File selectedFile;
    private ImageView imageView;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            loadPublications(pagination.getCurrentPageIndex());
            DisplayPublication();

            pagination.currentPageIndexProperty().addListener((obs, oldPageIndex, newPageIndex) -> {
                try {
                    loadPublications(newPageIndex.intValue());
                    Publications.getChildren().clear();
                    DisplayPublication();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
            });
            hyperlinktitle.setOnAction(event -> {
                try {
                    Navigation.navigateTo("/fxml/home/homepage.fxml", hyperlinktitle);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
    private void loadPublications(int pageIndex) throws SQLException {
        List<Publication> publications=pS.select(pageIndex,PAGE_SIZE);
        int totalpub=pS.countPublicationsValidated();
        pagination.setPageCount((totalpub + PAGE_SIZE - 1) / PAGE_SIZE);
        publicationsList.setAll(publications);
    }
    private void DisplayPublication() throws MalformedURLException, SQLException {
        int numPublications = publicationsList.size();
        for (int i = 0; i < numPublications; i += 2) {  // Increment by 2 since we process two items at a time
            HBox hbox = new HBox(20);  // 20 is the spacing between elements in the HBox
            hbox.setAlignment(Pos.CENTER);  // Center alignment of the contents in the HBox
            Publication pub1 = publicationsList.get(i);
            StackPane stackPane1 = createStackPane(pub1);
            hbox.getChildren().add(stackPane1);
            if (i + 1 < numPublications) {
                Publication pub2 = publicationsList.get(i + 1);
                StackPane stackPane2 = createStackPane(pub2);
                hbox.getChildren().add(stackPane2);
            }
            Publications.getChildren().add(hbox);
        }

    }

    private StackPane createStackPane(Publication publication) throws MalformedURLException, SQLException {StackPane stackPane = new StackPane();
        stackPane.setPrefSize(400.0, 300.0); // Width and Height swapped to match FXML
        stackPane.getStyleClass().add("block01");
        String imagePath = UPLOAD_ROOT + "/" + publication.getImage();
        File file = new File(imagePath);
        String imageUrl = file.toURI().toURL().toExternalForm();
        stackPane.setStyle("-fx-background-image: url('" + imageUrl + "');");
        Pane backgroundPane = new Pane();
        backgroundPane.setPrefSize(200.0, 200.0); // Width and Height swapped to match FXML
        backgroundPane.setStyle("-fx-background-color: linear-gradient(to top, rgba(0,0,0,0.5), white);");
        backgroundPane.setBlendMode(BlendMode.MULTIPLY);

        VBox container = new VBox();
        container.setAlignment(Pos.BOTTOM_CENTER);
        container.setPrefSize(400.0, 76.0); // Width and Height swapped to match FXML
        container.setPadding(new Insets(10, 10, 10, 10));

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10.0);
        gridPane.setVgap(10.0);
        gridPane.getColumnConstraints().addAll(
                new ColumnConstraints(100, Control.USE_COMPUTED_SIZE, Double.MAX_VALUE, Priority.SOMETIMES, HPos.LEFT, true),
                new ColumnConstraints(100, Control.USE_COMPUTED_SIZE, Double.MAX_VALUE, Priority.SOMETIMES, HPos.RIGHT, true)
        );
        gridPane.getRowConstraints().addAll(
                new RowConstraints(30, Control.USE_COMPUTED_SIZE, Double.MAX_VALUE, Priority.SOMETIMES, VPos.CENTER, true),
                new RowConstraints(30, Control.USE_COMPUTED_SIZE, Double.MAX_VALUE, Priority.SOMETIMES, VPos.CENTER, true)
        );

        HBox hBox = new HBox(20);
        hBox.setAlignment(Pos.CENTER);
        Text dateText = new Text(publication.getDate_m().format(DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm:ss")));
        dateText.getStyleClass().add("text-bold");
        dateText.setStyle("-fx-fill: #d7e1ea;");
        FontAwesomeIcon eyeIcon = new FontAwesomeIcon();
        HBox viewsBox = new HBox(5);
        viewsBox.setAlignment(Pos.CENTER);
        eyeIcon.setIcon(FontAwesomeIcons.EYE);
        eyeIcon.setSize("16px"); // Set the size of the icon
        eyeIcon.setFill(Color.web("#a9cd71"));
        Text views = new Text(valueOf(publication.getVues()));
        views.setStyle("-fx-fill: #d7e1ea;");
        views.getStyleClass().add("text-bold");
        viewsBox.getChildren().addAll(views,eyeIcon);

        FontAwesomeIcon comIcon = new FontAwesomeIcon();
        HBox commentsBox = new HBox(5);
        commentsBox.setAlignment(Pos.CENTER);
        comIcon.setSize("16px"); // Set the size of the icon
        comIcon.setFill(Color.web("#a9cd71"));
        Text comments = new Text();
        comments.setStyle("-fx-fill: #d7e1ea;");
        comments.getStyleClass().add("text-bold");
        if(publication.getCom_ouvert())
        { int nbcom=cS.countComments(publication);
            if(nbcom>0)
            {
                comIcon.setIcon(FontAwesomeIcons.COMMENT);
                comments.setText(valueOf(nbcom));
                commentsBox.getChildren().addAll(comments,comIcon);
            }else
            {
                comIcon.setIcon(FontAwesomeIcons.EXCLAMATION_CIRCLE);
                comments.setText("Comments");
                commentsBox.getChildren().addAll(comIcon,comments);
            }
        }else
        {
            comIcon.setIcon(FontAwesomeIcons.LOCK);
            comments.setText("Comments");
            commentsBox.getChildren().addAll(comIcon,comments);

        }
        hBox.getChildren().addAll(dateText, viewsBox, commentsBox);
        gridPane.add(hBox, 0, 0, 1, 1);
        HBox hBox1 = new HBox(5);
        hBox1.setAlignment(Pos.CENTER_LEFT);
        TextFlow detailsTextFlow = new TextFlow();
        detailsTextFlow.getStyleClass().add("h4");
        Text detailsText = new Text(publication.getTitre());
        detailsText.setFill(Color.WHITE);
        detailsText.setStyle("-fx-fill: white;"); // Ensuring text color is white
        detailsTextFlow.getChildren().add(detailsText);
        if(Session.getInstance().getUser() != null)
        {
            if(publication.getUser().getId()== Session.getInstance().getUser().getId())
        {
            final Button updateButton = new Button();
            final Button deleteButton = new Button();
            FontAwesomeIcon updateIcon = new FontAwesomeIcon();
            updateIcon.setIcon(FontAwesomeIcons.PENCIL);
            updateIcon.setSize("16px"); // Set the size of the icon
            updateIcon.setFill(Color.web("#a9cd71"));
            updateButton.setGraphic(updateIcon);
            FontAwesomeIcon deleteIcon = new FontAwesomeIcon();
            deleteIcon.setIcon(FontAwesomeIcons.TRASH);
            deleteIcon.setSize("16px"); // Set the size of the icon
            deleteIcon.setFill(Color.web("#a9cd71"));
            deleteButton.setGraphic(deleteIcon);
            updateButton.setStyle("-fx-background-color: transparent;");
            deleteButton.setStyle("-fx-background-color: transparent;");
            updateButton.setOnAction(event -> {
                showUpdatePublicationDialog(publication);
            });
            deleteButton.setOnAction(event -> {
                try {
                    showDeleteConfirmationDialog(publication);
                } catch (SQLException | MalformedURLException e) {
                    throw new RuntimeException(e);
                }
            });
            hBox1.getChildren().addAll(detailsTextFlow,deleteButton,updateButton);

        }
            else
        {
            final Button likeButton = new Button();
            FontAwesomeIcon likeIcon = new FontAwesomeIcon();
            if(lS.selectWhere(Session.getInstance().getUser().getId(), publication.getId())==null)
            {
                likeIcon.setIcon(FontAwesomeIcons.HEART);
                likeButton.setOnAction(event -> {
                    try {

                        lS.add(new Like(Session.getInstance().getUser(), publication));
                        Publications.getChildren().clear();
                        DisplayPublication();
                    } catch (SQLException | MalformedURLException e) {
                        throw new RuntimeException(e);}
                });

            }else
            {
                likeIcon.setIcon(FontAwesomeIcons.HEART_ALT);
                likeButton.setOnAction(event -> {
                    try {
                        lS.delete(lS.selectWhere(Session.getInstance().getUser().getId(), publication.getId()).getId());
                        Publications.getChildren().clear();
                        DisplayPublication();
                    } catch (SQLException | MalformedURLException e) {
                        throw new RuntimeException(e);}
                });
            }
            likeIcon.setSize("16px"); // Set the size of the icon
            likeIcon.setFill(Color.web("#a9cd71"));
            likeButton.setGraphic(likeIcon);
            likeButton.setStyle("-fx-background-color: transparent;");
            hBox1.getChildren().addAll(detailsTextFlow,likeButton);

        }} else         hBox1.getChildren().add(detailsTextFlow);

        gridPane.add(hBox1, 0, 1, GridPane.REMAINING, 1);

        Button button = getButton(publication);
        HBox buttonContainer = new HBox(button); // Place the button in an HBox for alignment
        buttonContainer.setAlignment(Pos.CENTER_LEFT); // Align the button container to the left
        buttonContainer.setSpacing(10); // Add spacing between button and gridPane
        button.setOnAction(event -> {
            pagetitle.setText(publication.getCategorie().getNom());
            pagesubtitle.setText(publication.getCategorie().getNom());
            hyperlinktitle.setText("Brave Chats");
            hyperlinktitle.setOnAction(Event -> {
                try {
                    pagination.setCurrentPageIndex(0);
                    loadPublications(pagination.getCurrentPageIndex());
                    Publications.getChildren().clear();
                    DisplayPublication();
                } catch (SQLException | MalformedURLException e) {
                    throw new RuntimeException(e);
                }
                pagetitle.setText("Brave Chats");
                pagesubtitle.setText("Brave Chats");
                hyperlinktitle.setText("Home");
                hyperlinktitle.setOnAction(eventt -> {
                    try {
                        Navigation.navigateTo("/fxml/home/homepage.fxml", hyperlinktitle);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            });
            try {

                pagination.setCurrentPageIndex(0);
                publicationsList.setAll(pS.select(publication.getCategorie(),pagination.getCurrentPageIndex(),PAGE_SIZE));
                pagination.setPageCount((publicationsList.size() + PAGE_SIZE - 1) / PAGE_SIZE);
                Publications.getChildren().clear();
                DisplayPublication();
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        });

        container.getChildren().addAll(buttonContainer, gridPane); // Add button container and gridPane to the container
        stackPane.getChildren().addAll(backgroundPane, container); // Add backgroundPane and container to the stackPane

        return stackPane;

    }

    private static Button getButton(Publication publication) {
        Button button = new Button(publication.getCategorie().getNom()); // Set button text
        button.setStyle("-fx-background-radius: 15px; -fx-background-color: #a9cd71; -fx-background-insets: 0, 1, 2;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-radius: 15px; -fx-background-color: #b4c5d3; -fx-background-insets: 0, 1, 2;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-radius: 15px; -fx-background-color: #a9cd71; -fx-background-insets: 0, 1, 2;"));

        button.setMaxHeight(Double.MAX_VALUE); // Set max height to fill the container vertically
        button.setAlignment(Pos.CENTER_LEFT); // Align the button text to the left
        return button;
    }

    public void handleFirstPage() {
        pagination.setCurrentPageIndex(0);
    }
    public void handleLastPage() {
        pagination.setCurrentPageIndex(pagination.getPageCount() - 1);
    }
    private void showDeleteConfirmationDialog(Publication publication) throws SQLException, MalformedURLException {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Are you sure you want to delete the publication ?");
        alert.setContentText("Publication Title: " + publication.getTitre() + "\n Posted the : "+publication.getDate_m().format(DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm:ss")));
        ButtonType confirmButton = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(confirmButton, cancelButton);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == confirmButton) {
            pS.delete(publication.getId());
            pagination.setPageCount((pS.countPublications() + PAGE_SIZE - 1) / PAGE_SIZE);
            pagination.setCurrentPageIndex(0);
            publicationsList.remove(publication);
            Publications.getChildren().clear();
            DisplayPublication();
        }
    }
    private void showUpdatePublicationDialog(Publication publication) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("HelpMeBalance");
        Label titleLabel = new Label("Edit Publication");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-alignment: center;");
        imageView = new ImageView();
        try {
            String imagePath = UPLOAD_ROOT +"/"+ publication.getImage();
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                try (InputStream inputStream = new FileInputStream(imageFile)) {
                    Image image = new Image(inputStream);
                    imageView.setImage(image);
                    imageView.setImage(image);
                    double imageWidth = image.getWidth();
                    double imageHeight = image.getHeight();
                    double proportionalWidth = (200.0 / imageHeight) * imageWidth;
                    imageView.setFitWidth(proportionalWidth);
                    imageView.setFitHeight(200);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.err.println("Image file does not exist: " + imagePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Button UpdatePictureButton = new Button("Update Picture");
        Label titlepublicationLabel = new Label("Title");
        Label contentLabel = new Label("Publication");
        TextField title = new TextField(publication.getTitre());
        TextArea content = new TextArea(publication.getContenu());
        content.setWrapText(true);
        CheckBox allowcoms = new CheckBox("Allow Comments");
        allowcoms.setSelected(publication.getCom_ouvert());
        CheckBox anonyme = new CheckBox("Post as Anonyme");
        anonyme.setSelected(publication.getAnonyme());
        anonyme.setDisable(true);
        VBox titleImageBox = new VBox();
        titleImageBox.getChildren().addAll(titleLabel, imageView);
        titleImageBox.setAlignment(Pos.CENTER);
        titleImageBox.setSpacing(10);
        VBox contentBox = new VBox();
        contentBox.getChildren().addAll(titleImageBox,UpdatePictureButton,titlepublicationLabel,title,contentLabel,content,allowcoms,anonyme);
        contentBox.setSpacing(10);
        contentBox.setAlignment(Pos.CENTER_LEFT);
        Button updateButton = new Button("Update Publication");
        HBox buttonBox = new HBox();
        buttonBox.getChildren().add(updateButton);
        buttonBox.setAlignment(Pos.CENTER);
        UpdatePictureButton.setOnAction(event -> {
            selectImageFile();
        });
        updateButton.setOnAction(event -> {
            try {
                publication.setTitre(title.getText());
                publication.setContenu(content.getText());
                publication.setCom_ouvert(allowcoms.isSelected());
                if(selectedFile != null) publication.setImage(saveImageFile(selectedFile,UPLOAD_ROOT));
                pS.update(publication);
                publicationsList.replaceAll(pub -> {
                    if (pub.getId() == publication.getId()) {
                        pub=publication;
                    }
                    return pub;
                });
                Publications.getChildren().clear();
                DisplayPublication();
                dialog.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }

        });
        dialog.getDialogPane().setContent(new VBox(contentBox, buttonBox));
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);
        dialog.getDialogPane().lookupButton(ButtonType.CLOSE).setVisible(false);
        dialog.showAndWait();
    }
    private void selectImageFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image File");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Image files (*.jpg, *.jpeg, *.png)", "*.jpg", "*.jpeg", "*.png");
        fileChooser.getExtensionFilters().add(extFilter);
        selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            displaySelectedImage();
        }
    }
    private void displaySelectedImage() {
        if (selectedFile != null) {
            Image image = new Image(selectedFile.toURI().toString());
            try {
                imageView.setImage(image);
                double imageWidth = image.getWidth();
                double imageHeight = image.getHeight();
                double proportionalWidth = (200.0 / imageHeight) * imageWidth;
                imageView.setFitWidth(proportionalWidth);
                imageView.setFitHeight(200);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public String saveImageFile(File selectedFile, String targetDirectory) {
        String fileName = null;
        if (selectedFile != null && selectedFile.exists()) {
            try {
                String originalFileName = selectedFile.getName();
                String uniqueFileName = System.currentTimeMillis() + "_" + originalFileName;
                Path targetDirPath = Path.of(targetDirectory);
                if (!Files.exists(targetDirPath)) {
                    Files.createDirectories(targetDirPath);
                }
                Path targetFilePath = targetDirPath.resolve(uniqueFileName);
                Files.copy(selectedFile.toPath(), targetFilePath, StandardCopyOption.REPLACE_EXISTING);
                fileName = uniqueFileName;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fileName;
    }
}
