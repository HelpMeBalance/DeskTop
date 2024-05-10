package org.example.controllers;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcons;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import org.example.models.*;
import org.example.service.*;
import org.example.utils.Navigation;
import org.example.utils.Session;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
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
    public Hyperlink hyperlinktitle;

    private static final String UPLOAD_ROOT="src/uploads/pub_pictures";
    private static final int PAGE_SIZE = 4;
    public Button addpublication;
    public VBox Categories;
    public VBox exploremore;
    public VBox relatedtopics;
    public HBox paginatorhbox;
    public VBox viewPublication;


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
            initCategories("");
            initExploreMore();
            initRelatedTopics("");
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
            addpublication.setOnAction(event -> {
                if(Session.getInstance().getUser()==null)
                {try {
                    Navigation.navigateTo("/fxml/Auth/Login.fxml", addpublication);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }}
                else addPublication();
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
        leaveviewpublication();
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
        hBox.setPadding(new Insets(5)); // Add padding around the content
        hBox.getChildren().addAll(dateText, viewsBox, commentsBox);
        //hBox.setStyle("-fx-background-color: rgba(211, 211, 211, 0.5); -fx-background-radius: 5px;");
        gridPane.add(hBox, 0, 0, 1, 1);
        HBox hBox1 = new HBox(5);
        hBox1.setAlignment(Pos.CENTER_LEFT);
        TextFlow detailsTextFlow = new TextFlow();
        detailsTextFlow.getStyleClass().add("h4");
        Text detailsText = new Text(publication.getTitre());
        detailsText.setStyle("-fx-fill: white;"); // Ensuring text color is white
        Hyperlink viewMoreLink = new Hyperlink();
        viewMoreLink.getStyleClass().add("h4");
        FontAwesomeIcon arrowIcon = new FontAwesomeIcon();
        arrowIcon.setIcon(FontAwesomeIcons.ARROW_LEFT);
        arrowIcon.setSize("16px"); // Set the size of the icon
        arrowIcon.setFill(Color.web("#a9cd71"));
        viewMoreLink.setGraphic(arrowIcon);
        String defaultStyle = "-fx-text-fill: #a9cd71;";
        String hoverStyle = "-fx-text-fill: #ffffff;";
        viewMoreLink.setStyle(defaultStyle); // Set default style
        viewMoreLink.setOnMouseEntered(e -> viewMoreLink.setStyle(hoverStyle)); // Set hover style
        viewMoreLink.setOnMouseExited(e -> viewMoreLink.setStyle(defaultStyle)); // Reset to default style on exit
        viewMoreLink.setOnAction(event -> {
            try {
                viewPublication(publication);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            try {
                pS.addView(publication.getId());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        detailsTextFlow.getChildren().addAll(detailsText,viewMoreLink);
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
            try {
                displaypubundercat(publication.getCategorie().getNom());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        });
        String contenu = publication.getContenu();
        String pubTextString = contenu.length() > 40 ? contenu.substring(0, 40) + "..." : contenu;
        Text pubText = new Text(pubTextString);
        pubText.getStyleClass().add("h5");
        pubText.setStyle("-fx-fill: white;"); // Ensuring text color is white
        VBox pubContainer = new VBox(pubText); // Place the button in an HBox for alignment
        pubContainer.setAlignment(Pos.CENTER_LEFT); // Align the button container to the left
        pubContainer.setSpacing(10); // Add spacing between button and gridPane

        container.getChildren().addAll(buttonContainer, gridPane,pubContainer); // Add button container and gridPane to the container
        stackPane.getChildren().addAll(backgroundPane, container); // Add backgroundPane and container to the stackPane

        return stackPane;

    }
    private HBox createPublicationBox(Publication publication) throws SQLException {
        HBox hBox = new HBox(20);
        hBox.setAlignment(Pos.CENTER_LEFT);

        // Load and set image
        ImageView imageView = new ImageView();
        try {
            String imagePath = UPLOAD_ROOT + "/" + publication.getImage();
            File file = new File(imagePath);
            String imageUrl = file.toURI().toURL().toExternalForm();
            Image image = new Image(imageUrl);
            imageView.setImage(image);
            imageView.setFitHeight(30.0);
            imageView.setPreserveRatio(true);
            hBox.getChildren().add(imageView);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        // VBox for text details
        VBox textDetails = new VBox(5);

        // Title
        Text titleText = new Text(publication.getTitre());
        titleText.getStyleClass().add("text-bold");
        titleText.setStyle("-fx-fill: grey;");

        // Date
        Text dateText = new Text(publication.getDate_m().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")));
        dateText.getStyleClass().add("text-bold");
        dateText.setStyle("-fx-fill: #a9cd71;");

        // Views and comments
        FontAwesomeIcon eyeIcon = new FontAwesomeIcon();
        eyeIcon.setIcon(FontAwesomeIcons.EYE);
        eyeIcon.setSize("16px"); // Set the size of the icon
        eyeIcon.setFill(Color.web("#a9cd71"));

        Text views = new Text(" | "+ publication.getVues());
        views.setStyle("-fx-fill: grey;");
        views.getStyleClass().add("text-bold");

        FontAwesomeIcon comIcon = new FontAwesomeIcon();
        comIcon.setSize("16px"); // Set the size of the icon
        comIcon.setFill(Color.web("#a9cd71"));

        Text comments = new Text();
        comments.setStyle("-fx-fill: grey;");
        comments.getStyleClass().add("text-bold");

        if(publication.getCom_ouvert()) {
            int nbcom = cS.countComments(publication);
            if(nbcom > 0) {
                comIcon.setIcon(FontAwesomeIcons.COMMENT);
                comments.setText(" | "+ nbcom);
            } else {
                comIcon.setIcon(FontAwesomeIcons.EXCLAMATION_CIRCLE);
                comments.setText(" | Comments");
            }
        } else {
            comIcon.setIcon(FontAwesomeIcons.LOCK);
            comments.setText(" | Comments");
        }
        // Add all text elements to the text VBox
        textDetails.getChildren().addAll(titleText, new HBox(dateText, new HBox(5, views, eyeIcon), new HBox(5, comments, comIcon)));

        // Add the text VBox to the HBox
        hBox.getChildren().add(textDetails);


        return hBox;
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
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Image files ( *.png)", "*.png");
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

    public void displaypubundercat(String nom) throws SQLException {
        initRelatedTopics("");
        pagetitle.setText(nom);
        pagesubtitle.setText(nom);
        hyperlinktitle.setText("Brave Chats");
        hyperlinktitle.setOnAction(Event -> {
            try {
                pagination.setCurrentPageIndex(0);
                loadPublications(pagination.getCurrentPageIndex());
                Publications.getChildren().clear();
                DisplayPublication();
                initCategories("");
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
            initCategories(nom);
            pagination.setCurrentPageIndex(0);
            publicationsList.setAll(pS.select(catS.selectWhere(nom),pagination.getCurrentPageIndex(),PAGE_SIZE));
            pagination.setPageCount((publicationsList.size() + PAGE_SIZE - 1) / PAGE_SIZE);
            Publications.getChildren().clear();
            DisplayPublication();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void displaypubundersubcat(String nom) {
        pagetitle.setText(nom);
        pagesubtitle.setText(nom);
        hyperlinktitle.setText("Brave Chats");
        hyperlinktitle.setOnAction(Event -> {
            try {
                pagination.setCurrentPageIndex(0);
                loadPublications(pagination.getCurrentPageIndex());
                Publications.getChildren().clear();
                DisplayPublication();
                initCategories("");
                initRelatedTopics("");
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
            initRelatedTopics(nom);
            pagination.setCurrentPageIndex(0);
            publicationsList.setAll(pS.select(souscatS.selectWhere(nom),pagination.getCurrentPageIndex(),PAGE_SIZE));
            pagination.setPageCount((publicationsList.size() + PAGE_SIZE - 1) / PAGE_SIZE);
            Publications.getChildren().clear();
            DisplayPublication();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addPublication()  {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("HelpMeBalance");
        Label titleLabel = new Label("Add Publication");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-alignment: center;");
        imageView = new ImageView();
        Button AddPictureButton = new Button("Add Picture");
        Label errorMessage=new Label();
        errorMessage.setStyle("-fx-fill: red;"); // Ensuring text color is white

        Label titlepublicationLabel = new Label("Title");
        Label contentLabel = new Label("Publication");
        TextField title = new TextField();
        TextArea content = new TextArea();
        CheckBox allowcoms = new CheckBox("Allow Comments");
        VBox titleImageBox = new VBox();
        titleImageBox.getChildren().addAll(titleLabel,imageView);
        titleImageBox.setAlignment(Pos.CENTER);
        titleImageBox.setSpacing(10);
        ComboBox<String> categoryComboBox = new ComboBox<>();
        ComboBox<String> subcategoryComboBox = new ComboBox<>();
        try {
            List<Categorie> categories = catS.selectCategoriesWithSubcategories();
            for (Categorie categorie : categories) {
                categoryComboBox.getItems().add(categorie.getNom());
            }
            categoryComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    if (newValue != null && categories != null) {
                        try {
                            Categorie selectedCategory = categories.stream()
                                    .filter(c -> c.getNom().equals(newValue))
                                    .findFirst()
                                    .orElse(null);
                            if (selectedCategory != null) {
                                List<SousCategorie> subcategories = souscatS.select(selectedCategory);
                                subcategoryComboBox.getItems().clear();
                                if (subcategories != null) {
                                    for (SousCategorie sousCategorie : subcategories) {
                                        subcategoryComboBox.getItems().add(sousCategorie.getNom());
                                    }
                                }
                                if (!subcategories.isEmpty()) {
                                    subcategoryComboBox.getSelectionModel().selectFirst();
                                }
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            if (!categories.isEmpty()) {
                categoryComboBox.getSelectionModel().selectFirst();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        VBox contentBox = new VBox();
        contentBox.getChildren().addAll(titleImageBox,AddPictureButton,errorMessage,titlepublicationLabel,title,contentLabel,content,allowcoms,categoryComboBox,subcategoryComboBox);
        contentBox.setSpacing(10);
        contentBox.setAlignment(Pos.CENTER_LEFT);
        Button addButton = new Button("Add Publication");
        HBox buttonBox = new HBox();
        buttonBox.getChildren().add(addButton);
        buttonBox.setAlignment(Pos.CENTER);
        AddPictureButton.setOnAction(event -> {
            selectImageFile();
        });
        addButton.setOnAction(event -> {
            boolean isValid = true;
            errorMessage.setText("");
            StringBuilder errors = new StringBuilder();

            if (title.getText().trim().isEmpty()) {
                errors.append("Title must be filled.\n");
                isValid = false;
            }
            else if (content.getText().trim().isEmpty()) {
                errors.append("Content must be filled.\n");
                isValid = false;
            }
            else  if (title.getText().length() < 5) {
                errors.append("Title must be at least 5 characters long.\n");
                isValid = false;
            }
            else  if (content.getText().length() < 10) {
                errors.append("Content must be at least 10 characters long.\n");
                isValid = false;
            }
            errorMessage.setText(errors.toString());
            if(isValid)
            {try {
                String fileName = "default.png";
                if(selectedFile != null) fileName = saveImageFile(selectedFile,UPLOAD_ROOT);
                Publication publication=new Publication(Session.getInstance().getUser(),catS.selectWhere(categoryComboBox.getValue()),souscatS.selectWhere(subcategoryComboBox.getValue()),title.getText(),content.getText(),allowcoms.isSelected(),false,fileName);
                pS.add(publication);
                Publications.getChildren().clear();
                DisplayPublication();
                dialog.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            }
        });
        dialog.getDialogPane().setContent(new VBox(contentBox, buttonBox));
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);
        dialog.getDialogPane().lookupButton(ButtonType.CLOSE).setVisible(false);
        dialog.showAndWait();
    }

    public void initCategories(String cat) throws SQLException {
       Categories.getChildren().clear();
        List<Categorie> categories=catS.selectCategoriesWithSubcategories();
        for (Categorie categorie : categories) {
            int nbsubcat=pS.countPublicationsValidatedUnderCat(categorie);
            Button catButton = new Button(nbsubcat == 0 ? categorie.getNom() + "     New" : categorie.getNom() + "   " + nbsubcat);
            if(Objects.equals(cat, categorie.getNom()))catButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;  -fx-text-fill: #a9cd71;");
            else {catButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-text-fill: grey;");
            catButton.setOnMouseEntered(e -> catButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;  -fx-text-fill: #a9cd71;"));
            catButton.setOnMouseExited(e -> catButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;-fx-text-fill: grey; "));}
            catButton.setOnAction(event -> {
                try {
                    displaypubundercat(categorie.getNom());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            Categories.getChildren().add(catButton);
        }
    }
    public  void initExploreMore() throws SQLException {
        List<Publication> pubs=pS.select();
        int numPubs = Math.min(pubs.size(), 3); // Get the minimum between the number of pubs and 3
        for(int i=0;i<numPubs;i++)
        {
            exploremore.getChildren().add(createPublicationBox(pubs.get(i)));
        }
    }
    private void initRelatedTopics(String souscat) throws SQLException {
        relatedtopics.getChildren().clear();
        List<SousCategorie> subcategories=souscatS.select();
        // Iterate over subcategories
        for (int i = 0; i < subcategories.size(); i += 3) {
            HBox row = new HBox(10); // Create a row for buttons with spacing between them
            row.setAlignment(Pos.CENTER_LEFT); // Align buttons to the left

            // Add buttons for each subcategory in the row
            for (int j = i; j < Math.min(i + 3, subcategories.size()); j++) {
                Button button = new Button(subcategories.get(j).getNom()); // Set button text
                if(Objects.equals(souscat, subcategories.get(j).getNom()))button.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;  -fx-text-fill: #a9cd71;");
                else
                {
                    button.setStyle("-fx-background-radius: 15px; -fx-background-color: transparent; -fx-border-color: #a9cd71; -fx-border-width: 2; -fx-border-radius: 15px; -fx-text-fill: grey;");
                    button.setOnMouseEntered(e -> button.setStyle("-fx-background-radius: 15px; -fx-background-color: #a9cd71; -fx-border-color: #a9cd71; -fx-border-width: 2; -fx-border-radius: 15px;"));
                    button.setOnMouseExited(e -> button.setStyle("-fx-background-radius: 15px; -fx-background-color: transparent; -fx-border-color: #a9cd71; -fx-border-width: 2; -fx-border-radius: 15px; -fx-text-fill: grey;"));
                }
                button.setMaxHeight(Double.MAX_VALUE); // Set max height to fill the container vertically
                button.setAlignment(Pos.CENTER_LEFT); // Align the button text to the left
                int finalJ = j;
                button.setOnAction(event -> {
                    try {
                        initCategories(subcategories.get(finalJ).getCategorie().getNom());
                        displaypubundersubcat(button.getText());
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });

                    row.getChildren().add(button); // Add button to the row
            }
            relatedtopics.getChildren().add(row); // Add row of buttons to the VBox
        }

    }
    private void viewPublication(Publication publication) throws SQLException {
        viewPublication.setVisible(true);
        Publications.setVisible(false);
        paginatorhbox.setVisible(false);

        initRelatedTopics(publication.getSous_categorie().getNom());
        pagetitle.setText(publication.getSous_categorie().getNom());
        pagesubtitle.setText(publication.getSous_categorie().getNom());
        hyperlinktitle.setText(publication.getCategorie().getNom());
        hyperlinktitle.setOnAction(Event -> {
            try {
                initCategories(publication.getCategorie().getNom());
                displaypubundercat(publication.getCategorie().getNom());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        try {
            initCategories(publication.getCategorie().getNom());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        VBox publicationDetails = new VBox(10);
        publicationDetails.setAlignment(Pos.CENTER);
        publicationDetails.setPadding(new Insets(10));

        // Display the publication image
        ImageView imageView = new ImageView();
        String imagePath = UPLOAD_ROOT + "/" + publication.getImage();
        File file = new File(imagePath);
        String imageUrl = null;
        try {
            imageUrl = file.toURI().toURL().toExternalForm();
            Image image = new Image(imageUrl);
            imageView.setImage(image);
            double imageWidth = image.getWidth();
            double imageHeight = image.getHeight();
            double proportionalWidth = (350.0 / imageHeight) * imageWidth;
            imageView.setFitWidth(proportionalWidth);
            imageView.setFitHeight(350);
            publicationDetails.getChildren().add(imageView);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        String roleLabel;
        if (publication.getUser().getRoles().contains("ROLE_ADMIN")) {
            roleLabel = "Administration";
        }
        else if (publication.getUser().getRoles().contains("ROLE_PSY")) {
            roleLabel = "Psychologist";
        } else {
            roleLabel = "Patient";
        }
        // Date
        Text dateText = new Text(publication.getDate_m().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")));
        dateText.getStyleClass().add("text-bold");
        dateText.setStyle("-fx-fill: #a9cd71;");

        FontAwesomeIcon calendarIcon = new FontAwesomeIcon();
        calendarIcon.setIcon(FontAwesomeIcons.CALENDAR);
        calendarIcon.setSize("16px"); // Set the size of the icon
        calendarIcon.setFill(Color.web("#a9cd71"));

        FontAwesomeIcon eyeIcon = new FontAwesomeIcon();
        eyeIcon.setIcon(FontAwesomeIcons.EYE);
        eyeIcon.setSize("16px"); // Set the size of the icon
        eyeIcon.setFill(Color.web("#a9cd71"));

        Text views = new Text(" | "+ publication.getVues());
        views.setStyle("-fx-fill: grey;");
        views.getStyleClass().add("text-bold");

        FontAwesomeIcon comIcon = new FontAwesomeIcon();
        comIcon.setSize("16px"); // Set the size of the icon
        comIcon.setFill(Color.web("#a9cd71"));

        Text comments = new Text();
        comments.setStyle("-fx-fill: grey;");
        comments.getStyleClass().add("text-bold");

        if(publication.getCom_ouvert()) {
            int nbcom = cS.countComments(publication);
            if(nbcom > 0) {
                comIcon.setIcon(FontAwesomeIcons.COMMENT);
                comments.setText(" | "+ nbcom);
            } else {
                comIcon.setIcon(FontAwesomeIcons.EXCLAMATION_CIRCLE);
                comments.setText(" | Comments");
            }
        } else {
            comIcon.setIcon(FontAwesomeIcons.LOCK);
            comments.setText(" | Comments");
        }
        Label authorLabel = new Label(publication.getUser().getLastname() +" "+publication.getUser().getFirstname() + " | " + roleLabel + " | ");
        HBox publicationDetailsHBox = new HBox(authorLabel, new HBox(5, dateText, calendarIcon), new HBox(5, views, eyeIcon), new HBox(5, comments, comIcon));
        publicationDetailsHBox.setAlignment(Pos.CENTER);
        publicationDetails.getChildren().add(publicationDetailsHBox);
        Label titleLabel = new Label(publication.getTitre());
        titleLabel.getStyleClass().add("text-bold"); // Apply CSS style for bold text
        titleLabel.setStyle("-fx-text-fill: #a9cd71;"); // Set text color to green

// Create the pen icon
        FontAwesomeIcon penIcon = new FontAwesomeIcon();
        penIcon.setIcon(FontAwesomeIcons.PENCIL);
        penIcon.setSize("16px"); // Set the size of the icon
        penIcon.setFill(Color.web("#a9cd71")); // Set icon color to green

// Create an HBox to contain the title label and the pen icon
        HBox titleBox = new HBox(titleLabel, penIcon);
        titleBox.setAlignment(Pos.CENTER); // Align items to the left
        titleBox.setSpacing(5); // Set spacing between the title label and the pen icon

// Add the title HBox to the publicationDetails VBox
        publicationDetails.getChildren().add(titleBox);

        // Display content
        Text contentText = new Text(publication.getContenu());
        contentText.setWrappingWidth(400); // Set the wrapping width to limit the text width
        publicationDetails.getChildren().add(contentText);

        // Add the publication details VBox to the viewPublication VBox
        viewPublication.getChildren().setAll(publicationDetails);

        // Create the "Related Topics" title label
        Label relatedTopicsLabel = new Label("Explore Related Topics");
        relatedTopicsLabel.getStyleClass().add("text-bold"); // Apply CSS style for bold text
        relatedTopicsLabel.setStyle("-fx-text-fill: #b4c5d3;"); // Set text color to green
        HBox subcategoryButtonsBox = new HBox();
        subcategoryButtonsBox.setSpacing(10); // Set spacing between buttons
        subcategoryButtonsBox.setAlignment(Pos.CENTER_LEFT); // Align buttons to the left
        for (SousCategorie subcategory : souscatS.select(publication.getCategorie())) {
            Button button = new Button(subcategory.getNom());
            if(Objects.equals(subcategory.getNom(), publication.getSous_categorie().getNom()))
                button.setStyle("-fx-background-radius: 15px; -fx-background-color: #a9cd71; -fx-background-insets: 0, 1, 2;");
                else{
                button.setStyle("-fx-background-radius: 15px; -fx-background-color: transparent; -fx-background-insets: 0, 1, 2; -fx-text-fill: grey;");
                button.setOnMouseEntered(e -> button.setStyle("-fx-background-radius: 15px; -fx-background-color: #a9cd71; -fx-background-insets: 0, 1, 2;"));
                button.setOnMouseExited(e -> button.setStyle("-fx-background-radius: 15px; -fx-background-color: transparent; -fx-text-fill: grey; -fx-background-insets: 0, 1, 2;"));
            }
            button.setMaxHeight(Double.MAX_VALUE); // Set max height to fill the container vertically
            button.setAlignment(Pos.CENTER_LEFT);
            button.setOnAction(event -> {
                displaypubundersubcat(subcategory.getNom());
            });
            subcategoryButtonsBox.getChildren().add(button);
        }
        VBox relatedTopicsBox = new VBox(relatedTopicsLabel, subcategoryButtonsBox);
        relatedTopicsBox.setAlignment(Pos.CENTER_LEFT);
        relatedTopicsBox.setSpacing(20); // Set spacing between components

        Label shareLabel = new Label("Share:");
        shareLabel.getStyleClass().add("text-bold"); // Apply CSS style for bold text
        shareLabel.setStyle("-fx-text-fill: #b4c5d3;");
        HBox shareButtonsBox = new HBox();
        shareButtonsBox.setSpacing(10); // Set spacing between buttons
        shareButtonsBox.setAlignment(Pos.CENTER_RIGHT); // Align buttons to the right
// Add share buttons to the VBox (you need to populate these buttons with appropriate share options)
        Button linkedin = new Button();
        Button twitter = new Button();
        Button whatsapp = new Button();
        double buttonSize = 30; // Set the size of the buttons
        linkedin.setMinSize(buttonSize, buttonSize);
        twitter.setMinSize(buttonSize, buttonSize);
        whatsapp.setMinSize(buttonSize, buttonSize);
        FontAwesomeIcon linkedinIcon = new FontAwesomeIcon();
        linkedinIcon.setIcon(FontAwesomeIcons.CLIPBOARD);
        linkedinIcon.setSize("16px"); // Set the size of the icon
        linkedinIcon.setFill(Color.WHITE); // Set the color of the icon to white

// Create and configure the Twitter icon
        FontAwesomeIcon twitterIcon = new FontAwesomeIcon();
        twitterIcon.setIcon(FontAwesomeIcons.TWITTER);
        twitterIcon.setSize("16px"); // Set the size of the icon
        twitterIcon.setFill(Color.WHITE); // Set the color of the icon to white

// Create and configure the Gmail icon
        FontAwesomeIcon whatsappIcon = new FontAwesomeIcon();
        whatsappIcon.setIcon(FontAwesomeIcons.WHATSAPP);
        whatsappIcon.setSize("16px"); // Set the size of the icon
        whatsappIcon.setFill(Color.WHITE); // Set the color of the icon to white
        linkedin.setGraphic(linkedinIcon);
        twitter.setGraphic(twitterIcon);
        whatsapp.setGraphic(whatsappIcon);
        String buttonStyle = "-fx-background-color: #b4c5d3; -fx-background-radius: 50%;";
        linkedin.setStyle(buttonStyle);
        twitter.setStyle(buttonStyle);
        whatsapp.setStyle(buttonStyle);
        linkedin.setOnAction(e -> {
            // Create a string that combines the title and content of the publication
            String shareText = "Check out this great article on mental health: \n Title :" + publication.getTitre() + "\n Content : " + publication.getContenu()+"\n Posted By : "+publication.getUser().getLastname()+" "+publication.getUser().getFirstname();
            // Create a StringSelection object from the combined text
            StringSelection stringSelection = new StringSelection(shareText);
            // Get the system clipboard
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            // Set the clipboard contents to our StringSelection
            clipboard.setContents(stringSelection, null);
            JOptionPane.showMessageDialog(null, "Text copied to clipboard. Please paste it into your LinkedIn post.");

        });



        twitter.setOnAction(e -> {
            try {
                String shareText = URLEncoder.encode("Check out this article on mental health: \n " + publication.getTitre() + "\n" + publication.getContenu(), "UTF-8");
                Desktop.getDesktop().browse(new URI("https://twitter.com/intent/tweet?text=" + shareText));
            } catch (IOException | URISyntaxException ex) {
                ex.printStackTrace();
            }
        });
        whatsapp.setOnAction(e -> {
            try {
                // Encode the subject
                String subject = URLEncoder.encode("Check out this great article on mental health: \n ", "UTF-8");
                // Properly encode the complete share text including title, content, and poster information
                String shareText = URLEncoder.encode("Title: " + publication.getTitre() + "\nContent: " + publication.getContenu() + "\nPosted By: " + publication.getUser().getLastname() + " " + publication.getUser().getFirstname(), "UTF-8");
                // Concatenate the encoded subject and body into one string for the WhatsApp message
                String message = subject + "%0A%0A" + shareText; // Use '%0A%0A' as a newline character in the URL.
                // Create the WhatsApp link with the correctly concatenated and encoded message
                String whatsappLink = "https://api.whatsapp.com/send?text=" + message;
                Desktop.getDesktop().browse(new URI(whatsappLink));
            } catch (IOException | URISyntaxException exc) {
                exc.printStackTrace();
            }
        });

        shareButtonsBox.getChildren().addAll(linkedin, twitter, whatsapp);
        shareButtonsBox.setAlignment(Pos.CENTER_RIGHT);
        VBox shareBox = new VBox(shareLabel, shareButtonsBox);
        shareBox.setAlignment(Pos.CENTER_RIGHT);
        shareBox.setSpacing(20); // Set spacing between components

        HBox shareandtopicsBox= new HBox(relatedTopicsBox,shareBox);
        shareandtopicsBox.setAlignment(Pos.CENTER);
        shareandtopicsBox.setSpacing(100); // Set the spacing between the related topics and share sections
// Add the HBox to the publicationDetails VBox
        publicationDetails.getChildren().add(shareandtopicsBox);
        int nbComs=cS.countComments(publication);
            publicationDetails.getChildren().add(showComments(nbComs,publication.getId()));



    }
    private VBox showComments(int nbComs, int id) throws SQLException {
        HBox labelbox=new HBox(5);
        labelbox.setAlignment(Pos.CENTER);
        Label commentsLabel = new Label("Comments ("+nbComs+")");
        if(nbComs==0)commentsLabel.setText("No Comments Yet");
        commentsLabel.getStyleClass().add("text-bold"); // Apply CSS style for bold text
        commentsLabel.setStyle("-fx-text-fill: #b4c5d3;"); // Set text color to green
        commentsLabel.getStyleClass().add("h2");
        List <Commentaire> commentaires = cS.select(id);
        Publication pub=pS.selectWhere(id);
        VBox commentsBox = new VBox();
        commentsBox.setAlignment(Pos.CENTER);
        final Button addButton = new Button();
        final Button refreshButton = new Button();
        FontAwesomeIcon addIcon = new FontAwesomeIcon();
        addIcon.setIcon(FontAwesomeIcons.PLUS);
        addIcon.setSize("16px"); // Set the size of the icon
        addIcon.setFill(Color.web("#a9cd71"));
        addButton.setGraphic(addIcon);
        FontAwesomeIcon refreshIcon = new FontAwesomeIcon();
        refreshIcon.setIcon(FontAwesomeIcons.REFRESH);
        refreshIcon.setSize("16px"); // Set the size of the icon
        refreshIcon.setFill(Color.web("#a9cd71"));
        refreshButton.setGraphic(refreshIcon);
        addButton.setStyle("-fx-background-color: transparent;");
        refreshButton.setStyle("-fx-background-color: transparent;");
        addButton.setOnAction(event -> {
            if(Session.getInstance().getUser()==null)
            {try {
                Navigation.navigateTo("/fxml/Auth/Login.fxml", addButton);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }}
            else {
                addcomment(pub);
            }
        });
        refreshButton.setOnAction(event -> {
            try {
                viewPublication(pub);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        if(pub.getCom_ouvert() )labelbox.getChildren().addAll(commentsLabel,refreshButton,addButton);
        else labelbox.getChildren().addAll(commentsLabel,refreshButton);
        commentsBox.getChildren().add(labelbox);
        for (Commentaire comment : commentaires) {
            commentsBox.getChildren().add(createCommentBox(comment));
        }
        return commentsBox;
    }
    private HBox createCommentBox(Commentaire commentaire) {
        HBox hBox = new HBox(20);
        hBox.setAlignment(Pos.CENTER);
        ImageView imageView = new ImageView();
        try {
            String imagePath = UPLOAD_ROOT + "/" + commentaire.getPublication().getImage();
            File file = new File(imagePath);
            String imageUrl = file.toURI().toURL().toExternalForm();
            Image image = new Image(imageUrl);
            imageView.setImage(image);
            imageView.setFitHeight(30.0);
            imageView.setPreserveRatio(true);
            hBox.getChildren().add(imageView);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        // VBox for text details
        VBox textDetails = new VBox(5);

        Label authorLabel = new Label(commentaire.getUser().getLastname() +" "+commentaire.getUser().getFirstname());
        if(commentaire.getAnonyme())authorLabel.setText("Anonyme");
        authorLabel.getStyleClass().add("text-bold");
        authorLabel.setStyle("-fx-fill: grey;");
        authorLabel.getStyleClass().add("h4");

        LocalDateTime currentTime = LocalDateTime.now();
        Duration duration = Duration.between(commentaire.getDate_m(), currentTime);
        long secondsDiff = duration.getSeconds();
        long minutesDiff = secondsDiff / 60;
        long hoursDiff = minutesDiff / 60;

        String timeText;
        if (secondsDiff < 60) {
            timeText = "il y a " + secondsDiff + " seconde" + (secondsDiff > 1 ? "s" : "");
        } else if (minutesDiff < 60) {
            timeText = "il y a " + minutesDiff + " minute" + (minutesDiff > 1 ? "s" : "");
        } else if (hoursDiff < 24) {
            timeText = "il y a " + hoursDiff + " heure" + (hoursDiff > 1 ? "s" : "");
        } else {
            // Use the original date format
            timeText = commentaire.getDate_m().format(DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm:ss"));
        }

        Text dateText = new Text(timeText);
        dateText.getStyleClass().add("text-bold");
        dateText.setStyle("-fx-fill: #a9cd71;");
        HBox dateBox = new HBox(5);
        dateBox.getChildren().add(dateText);
        if(!commentaire.getDate_c().equals(commentaire.getDate_m()))
        {
            Text updatedText = new Text(" Updated");
            updatedText.getStyleClass().add("text-bold");
            updatedText.setStyle("-fx-fill: #b4c5d3;");
            dateBox.getChildren().addAll(new Text(" "), updatedText);
        }

        Text contentText = new Text(commentaire.getContenu());
        contentText.getStyleClass().add("text-regular");
        contentText.setStyle("-fx-fill: grey;");
        HBox buttonsBox = new HBox(5);
        buttonsBox.setAlignment(Pos.CENTER_LEFT);
        buttonsBox.getChildren().add(authorLabel);
        if(Session.getInstance().getUser() != null)
        {
            if(commentaire.getUser().getId()== Session.getInstance().getUser().getId())
            {
                buttonsBox.getChildren().clear();
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
                    try {
                        showUpdateCommentDialog(commentaire);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
                deleteButton.setOnAction(event -> {
                    try {
                        showDeleteConfirmationDialog(commentaire);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
                buttonsBox.getChildren().addAll(authorLabel,deleteButton,updateButton);
            }
        }

        textDetails.getChildren().addAll(buttonsBox,dateBox,contentText);
        // Add the text VBox to the HBox
        hBox.getChildren().add(textDetails);
        return hBox;
    }

    private void showDeleteConfirmationDialog(Commentaire comment) throws SQLException {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Are you sure you want to delete the comment ?");
        alert.setContentText(" Posted By : "+comment.getUser().getLastname()+" "+comment.getUser().getFirstname());
        ButtonType confirmButton = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(confirmButton, cancelButton);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == confirmButton) {
            cS.delete(comment.getId());
            viewPublication(comment.getPublication());
        }
    }

    private void showUpdateCommentDialog(Commentaire comment) throws SQLException {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("HelpMeBalance");
        Label titleLabel = new Label("Edit Comment");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-alignment: center;");
        Label contentLabel = new Label("Content");
        TextField content = new TextField(comment.getContenu());
        VBox titleImageBox = new VBox();
        titleImageBox.getChildren().addAll(titleLabel);
        titleImageBox.setAlignment(Pos.CENTER);
        titleImageBox.setSpacing(10);
        VBox contentBox = new VBox();
        contentBox.getChildren().addAll(titleImageBox,contentLabel,content);
        contentBox.setSpacing(10);
        contentBox.setAlignment(Pos.CENTER_LEFT);
        Button updateButton = new Button("Update Comment");
        HBox buttonBox = new HBox();
        buttonBox.getChildren().add(updateButton);
        buttonBox.setAlignment(Pos.CENTER);
        updateButton.setOnAction(event -> {
            try {
                comment.setContenu(content.getText());
                cS.update(comment);
                viewPublication(comment.getPublication());
                dialog.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        dialog.getDialogPane().setContent(new VBox(contentBox, buttonBox));
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);
        dialog.getDialogPane().lookupButton(ButtonType.CLOSE).setVisible(false);
        dialog.showAndWait();
    }
    public void addcomment(Publication pub) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("HelpMeBalance");
        Label titleLabel = new Label("Add Comment");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-alignment: center;");
        Label contentLabel = new Label("Content");
        TextField content = new TextField();
        VBox titleImageBox = new VBox();
        titleImageBox.getChildren().addAll(titleLabel);
        titleImageBox.setAlignment(Pos.CENTER);
        titleImageBox.setSpacing(10);
        VBox contentBox = new VBox();
        contentBox.getChildren().addAll(titleImageBox,contentLabel,content);
        contentBox.setSpacing(10);
        contentBox.setAlignment(Pos.CENTER_LEFT);
        Button addButton = new Button("Post Comment");
        HBox buttonBox = new HBox();
        buttonBox.getChildren().add(addButton);
        buttonBox.setAlignment(Pos.CENTER);
        addButton.setOnAction(event -> {
            try {
                Commentaire commentaire =new Commentaire(Session.getInstance().getUser(),pub,content.getText(),false);
                cS.add(commentaire);
                viewPublication(pub);
                dialog.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        dialog.getDialogPane().setContent(new VBox(contentBox, buttonBox));
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);
        dialog.getDialogPane().lookupButton(ButtonType.CLOSE).setVisible(false);
        dialog.showAndWait();
    }

    private void leaveviewpublication()
    {
        viewPublication.getChildren().clear();
        viewPublication.setVisible(false);
        Publications.setVisible(true);
        paginatorhbox.setVisible(true);
    }
}
