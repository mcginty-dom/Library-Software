package Frontend.Control;

import Backend.Sounds.SoundEffects;
import Backend.Users.User;
import Frontend.UIManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.TilePane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.paint.Color;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static Frontend.UIManager.UI_ICON;

/**
 * FXML controller for the Avatar select page, linked with Avatar_Select.fxml.
 * <br>
 * Should never need to be created manually, use the launchAvatarSelectPage
 * method to open the page and acquire the controller
 * @author Matt
 */
public class AvatarSelectPageController {

    /**
     * For logging, preferred to System.out for persistence and making
     * console output neater.
     */
    private static final Logger LOGGER = Logger.getLogger(
            AvatarSelectPageController.class.getName());

    /**
     * Root path to the folders of avatar images.
     */
    public static final String AVATARS_PATH = "src/res/images/avatars";
    /**
     * Extension from AVATARS_PATH to the directory containing user avatar
     * folders.
     */
    public static final String USER_AVATAR_PATH = "user_avatars";
    /**
     * Extension from AVATARS_PATH to the temporary avatar directory.
     */
    public static final String TEMP_AVATAR_PATH = "temp_avatars";
    /**
     * Extension from AVATARS_PATH to the directory containing emoji
     * style avatars.
     */
    public static final String EMOJI_PATH = "emoji";
    /**
     * Extension from AVATARS_PATH to the directory containing default
     * system avatars.
     */
    public static final String SYSTEM_AVATARS_PATH = "system_avatars";

    /**
     * The base width and height of each image in the selection inspector.
     */
    private static final double DEFAULT_IMAGE_SIZE = 100;

    /**
     * The file chooser for loading images on load image.
     */
    private final FileChooser fileChooser = new FileChooser();

    // From fxml
    @FXML private TabPane avatarTabPane;
    @FXML private TilePane systemAvatarsPane;
    @FXML private TilePane emojiPane;
    @FXML private Tab customAvatarTab;
    @FXML private TilePane customAvatarPane;
    @FXML private Label noImagesAvailable;
    @FXML private Button useImageButton;
    @FXML private Slider sizeSlider;
    @FXML private Label sizeSliderLabel;

    private HashMap<ImageView, String> imagePaths = new HashMap<>();
    private ImageView selected;
    private Stage stage;
    private User user;

    private ArrayList<ImageView> images = new ArrayList<>();

    /**
     * Method will be called on loading page, perform all initialization tasks.
     * @throws IOException if images could not be loaded
     * will throw an IOException.
     */
    @FXML
    private void initialize() throws IOException {

        // Create the temp directory
        new File(Paths.get(AVATARS_PATH, TEMP_AVATAR_PATH).toString()).mkdirs();

        // Set up file chooser
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter(
                "Image files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.mpo");
        fileChooser.getExtensionFilters().add(filter);
        filter = new FileChooser.ExtensionFilter("All files", "*");
        fileChooser.getExtensionFilters().add(filter);

        // Set up slider
        sizeSlider.valueProperty().addListener(
                (observable, oldValue, newValue) -> updateImageSizes(
                        newValue.intValue()));

        // Set up images
        loadImages();

        useImageButton.setDisable(true);

    }

    /**
     * Set the size of images.
     * @param newValue the width and height of each image in the image inspector.
     */
    private void updateImageSizes(final int newValue) {
        sizeSliderLabel.setText(String.format("%4d", newValue) + "%");
        for (ImageView imageView: images) {
            imageView.setFitHeight(newValue);
            imageView.setFitWidth(newValue);
        }
    }

