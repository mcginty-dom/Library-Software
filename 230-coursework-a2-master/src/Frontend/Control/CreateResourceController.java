package Frontend.Control;

import Backend.Databases.ResourceDatabase;
import Backend.Resources.Book;
import Backend.Resources.DVD;
import Backend.Resources.Laptop;
import Backend.Resources.Resource;
import Backend.Sounds.SoundEffects;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Logger;

import static Frontend.UIManager.UI_ICON;

/**
 * JavaFX controller for the Resource creation page,
 * linked with Create_Resource.fxml
 * Controls the creation form for books, dvds and laptops.
 * <br>
 * Should never need to be created manually, use the launchCreateResource
 * method to open the page.
 * @author Matt
 */
public class CreateResourceController {

    /**
     * For logging, preferred to System.out for persistence and making
     * console output neater.
     */
    private static final Logger LOGGER = Logger.getLogger(
            CreateResourceController.class.getName());

    /**
     * Title of the create resource page.
     */
    private static final String CREATE_RESOURCE_TITLE = "Create New Resources";

    /**
     * Local path to default book icon.
     */
    private static final String DEFAULT_BOOK_ICON_PATH = "src/res/images/thumbnails/book_default.png";

    /**
     * Local path to default DVD icon.
     */
    private static final String DEFAULT_DVD_ICON_PATH = "src/res/images/thumbnails/dvd_default.png";

    /**
     * Local path to default Laptop icon.
     */
    private static final String DEFAULT_LAPTOP_ICON_PATH = "src/res/images/thumbnails/laptop_default.png";

    /**
     * Default Icon to display for books with no assigned icon, from the default book icon path.
     */
    private static final Image DEFAULT_BOOK_ICON_IMAGE = new Image("file:" + DEFAULT_BOOK_ICON_PATH);

    /**
     * Default Icon to display for dvds with no assigned icon, from the default dvd icon path.
     */
    private static final Image DEFAULT_DVD_ICON_IMAGE = new Image("file:" + DEFAULT_DVD_ICON_PATH);

    /**
     * Default Icon to display for laptops with no assigned icon, from the default laptop icon path.
     */
    private static final Image DEFAULT_LAPTOP_ICON_IMAGE = new Image("file:" + DEFAULT_LAPTOP_ICON_PATH);


    // Books
    @FXML private ImageView bookThumbnailPreview;
    @FXML private TextField bookTitleField;
    @FXML private TextField bookPublishingYear;
    @FXML private TextField bookAuthor;
    @FXML private TextField bookPublisher;
    @FXML private TextField bookGenre;
    @FXML private TextField bookISBN;
    @FXML private TextField bookLanguage;
    @FXML private ChoiceBox bookMinimumLoanDuration;
    @FXML private Spinner bookCopyNumber;
    private boolean bookThumbnailChanged;

    @FXML private Label bookOutputLabel;

    // DVDs
    @FXML private ImageView dvdThumbnailPreview;
    @FXML private TextField dvdTitleField;
    @FXML private TextField dvdPublishingYear;
    @FXML private TextField dvdDirectorField;
    @FXML private TextField dvdRuntimeField;
    @FXML private TextField dvdLanguageField;
    @FXML private TextField dvdSubtitlesField;
    @FXML private ChoiceBox dvdMinimumLoanDuration;
    @FXML private Spinner dvdCopyNumber;
    private boolean dvdThumbnailChanged;

    @FXML private Label dvdOutputLabel;

    // Laptops
    @FXML private ImageView laptopThumbnailPreview;
    @FXML private TextField laptopTitleField;
    @FXML private TextField laptopReleaseYearField;
    @FXML private TextField laptopManufacturerField;
    @FXML private TextField laptopModelField;
    @FXML private TextField laptopOSField;
    @FXML private ChoiceBox laptopMinimumLoanDuration;
    @FXML private Spinner laptopCopyNumber;
    private boolean laptopThumbnailChanged;

    @FXML private Label laptopOutputLabel;

