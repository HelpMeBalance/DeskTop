package org.example.controllers;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcons;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.example.models.Categorie;
import org.example.models.Publication;
import org.example.models.SousCategorie;
import org.example.service.*;
import org.example.utils.Navigation;
import org.example.utils.Session;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
public class PublicationController implements Initializable {

    @FXML
    private TableView Publications;
    @FXML
    private TableColumn<Publication, String> title;

    @FXML
    private TableColumn<Publication, String> content;

    @FXML
    private TableColumn<Publication, String> userName;

    @FXML
    private TableColumn<Publication, String> state;

    @FXML
    private TableColumn<Publication, Integer> views;

    @FXML
    private TableColumn<Publication, String> comments;

    @FXML
    private TableColumn<Publication, Void> viewcomments;

    @FXML
    private TableColumn<Publication, String> postedAt;

    @FXML
    private TableColumn<Publication, String> updatedAt;
    @FXML
    private TableColumn<Publication, Void> actions;
    @FXML
    public TextField SearchPublication;
    @FXML
    public Hyperlink hlFirst;
    @FXML
    public Pagination pagination;
    @FXML
    public Hyperlink hlLast;
    @FXML
    public Label paginationLegend;
    @FXML
    public AnchorPane chartContainer;
    @FXML
    public TableColumn <Publication, Integer> likes;
    private static final int PAGE_SIZE = 4;
    private static final String UPLOAD_ROOT="src/uploads/pub_pictures";
    private PublicationService pS=new PublicationService();
    private CommentaireService cS=new CommentaireService();
    private CategorieService catS=new CategorieService();
    private LikeService lS=new LikeService();
    private SousCategorieService souscatS=new SousCategorieService();
    ObservableList<Publication> publicationsList=FXCollections.observableArrayList();
    String searchText=null;
    private File selectedFile;
    private ImageView imageView;
    BarChart<String, Number> barChart;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initPublications();
        pagination.currentPageIndexProperty().addListener((obs, oldPageIndex, newPageIndex) -> {
            try {
                loadPublications(newPageIndex.intValue());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }
    private void CreateChart() throws SQLException {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Publication Metrics");
        xAxis.setLabel("Publication");
        yAxis.setLabel("Value");
        chartContainer.getChildren().add(barChart);
        AnchorPane.setTopAnchor(barChart, 0.0);
        AnchorPane.setBottomAnchor(barChart, 0.0);
        AnchorPane.setLeftAnchor(barChart, 0.0);
        AnchorPane.setRightAnchor(barChart, 0.0);
        XYChart.Series<String, Number> viewsSeries = new XYChart.Series<>();
        viewsSeries.setName("Views");
        XYChart.Series<String, Number> likesSeries = new XYChart.Series<>();
        likesSeries.setName("Likes");
        XYChart.Series<String, Number> commentsSeries = new XYChart.Series<>();
        commentsSeries.setName("Comments");
        for (Publication publication : publicationsList) {
            viewsSeries.getData().add(new XYChart.Data<>(publication.getId() + " : " + publication.getTitre(), publication.getVues()));
            likesSeries.getData().add(new XYChart.Data<>(publication.getId() + " : " + publication.getTitre(), lS.countLikes(publication)));
            commentsSeries.getData().add(new XYChart.Data<>(publication.getId() + " : " + publication.getTitre(), cS.countCommentsAdmin(publication)));
        }
        barChart.getData().clear();
        barChart.getData().addAll(viewsSeries, likesSeries, commentsSeries);
        for (XYChart.Series<String, Number> series : barChart.getData()) {
            for (XYChart.Data<String, Number> data : series.getData()) {
                Tooltip tooltip = new Tooltip(data.getXValue().substring(data.getXValue().indexOf(":") + 2) +"\n"+series.getName() + " : "+ data.getYValue().intValue());
                Tooltip.install(data.getNode(), tooltip);
                data.getNode().setOnMouseEntered(event -> {
                    data.getNode().setStyle("-fx-background-color:#b4c5d3;");
                    Point2D point = data.getNode().localToScreen(data.getNode().getBoundsInLocal().getMinX(), data.getNode().getBoundsInLocal().getMinY());
                    double adjustedY = point.getY() - tooltip.getHeight() - 5;
                    tooltip.show(data.getNode(), point.getX(), adjustedY);
                });
                data.getNode().setOnMouseExited(event -> {
                    data.getNode().setStyle("");
                    tooltip.hide();
                });
            }
        }
    }
    private void loadPublications(int pageIndex) throws SQLException {
        List<Publication> publications;
        int totalpub=pS.countPublications();
        if(searchText != null && !searchText.isEmpty()) {
             publications = pS.search(searchText, pagination.getCurrentPageIndex(), PAGE_SIZE);
            totalpub=pS.search(searchText).size();
            pagination.setPageCount((totalpub + PAGE_SIZE - 1) / PAGE_SIZE);
        }else {
            pagination.setPageCount((pS.countPublications() + PAGE_SIZE - 1) / PAGE_SIZE);
            publications = pS.selectAdmin(pagination.getCurrentPageIndex(), PAGE_SIZE);

        }
        publicationsList.setAll(publications);
        updatePaginationLegend(pageIndex,PAGE_SIZE,totalpub);
        chartContainer.getChildren().clear();
        CreateChart();
    }
    private void updatePaginationLegend(int pageIndex, int pageSize, int totalPublications) {
        int start = pageIndex * pageSize + 1;
        int end = Math.min(start + pageSize - 1, totalPublications);
        paginationLegend.setText("Showing " + start + " to " + end + " of " + totalPublications + " publications");
    }
    public void initPublications() {

        try
        {
            pagination.setPageCount((pS.countPublications() + PAGE_SIZE - 1) / PAGE_SIZE);
            loadPublications(pagination.getCurrentPageIndex());
            Publications.setItems(publicationsList);
            CreateChart();
            title.setCellValueFactory(new PropertyValueFactory<>("titre"));
            content.setCellValueFactory(new PropertyValueFactory<>("contenu"));
            userName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUser().getLastname()+" "+cellData.getValue().getUser().getFirstname()));
            state.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getValide() ? "Approved" : "Under Review"));
            views.setCellValueFactory(new PropertyValueFactory<>("vues"));
            likes.setCellValueFactory(cellData -> {
                try {

                    int likeCount = lS.countLikes(cellData.getValue());
                    return new SimpleIntegerProperty(likeCount).asObject();
                } catch (SQLException e) {
                    e.printStackTrace();
                    return new SimpleIntegerProperty(0).asObject();
                }
            });
            comments.setCellValueFactory(cellData -> {
                try {
                    int count = cS.countCommentsAdmin(cellData.getValue());
                    String res;
                    if(cellData.getValue().getCom_ouvert())
                    {
                        if(count>0)res="\uD83D\uDCAC " +count; 
                        else res = "\uD83D\uDEAB comments";
                    }
                    else
                    {
                        if(count>0)res="\uD83D\uDD12 " +count;
                        else res = "\uD83D\uDD12 comments";
                    }
                    return new SimpleStringProperty(res);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            viewcomments.setCellFactory(param -> new TableCell<>(){
                private final Button viewButton = new Button();
                {
                    FontAwesomeIcon viewIcon = new FontAwesomeIcon();
                    viewIcon.setIcon(FontAwesomeIcons.ELLIPSIS_H);
                    viewButton.setGraphic(viewIcon);
                    viewButton.setStyle("-fx-background-color: transparent;");
                    viewButton.setOnAction(event -> {
                        Publication publication = getTableView().getItems().get(getIndex());
                        try {
                            if(cS.countCommentsAdmin(publication)==0 && !publication.getCom_ouvert())
                                showemptycommentsDialog();
                            else Navigation.navigateTo("/fxml/Admin/Commentaire.fxml", viewButton, publication.getId());
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        HBox buttonsBox = new HBox(viewButton);
                        buttonsBox.setSpacing(5);
                        setGraphic(buttonsBox);
                    }
                }
            });
            postedAt.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDate_c().format(DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm:ss"))));
            updatedAt.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDate_m().format(DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm:ss"))));
            actions.setCellFactory(param -> new TableCell<>() {
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
                        Publication publication = getTableView().getItems().get(getIndex());
                        showPublicationDetails(publication);
                    });
                    updateButton.setOnAction(event -> {
                        Publication publication = getTableView().getItems().get(getIndex());
                        showUpdatePublicationDialog(publication);
                    });
                    deleteButton.setOnAction(event -> {
                        Publication publication = getTableView().getItems().get(getIndex());
                        try {
                            showDeleteConfirmationDialog(publication);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        HBox buttonsBox = new HBox(viewButton, updateButton, deleteButton);
                        buttonsBox.setSpacing(5);
                        setGraphic(buttonsBox);
                    }
                }
            });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private void showDeleteConfirmationDialog(Publication publication) throws SQLException {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Are you sure you want to delete the publication ?");
        alert.setContentText("Publication Title: " + publication.getTitre() + "\n Posted By : "+publication.getUser().getLastname()+" "+publication.getUser().getFirstname());
        ButtonType confirmButton = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(confirmButton, cancelButton);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == confirmButton) {
            pS.delete(publication.getId());
            pagination.setPageCount((pS.countPublications() + PAGE_SIZE - 1) / PAGE_SIZE);
            pagination.setCurrentPageIndex(0);
            loadPublications(0);
        }
    }
    private void showemptycommentsDialog() throws SQLException {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("No Comments Found");
        alert.setHeaderText(" The comments are empty and locked ");
        alert.showAndWait();
    }
    private void showPublicationDetails(Publication publication) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("HelpMeBalance");
        Label titleLabel = new Label("Publication Details");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-alignment: center;");
        imageView = new ImageView();
        try {
            String imagePath = UPLOAD_ROOT +"/"+ publication.getImage();
           File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                try (InputStream inputStream = new FileInputStream(imageFile)) {
                    Image image = new Image(inputStream);
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
        Label contentLabel = new Label(
                "Categorie : "+publication.getCategorie().getNom() + "\n" +
                        "Sub-Categorie : "+publication.getSous_categorie().getNom() + "\n" +
                        "Title: " + publication.getTitre() + "\n" +
                        "Content: \n" + publication.getContenu() + "\n"
        );
        VBox titleImageBox = new VBox();
        titleImageBox.getChildren().addAll(titleLabel, imageView);
        titleImageBox.setAlignment(Pos.CENTER);
        titleImageBox.setSpacing(10);
        VBox contentBox = new VBox();
        contentBox.getChildren().addAll(titleImageBox, contentLabel);
        contentBox.setSpacing(10);
        contentBox.setAlignment(Pos.CENTER_LEFT);
        String val=(publication.getValide())?"Invalidate":"Validate";
                Button validateButton = new Button(val);
                HBox buttonBox = new HBox();
                buttonBox.getChildren().add(validateButton);
                buttonBox.setAlignment(Pos.CENTER);
                validateButton.setOnAction(event -> {
                    try {
                        pS.validate(publication.getId());
                        publicationsList.replaceAll(pub -> {
                            if (pub.getId() == publication.getId()) {
                                pub.setValide(!pub.getValide());
                            }
                            return pub;
                        });
                        Publications.refresh();
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
                Publications.refresh();
                loadPublications(pagination.getCurrentPageIndex());
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

    public void addpublication()  {
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
                pagination.setCurrentPageIndex(0);
                loadPublications(0);
                dialog.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }}
        });
        dialog.getDialogPane().setContent(new VBox(contentBox, buttonBox));
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);
        dialog.getDialogPane().lookupButton(ButtonType.CLOSE).setVisible(false);
        dialog.showAndWait();
    }
    public void handleSearch() throws SQLException {
         searchText = SearchPublication.getText().toLowerCase();
        pagination.setCurrentPageIndex(0);
        loadPublications(0);
    }
    public void handleFirstPage() {
        pagination.setCurrentPageIndex(0);
    }
    public void handleLastPage() {
        pagination.setCurrentPageIndex(pagination.getPageCount() - 1);
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

}