    /**
     * Load all image previews from file and stores them as previews in their relevant tab.
     * @throws IOException if files could not be loaded will throw an exception
     */
    private void loadImages() throws IOException {
        systemAvatarsPane.getChildren().clear();
        emojiPane.getChildren().clear();
        customAvatarPane.getChildren().clear();


        // Load system avatars
        try (Stream<Path> paths = Files.list(Paths.get(AVATARS_PATH, SYSTEM_AVATARS_PATH))) {
            List<Path> systemImagePaths;
            systemImagePaths = paths.collect(Collectors.toList());
            for (Path p : systemImagePaths) {
                ImageView imageView = createSelectableImageView(p.toString());
                systemAvatarsPane.getChildren().add(imageView);
                images.add(imageView);
            }

        }
        // Load emoji avatars
        try (Stream<Path> paths = Files.list(Paths.get(AVATARS_PATH, EMOJI_PATH))) {
            List<Path> emojiImagePaths;
            emojiImagePaths = paths.collect(Collectors.toList());
            for (Path p : emojiImagePaths) {
                ImageView imageView = createSelectableImageView(p.toString());
                emojiPane.getChildren().add(imageView);
                images.add(imageView);
            }
        }

        // Load all custom avatars
        Path userImagesPath;
        if (this.user == null) {
            userImagesPath = Paths.get(AVATARS_PATH, TEMP_AVATAR_PATH);
        } else {
            userImagesPath = getUsernamePath(user.getUsername());
        }

        // Create if doesn't exist
        File directory = new File(userImagesPath.toString());
        if (!directory.exists()) {
            directory.mkdirs();
        }

        try (Stream<Path> paths = Files.list(userImagesPath)) {
            List<Path> customImagePaths;
            customImagePaths = paths.collect(Collectors.toList());
            if (customImagePaths.size() == 0) {
                noImagesAvailable.setVisible(true);
            } else {
                noImagesAvailable.setVisible(false);
                for (Path p : customImagePaths) {
                    ImageView imageView = createSelectableImageView(
                            p.toString());
                    customAvatarPane.getChildren().add(imageView);
                    images.add(imageView);
                }
            }
        }
    }

    /**
     * Creates a selectable image view pane, with a similar
     * behavior to toggle buttons. Contract that between zero
     * or one images created this way can be selected.
     * @param path the path to an image, local or fully qualified
     * @return an ImageView node with selectable capabilities.
     */
    private ImageView createSelectableImageView(final String path) {
        final float dropRadius = 4.5f;
        final float dropOffset = 4;
        final float dropWidth = 10;
        final float borderWidth = 10;
        final float glowValue = 0.5f;

        ImageView imageView = new ImageView();

        imageView.setFitWidth(DEFAULT_IMAGE_SIZE);
        imageView.setFitHeight(DEFAULT_IMAGE_SIZE);
        imageView.setPreserveRatio(true);

        DropShadow regularDrop = new DropShadow(
                dropRadius, dropOffset, dropOffset, Color.GRAY);
        DropShadow selectedDrop = new DropShadow(
                dropRadius, dropOffset, dropOffset, Color.GRAY);
        InnerShadow selectedBorder = new InnerShadow(
                BlurType.THREE_PASS_BOX, Color.BLACK, 0, 1, 0, 0);
        selectedBorder.setWidth(borderWidth);
        selectedBorder.setHeight(borderWidth);
        Glow selectedGlow = new Glow(glowValue);
        selectedDrop.setInput(selectedBorder);
        selectedBorder.setInput(selectedGlow);


        imageView.setEffect(regularDrop);
        regularDrop.setWidth(dropWidth);
        regularDrop.setHeight(dropWidth);
        imageView.setImage(new Image("file:" + path));
        imageView.addEventFilter(
                MouseEvent.MOUSE_CLICKED,
                event -> handleSelectableImageClick(
                        imageView, regularDrop, selectedDrop));

        imagePaths.put(imageView, path);
        return imageView;
    }

    /**
     * Handles the toggle and selection behaviour of selectable images.
     * @param imageTarget the image that was clicked on.
     * @param regularDrop the drop shadow effect of unselected images.
     * @param selectedDrop the drop shadow effect of selected images.
     */
    private void handleSelectableImageClick(
            final ImageView imageTarget,
            final DropShadow regularDrop,
            final DropShadow selectedDrop) {
        if (selected != null) {
            selected.setEffect(regularDrop);
        }
        if (selected == imageTarget) {
            selected = null;
        } else {
            SoundEffects.resetButton.play();
            selected = imageTarget;
            selected.setEffect(selectedDrop);
        }
        useImageButton.setDisable(selected == null);
    }