    /**
     * JavaFX method called when this controller is created, sets
     * the initial state of nodes in the system. Should never need
     * to be called directly.
     */
    @FXML private void initialize() {
        setupChoiceBoxes();
        setupSpinners();
    }

    /**
     * Sets all spinners constraints so they can be used.
     */
    private void setupSpinners() {
        Spinner[] spinners = new Spinner[]{
                bookCopyNumber,
                dvdCopyNumber,
                laptopCopyNumber
        };
        for (Spinner spinner: spinners) {
            spinner.setValueFactory(
                    new SpinnerValueFactory.IntegerSpinnerValueFactory(
                            0, Integer.MAX_VALUE, 0
                    )
            );
        }
    }

    /**
     * Sets all choice boxes' choices so they can be used.
     */
    private void setupChoiceBoxes() {
        ChoiceBox[] choiceBoxes = new ChoiceBox[]{
                bookMinimumLoanDuration,
                dvdMinimumLoanDuration,
                laptopMinimumLoanDuration
        };
        for (ChoiceBox cd: choiceBoxes){
            cd.setItems(FXCollections.observableArrayList(
                    Arrays.asList(
                            Resource.ONE_DAY,
                            Resource.ONE_WEEK,
                            Resource.TWO_WEEKS,
                            Resource.FOUR_WEEKS
                    )
            ));
        }
    }

    /**
     * JavaFX method. Handle pressing a cancel button, ignore all changes and
     * close the window.
     * @param actionEvent the action event of a button click
     */
    @FXML private void handleCancel(final ActionEvent actionEvent) {
        Node caller = (Node) actionEvent.getSource();
        Stage stage = (Stage) (caller).getScene().getWindow();
        stage.fireEvent(
                new WindowEvent(
                        stage,
                        WindowEvent.WINDOW_CLOSE_REQUEST
                )
        );
    }

    // BOOKS