    /**
     * Javafx generated method. Behaviour to perform when
     * the "Use image" button is clicked.
     * Uses the selected image for the user avatar and close the window.
     * @param actionEvent actionEvent generated by the button
     */
    @FXML
    private void handleUseImage(final ActionEvent actionEvent) {
        SoundEffects.submitButton.play();
        WindowEvent close = new WindowEvent(
                stage, WindowEvent.WINDOW_CLOSE_REQUEST);
        stage.fireEvent(close);
    }

    /**
     * FXML generated method. Behaviour to perform when the "New Image" button
     * is clicked.
     * Opens the paint program as a popup, and will be ready to accept an image
     * created by a user if it is created.
     * @param actionEvent actionEvent generated by the button
     * @throws IOException if loading the paint program fails will throw an
     * IOException.
     */
    @FXML
    private void handleNewImage(
            final ActionEvent actionEvent) throws IOException {
        SoundEffects.newPage.play();

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/Paint_Program.fxml"));
        loader.load();
        Parent paintProgramRoot = loader.getRoot();
        PaintProgramController controller = loader.getController();
        Scene newScene = new Scene(paintProgramRoot);
        Stage newStage = new Stage();
        newStage.initModality(Modality.APPLICATION_MODAL);
        newStage.getIcons().add(new Image("file:" + UIManager.UI_ICON));
        newStage.setTitle("Tawe Lib Paint");
        newStage.setScene(newScene);
        newStage.setOnHidden(event -> onNewImageClose(controller));

        controller.setStage(newStage);

        newStage.show();
    }