    /**
     * JavaFX method. Handle pressing the reset button on the book tab.
     * Remove all changes and set all fields to default values
     * @param actionEvent the action event created but clicking the reset button
     */
    @FXML private void handleResetBook(final ActionEvent actionEvent) {
        SoundEffects.resetButton.play();
        bookThumbnailPreview.setImage(DEFAULT_BOOK_ICON_IMAGE);
        bookTitleField.setText("");
        bookPublishingYear.setText("");
        bookAuthor.setText("");
        bookPublisher.setText("");
        bookGenre.setText("");
        bookISBN.setText("");
        bookLanguage.setText("");
        bookMinimumLoanDuration.setValue(null);
        bookCopyNumber.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(
                        0, Integer.MAX_VALUE, 0
                )
        );
        bookThumbnailChanged = false;
    }

    /**
     * JavaFX method. Handle pressing the submit button on the book tab.
     * Read all fields and attempt to construct a book in the system.
     * If successful the book will be added to the system, if unsuccessful
     * nothing will be added.
     * @param actionEvent the action event generated by the submit button.
     * @throws IOException may create an exception if there is an error
     * handling thumbnails.
     */
    @FXML private void handleSubmitBook(final ActionEvent actionEvent) throws IOException {
        boolean valid = true;

        // Mandatory fields
        String title = bookTitleField.getText();
        if (title.equals("")) {
            bookOutputLabel.setText("Title is required");
            valid = false;
        }
        String yearString = bookPublishingYear.getText();
        if (yearString.equals("")) {
            bookOutputLabel.setText("Year is required");
            valid = false;
        }
        int year = Integer.MIN_VALUE;
        try {
            year = Integer.parseInt(yearString);
        } catch (NumberFormatException e) {
            bookOutputLabel.setText("Year could not be parsed");
            valid = false;
        }
        String minLoanDuration = (String) bookMinimumLoanDuration.getValue();
        if (minLoanDuration == null) {
            bookOutputLabel.setText("Min loan duration is required");
            valid = false;
        }
        String author = bookAuthor.getText();
        if (author.equals("")) {
            bookOutputLabel.setText("Author is required");
            valid = false;
        }
        String publisher = bookPublisher.getText();
        if (publisher.equals("")) {
            bookOutputLabel.setText("Publisher is required");
            valid = false;
        }

        if (!valid) {
            SoundEffects.errorMessage2.play();
            return;
        }

        // Optional fields

        String genre = bookGenre.getText();
        String isbn = bookISBN.getText();
        String language = bookLanguage.getText();

        // --> Book can be created

        // Handle image, save locally if custom upload.
        int id = ResourceDatabase.getNextResourceID();
        String thumbnailPath;
        if (bookThumbnailChanged) {
            thumbnailPath = String.format(
                    "src/res/images/thumbnails/books/%d.png", id);
            saveImage(bookThumbnailPreview.getImage(), thumbnailPath, "png");
        } else {
            thumbnailPath = DEFAULT_BOOK_ICON_PATH;
        }
        Book newBook = new Book(thumbnailPath, id, title, year, minLoanDuration,
                author, publisher, genre, isbn, language);

        ResourceDatabase.addBook(newBook);

        for (int i = 0; i < (int) bookCopyNumber.getValue(); i++) {
            newBook.createAndAddCopy();
        }

        bookOutputLabel.setText("Book: " + newBook.getTitle() + " created");
        LOGGER.info("Book: " + newBook + " was created.");
        SoundEffects.submitButton.play();
        handleResetBook(actionEvent);
    }

    /**
     * JavaFX method. Action to perform on pressing the change thumbnail button
     * in the book tab. Loads an image and sets it as the resource's thumbnail.
     * @param actionEvent the event generated by the change thumbnail button.
     */
    @FXML private void handleChangeBookThumbnail(
            final ActionEvent actionEvent) {
        SoundEffects.imageSelect.play();
        String path = loadImage(actionEvent);
        if (path != null) {
            bookThumbnailChanged = true;
            bookThumbnailPreview.setImage(new Image("file:" + path));
        }
    }

    // DVDs

    /**
     * JavaFX method. Handle pressing the reset button on the dvd tab.
     * Remove all changes and set all fields to default values
     * @param actionEvent the action event created but clicking the reset button
     */
    @FXML private void handleResetDVD(final ActionEvent actionEvent) {
        SoundEffects.resetButton.play();
        dvdThumbnailPreview.setImage(DEFAULT_DVD_ICON_IMAGE);
        dvdTitleField.setText("");
        dvdPublishingYear.setText("");
        dvdDirectorField.setText("");
        dvdRuntimeField.setText("");
        dvdLanguageField.setText("");
        dvdSubtitlesField.setText("");
        dvdMinimumLoanDuration.setValue(null);
        dvdCopyNumber.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 0));
        dvdThumbnailChanged = false;
    }

    /**
     * JavaFX method. Handle pressing the submit button on the dvd tab.
     * Read all fields and attempt to construct a dvd in the system.
     * If successful the dvd will be added to the system, if unsuccessful
     * nothing will be added.
     * @param actionEvent the action event generated by the submit button.
     * @throws IOException may create an exception if there is an error
     * handling thumbnails.
     */
    @FXML private void handleSubmitDVD(
            final ActionEvent actionEvent) throws IOException {
        boolean valid = true;
        // Mandatory fields
        String title = dvdTitleField.getText();
        if (title.equals("")) {
            dvdOutputLabel.setText("Title is required");
            valid = false;
        }
        String yearString = dvdPublishingYear.getText();
        if (yearString.equals("")) {
            dvdOutputLabel.setText("Year is required");
            valid = false;
        }
        int year = Integer.MIN_VALUE;
        try {
            year = Integer.parseInt(yearString);
        } catch (NumberFormatException e) {
            dvdOutputLabel.setText("Year could not be parsed");
            valid = false;
        }
        String minLoanDuration = (String) dvdMinimumLoanDuration.getValue();
        if (minLoanDuration == null) {
            dvdOutputLabel.setText("Min loan duration is required");
            valid = false;
        }
        String director = dvdDirectorField.getText();
        if (director.equals("")) {
            dvdOutputLabel.setText("Director is required");
            valid = false;
        }
        String runLengthString = dvdRuntimeField.getText();
        if (runLengthString.equals("")) {
            dvdOutputLabel.setText("Run lenfth is required");
            valid = false;
        }
        int runLength = Integer.MIN_VALUE;
        try {
            runLength = Integer.parseInt(runLengthString);
        } catch (NumberFormatException e) {
            dvdOutputLabel.setText("Run length could not be parsed");
            valid = false;
        }
        if (!valid) {
            SoundEffects.errorMessage2.play();
            return;
        }

        // Optional fields

        String language = dvdLanguageField.getText();
        String[] subtitleLanguages = dvdSubtitlesField.getText().split(",");
        for (int i = 0; i < subtitleLanguages.length; i++) {
            subtitleLanguages[i] = subtitleLanguages[i].trim();
        }

        // --> DVD can be created

        // Handle image, save locally if custom upload.
        int id = ResourceDatabase.getNextResourceID();
        String thumbnailPath;
        if (dvdThumbnailChanged) {
            thumbnailPath = String.format(
                    "src/res/images/thumbnails/dvds/%d.png", id
            );
            saveImage(dvdThumbnailPreview.getImage(), thumbnailPath, "png");
        } else {
            thumbnailPath = DEFAULT_DVD_ICON_PATH;
        }
        DVD newDVD = new DVD(thumbnailPath, id, title, year, minLoanDuration,
                director, language, subtitleLanguages, runLength);
        ResourceDatabase.addDvd(newDVD);

        for (int i = 0; i < (int) dvdCopyNumber.getValue(); i++) {
            newDVD.createAndAddCopy();
        }

        dvdOutputLabel.setText("DVD: " + newDVD.getTitle() + " created");
        LOGGER.info("DVD: " + newDVD + " was created.");
        SoundEffects.submitButton.play();
        handleResetDVD(actionEvent);
    }

    /**
     * JavaFX method. Action to perform on pressing the change thumbnail button
     * in the dvd tab. Loads an image and sets it as the resource's thumbnail.
     * @param actionEvent the event generated by the change thumbnail button.
     */
    @FXML private void handleChangeDVDThumbnail(final ActionEvent actionEvent) {
        SoundEffects.imageSelect.play();
        String path = loadImage(actionEvent);
        if (path != null) {
            dvdThumbnailChanged = true;
            dvdThumbnailPreview.setImage(new Image("file:" + path));
        }
    }

    // Laptop
    /**
     * JavaFX method. Handle pressing the reset button on the dvd tab.
     * Remove all changes and set all fields to default values
     * @param actionEvent the action event created but clicking the reset button
     */
    @FXML private void handleResetLaptop(final ActionEvent actionEvent) {
        SoundEffects.resetButton.play();
        laptopThumbnailPreview.setImage(DEFAULT_LAPTOP_ICON_IMAGE);
        laptopTitleField.setText("");
        laptopReleaseYearField.setText("");
        laptopManufacturerField.setText("");
        laptopModelField.setText("");
        laptopMinimumLoanDuration.setValue(null);
        laptopCopyNumber.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(
                        0, Integer.MAX_VALUE, 0
                )
        );
        laptopThumbnailChanged = false;
    }

    /**
     * JavaFX method. Handle pressing the submit button on the laptop tab.
     * Read all fields and attempt to construct a laptop in the system.
     * If successful the laptop will be added to the system, if unsuccessful
     * nothing will be added.
     * @param actionEvent the action event generated by the submit button.
     * @throws IOException may create an exception if there is an error
     * handling thumbnails.
     */
    @FXML private void handleSubmitLaptop(
            final ActionEvent actionEvent) throws IOException {
        boolean valid = true;

        // Mandatory fields
        String title = laptopTitleField.getText();
        if (title.equals("")) {
            laptopOutputLabel.setText("Title is required");
            valid = false;
        }
        String yearString = laptopReleaseYearField.getText();
        if (yearString.equals("")) {
            laptopOutputLabel.setText("Year is required");
            valid = false;
        }
        int year = Integer.MIN_VALUE;
        try {
            year = Integer.parseInt(yearString);
        } catch (NumberFormatException e) {
            laptopOutputLabel.setText("Year could not be parsed");
            valid = false;
        }
        String minLoanDuration = (String) laptopMinimumLoanDuration.getValue();
        if (minLoanDuration == null) {
            laptopOutputLabel.setText("Min loan duration is required");
            valid = false;
        }
        String manufacturer = laptopManufacturerField.getText();
        if (manufacturer.equals("")) {
            laptopOutputLabel.setText("Manufacturer is required");
            valid = false;
        }
        String model = laptopModelField.getText();
        if (model.equals("")) {
            laptopOutputLabel.setText("Model is required");
            valid = false;
        }
        String os = laptopOSField.getText();
        if (os.equals("")) {
            laptopOutputLabel.setText("OS is required");
            valid = false;
        }
        if(!valid) {
            SoundEffects.errorMessage2.play();
            return;
        }

        // --> Laptop can be created

        // Handle image, save locally if custom upload.
        int id = ResourceDatabase.getNextResourceID();
        String thumbnailPath;
        if (laptopThumbnailChanged) {
            thumbnailPath = String.format(
                    "src/res/images/thumbnails/laptops/%d.png", id
            );
            saveImage(laptopThumbnailPreview.getImage(), thumbnailPath, "png");
        } else {
            thumbnailPath = DEFAULT_LAPTOP_ICON_PATH;
        }
        Laptop newLaptop = new Laptop(thumbnailPath, id, title, year,
                minLoanDuration, manufacturer, model, os);
        ResourceDatabase.addLaptop(newLaptop);

        for (int i = 0; i < (int) laptopCopyNumber.getValue(); i++) {
            newLaptop.createAndAddCopy();
        }

        laptopOutputLabel.setText(
                "Laptop: " + newLaptop.getTitle() + " created"
        );
        LOGGER.info("Laptop: " + newLaptop + " was created.");
        SoundEffects.submitButton.play();
        handleResetLaptop(actionEvent);


    }

    /**
     * JavaFX method. Action to perform on pressing the change thumbnail button
     * in the laptop tab. Loads an image and sets it as the resource's
     * thumbnail.
     * @param actionEvent the event generated by the change thumbnail button.
     */
    @FXML private void handleChangeLaptopThumbnail(
            final ActionEvent actionEvent) {
        SoundEffects.imageSelect.play();
        String path = loadImage(actionEvent);
        if (path != null) {
            laptopThumbnailChanged = true;
            laptopThumbnailPreview.setImage(new Image("file:" + path));
        }
    }

    /**
     * Save a javafx Image to a given filepath with the specified extension.
     * @param image the JavaFX image to save
     * @param thumbnailPath path to save image at with extension
     * @param format the file format to save in
     * @throws IOException if there was an error saving the image this will be
     * thrown.
     */
    private static void saveImage(
            final Image image, final String thumbnailPath, final String format
    ) throws IOException {

        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        File outFile = new File(thumbnailPath);
        ImageIO.write(bufferedImage, format, outFile);
    }

    /**
     * Open a system file selector and get the path to the file that the
     * user selects. If the user closes the selector without choosing
     * an image this will return null.
     * @param actionEvent an action event generated by a node on the active
     *                    stage
     * @return a string path to an image file or null.
     */
    private String loadImage(ActionEvent actionEvent) {

        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter(
                "Image files", "*.png", "*.jpg", "*.jpeg", "*.gif");
        fileChooser.getExtensionFilters().add(filter);

        Node node = (Node) actionEvent.getSource();
        Stage stage = (Stage) (node).getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            return file.getPath();
        }
        return null;
    }

    /**
     * Launch the create resource window as a popup from the current javaFX
     * application.
     * @throws IOException if there was a error reading data to launch the page.
     */
    public static void launchCreateResourcePage() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                DashboardController.class.getResource(
                        "/fxml/Create_Resource.fxml"
                )
        );
        loader.load();
        Parent root = loader.getRoot();
        Scene newScene = new Scene(root);
        Stage stage = new Stage();
        stage.setTitle(CREATE_RESOURCE_TITLE);
        stage.setScene(newScene);
        stage.show();
        stage.getIcons().add(new Image("file:" + UI_ICON));
    }
}