    /**
     * How to respond when the paint program closes.
     * Take the image from the controller and add it to custom images.
     * @param controller the controller of a newly created paint program.
     */
    private void onNewImageClose(final PaintProgramController controller) {
        BufferedImage image = controller.getAvatarImage();
        if (image != null) {
            String path = null;
            try {
                path = saveCustomImage(image, user);
                assert (path != null);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ImageView imageView = createSelectableImageView(path);
            noImagesAvailable.setVisible(false);
            customAvatarPane.getChildren().add(imageView);
            images.add(imageView);
            avatarTabPane.getSelectionModel().select(customAvatarTab);
        }
    }

    /**
     * Save an image to a user's custom images directory.
     * @param image The image to save.
     * @param user The user the image belongs to.
     * @return the path of the newly saved images.
     * @throws IOException if there was an error saving the file
     */
    private static String saveCustomImage(
            final BufferedImage image, final User user) throws IOException {

        String outputImageNameBase = Integer.toString(image.hashCode());
        String outputImageName;

        // Load all custom avatars
        Path path;
        if (user != null) {
            path = getUsernamePath(user.getUsername());
        } else {
            path = Paths.get(AVATARS_PATH, TEMP_AVATAR_PATH);
        }

        // Create if doesn't exist
        File directory = new File(path.toString());
        if (!directory.exists()) {
            directory.mkdirs();
        }

        try (Stream<Path> paths = Files.list(path)) {
            List<String> imagePathsStrings = new ArrayList<>();
            List<Path> imagePaths;
            imagePaths = paths.collect(Collectors.toList());
            for (Path p : imagePaths) {
                imagePathsStrings.add(p.toString());
            }

            outputImageName = outputImageNameBase + ".png";
            int i = 0;
            while (imagePathsStrings.contains(outputImageName)) {
                outputImageName = outputImageNameBase + i + ".png";
                i++;
            }

            File outputFile = new File(
                    Paths.get(path.toString(), outputImageName).toString());
            ImageIO.write(image, "png", outputFile);
        }
        return Paths.get(path.toString(), outputImageName).toString();

    }


    /**
     * Javafx generated method. Behaviour to perform when the "Load image"
     * button is clicked.
     * Allows a user to select one or more (or none) images from their local
     * filesystem, and allow them to be used as avatars by the user.
     * @param actionEvent actionEvent generated by the button
     */
    @FXML
    private void handleLoadImage(final ActionEvent actionEvent) {
        SoundEffects.imageSelect.play();

        List<File> list =
                fileChooser.showOpenMultipleDialog(stage);
        if (list != null) {
            for (File file : list) {
                customAvatarPane.getChildren().add(
                        createSelectableImageView(file.getPath()));
            }
            avatarTabPane.getSelectionModel().select(customAvatarTab);
        }

    }


    /**
     * Javafx generated method. Behaviour to perform when the "Cancel" button
     * is clicked. Close the popup, returning no image to the original caller.
     * Performs the same function as the close button
     * @param actionEvent actionEvent generated by the button
     */
    @FXML
    private void handleCancel(final ActionEvent actionEvent) {
        SoundEffects.cancel.play();
        selected = null;
        stage.fireEvent(new WindowEvent(
                        stage,
                        WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    /**
     * Returns the relative path to the specified user's image folder from the
     * project root. Does not verify that this user's folder actually exists.
     * @param username the username of the user.
     * @return the relative path to the user's image folder.
     */
    public static Path getUsernamePath(final String username) {
        /*
         * Convert any name into a unique name containing only alphanumeric
         * characters and underscores. This is guaranteed because:
         *
         * If the character is alphanumeric leave it.
         * If the character is an underscore, put an underscore before it.
         * If the character is neither alphanumeric OR a an underscore put
         * an underscore before it and add the hex value of its character.
         *
         *
         * So : Test123          -> Test123
         *    : Test ?123*       -> Test_20_3f123_2a
         *    : Test_2123__      -> Test___3f123____
         *    : Test___3f123____ -> Test______3f123________
         *    : .                -> _2e
         *    : _2e              -> __2e
         *    : __2e             -> ____2e
         *
         *    The only limit is that names will grow up to 5 times the given
         *    size, however this is an inevitable side effect of removing so
         *    many character.
         */
        StringBuilder path = new StringBuilder();
        final char escapeChar = '_';
        for (char c: username.toCharArray()) {
            if (c == escapeChar) {
                path.append(escapeChar);
                path.append(c);
            } else if (!Character.toString(c).matches("[a-zA-Z1-9]")) {
                path.append(escapeChar);
                path.append(Integer.toHexString(c));
            } else {
                path.append(c);
            }
        }
        return Paths.get(AVATARS_PATH, USER_AVATAR_PATH, path.toString());
    }

    /**
     * Set the user for this controller, loading all user based content.
     * @param setUser a user. Assumed to not be null.
     */
    private void setUser(final User setUser) {
        this.user = setUser;
        try {
            loadImages();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the path to the image selected in this session, or null
     * if no image have been selected.
     * @return the path to the image selected by a user.
     * If no image is selected this will return null.
     */
    public String getSelectedPath() {
        if (selected == null) {
            return null;
        }
        return imagePaths.get(selected);
    }

    /**
     * Get the stage of the AvatarSelectPage window that this controller
     * manages.
     * @return the window that this controller manages.
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * Create an instance of the avatar select page.
     * This will pop up the selector and is the advised way to produce a
     * selector.
     * @param user The current user to set any user-specific parameters.
     *             can be null to set no parameters
     * @return the controller of the newly created window.
     * @throws IOException if there is an error loading the page.
     */
    public static AvatarSelectPageController launchAvatarSelectPage(
            final User user) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                AvatarSelectPageController.class.getResource(
                        "/fxml/Avatar_Select.fxml"));
        loader.load();
        Parent avatarSelectRoot = loader.getRoot();
        AvatarSelectPageController controller = loader.getController();
        Scene newScene = new Scene(avatarSelectRoot);

        Stage stage = new Stage();
        stage.setScene(newScene);
        controller.setUser(user);
        controller.stage = stage;

        stage.initModality(Modality.APPLICATION_MODAL);
        stage.getIcons().add(new Image("file:" + UI_ICON));
        stage.setTitle("Choose an avatar");

        stage.show();
        return controller;
    }

}
