package Frontend.Control;

import Frontend.Paint.ColorPicker.ColorPickerPane;
import Frontend.Paint.Tools.PaintCanvas;
import Frontend.Paint.Toolbar.*;
import Frontend.UIManager;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.security.Policy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Controller for the paint program
 * Linked to Paint_Program.fxml
 * Allows user to use the paint program to draw custom avatars.
 * Handles all background processes for saving, loading, and
 * reseting the program
 * <br>
 * Should never need to be created manually, use the launchPaintProgram
 * method to open the page and get the controller
 * @author matt
 */
public class PaintProgramController {

    /**
     * The default width and height of a canvas in the paint program.
     */
    public static final int DEFAULT_CANVAS_SIZE = 512;

    private BufferedImage avatarImage;


    @FXML private Button newButton;
    @FXML private Button openFileButton;
    @FXML private Button saveButton;
    @FXML private Button saveAsButton;
    @FXML private ToggleButton touchSupportToggle;

    @FXML private ToggleButton paintButton;
    @FXML private ToggleButton eraserButton;
    @FXML private ToggleButton bucketButton;
    @FXML private ToggleButton eyeDropperButton;
    @FXML private ToggleButton lineButton;
    @FXML private ToggleButton drawSquareButton;
    @FXML private ToggleButton drawCircleButton;
    @FXML private ToggleButton dragButton;
    @FXML private ToggleButton magnifyButton;

    private Stage stage;

    private BrushToolbar brushToolbar = new BrushToolbar();
    private FillToolbar fillToolbar = new FillToolbar();
    private EraserToolbar eraserToolbar = new EraserToolbar();
    private DragToolbar dragToolbar;
    private MagnifyingToolbar magnifyingToolbar;
    private LineToolbar lineToolbar = new LineToolbar();
    private SquareToolbar squareToolbar = new SquareToolbar();
    private CircleToolbar circleToolbar = new CircleToolbar();
    private EyedropperToolbar eyedropperToolbar = new EyedropperToolbar();


    private PaintCanvas paintCanvas;

    @FXML private Button undoButton;
    @FXML private Button redoButton;

    @FXML private BorderPane mainPane;
    @FXML private SplitPane workingAreaSplitPane;
    @FXML private ScrollPane canvasViewportArea;

    private String saveFilePath = null;


    /**
     * JavaFX method called when the page is first opened. Sets
     * the initial state for the scene.
     * Should never need to be called manually.
     */
    @FXML
    private void initialize() {
        createNewCanvas(DEFAULT_CANVAS_SIZE, DEFAULT_CANVAS_SIZE, null);
        touchSupportToggle.selectedProperty().addListener(
                (observable, oldValue, newValue) -> {
            paintCanvas.setScrollDisable(newValue);
        });

        Platform.runLater(this::setupAccelerators);

    }

    /**
     * Load the paint canvas as a new canvas, discarding the old one
     * (if there was one) and replacing it. The canvas will have
     * the specified height, width and background color.
     * @param width the new width of the canvas
     * @param height the new height of the canvas
     * @param backgroundColor the background color of the canvas
     */
    private void createNewCanvas(
            final int width, final int height, final Color backgroundColor) {

        final double splitPanePartition = .75;

        workingAreaSplitPane.getItems().clear();

        this.paintCanvas = new PaintCanvas(width, height, backgroundColor);
        ColorPickerPane colorPickerPane = new ColorPickerPane(this.paintCanvas);
        ScrollPane colorPickerScrollPane = new ScrollPane(colorPickerPane);
        colorPickerScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        colorPickerScrollPane.setFitToHeight(true);
        colorPickerScrollPane.setFitToWidth(true);

        canvasViewportArea.setContent(paintCanvas);
        workingAreaSplitPane.getItems().addAll(
                canvasViewportArea, colorPickerScrollPane
        );
        SplitPane.setResizableWithParent(colorPickerScrollPane, Boolean.FALSE);
        workingAreaSplitPane.setDividerPosition(0, splitPanePartition);

        dragToolbar = new DragToolbar(paintCanvas);
        magnifyingToolbar = new MagnifyingToolbar(paintCanvas);

        this.paintButton.fire();
    }

    /**
     * Adds accelerators to all button on the paint program.
     */
    private void setupAccelerators() {
        // Accelerators
        undoButton.getScene().getAccelerators().put(
                new KeyCodeCombination(KeyCode.Z, KeyCombination.SHORTCUT_DOWN),
                () -> undoButton.fire());
        redoButton.getScene().getAccelerators().put(
                new KeyCodeCombination(KeyCode.Y, KeyCombination.SHORTCUT_DOWN),
                () -> redoButton.fire());
        newButton.getScene().getAccelerators().put(
                new KeyCodeCombination(KeyCode.N, KeyCombination.SHORTCUT_DOWN),
                () -> newButton.fire());
        openFileButton.getScene().getAccelerators().put(
                new KeyCodeCombination(KeyCode.O, KeyCombination.SHORTCUT_DOWN),
                () -> openFileButton.fire());
        saveButton.getScene().getAccelerators().put(
                new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN),
                () -> saveButton.fire());
        paintButton.getScene().getAccelerators().put(
                new KeyCodeCombination(KeyCode.B),
                () -> paintButton.fire());
        eraserButton.getScene().getAccelerators().put(
                new KeyCodeCombination(KeyCode.E),
                () -> eraserButton.fire());
        bucketButton.getScene().getAccelerators().put(
                new KeyCodeCombination(KeyCode.K),
                () -> bucketButton.fire());
        eyeDropperButton.getScene().getAccelerators().put(
                new KeyCodeCombination(KeyCode.I),
                () -> eyeDropperButton.fire());
        lineButton.getScene().getAccelerators().put(
                new KeyCodeCombination(KeyCode.L),
                () -> lineButton.fire());
        drawSquareButton.getScene().getAccelerators().put(
                new KeyCodeCombination(KeyCode.S),
                () -> drawSquareButton.fire());
        drawCircleButton.getScene().getAccelerators().put(
                new KeyCodeCombination(KeyCode.C),
                () -> drawCircleButton.fire());
        dragButton.getScene().getAccelerators().put(
                new KeyCodeCombination(KeyCode.D),
                () -> dragButton.fire());
        magnifyButton.getScene().getAccelerators().put(
                new KeyCodeCombination(KeyCode.M),
                () -> magnifyButton.fire());
        touchSupportToggle.getScene().getAccelerators().put(
                new KeyCodeCombination(KeyCode.T),
                () -> magnifyButton.fire());
    }

    /**
     * Cache the stage this controller resides in.
     * @param stage the current stage, not null.
     */
    public void setStage(final Stage stage) {
        this.stage = stage;
        // Don't close without checking
        stage.addEventFilter(
                WindowEvent.WINDOW_CLOSE_REQUEST, this::checkAndExit
        );
    }

    /**
     * JavaFX method. Set the paint brush as the active brush.
     * @param actionEvent the event generated by this button.
     */
    @FXML private void handlePaintbrushSelected(final ActionEvent actionEvent) {
        setActiveBrush(brushToolbar);
    }

    /**
     * JavaFX method. Set the fill bucket as the active brush.
     * @param actionEvent the event generated by this button.
     */
    @FXML private void handleFillBucketSelected(final ActionEvent actionEvent) {
        setActiveBrush(fillToolbar);
    }

    /**
     * JavaFX method. Set the eraser as the active brush.
     * @param actionEvent the event generated by this button.
     */
    @FXML private void handleEraserSelected(final ActionEvent actionEvent) {
        setActiveBrush(eraserToolbar);
    }

    /**
     * JavaFX method. Set the drag hand as the active brush.
     * @param actionEvent the event generated by this button.
     */
    @FXML private void handleDragSelected(final ActionEvent actionEvent) {
        setActiveBrush(dragToolbar);
    }

    /**
     * JavaFX method. Set the magnifying glass as the active brush.
     * @param actionEvent the event generated by this button.
     */
    @FXML private void handleMagnifySelected(final ActionEvent actionEvent) {
        setActiveBrush(magnifyingToolbar);
    }

    /**
     * JavaFX method. Set the line tool as the active brush.
     * @param actionEvent the event generated by this button.
     */
    @FXML private void handleLineSelected(final ActionEvent actionEvent) {
        setActiveBrush(lineToolbar);
    }

    /**
     * JavaFX method. Set the square tool as the active brush.
     * @param actionEvent the event generated by this button.
     */
    @FXML private void handleSquareSelected(final ActionEvent actionEvent) {
        setActiveBrush(squareToolbar);
    }

    /**
     * JavaFX method. Set the circle tool as the active brush.
     * @param actionEvent the event generated by this button.
     */
    @FXML private void handleCircleSelected(final ActionEvent actionEvent) {
        setActiveBrush(circleToolbar);
    }

    /**
     * JavaFX method. Set the eye dropper as the active brush.
     * @param actionEvent the event generated by this button.
     */
    @FXML private void handleEyeDropperSelected(final ActionEvent actionEvent) {
        setActiveBrush(eyedropperToolbar);
    }

    /**
     * Set the active brush, this will register the chosen brush as the
     * interpreter of mouse input for the canvas.
     * @param toolbar the toolbar for the brush selected.
     */
    private void setActiveBrush(final PaintProgramToolbar toolbar) {
        paintCanvas.setActiveBrush(toolbar.getBrush());
        mainPane.setTop(toolbar);
    }

    /**
     * JavaFX method, called with the new canvas button.
     * Will handle replacing the current canvas with a new one.
     * @param actionEvent the event created by this button firing.
     * @throws IOException if there is an error loading new
     * canvas contexts this will be thrown.
     */
    @FXML private void handleCreateNewCanvas(
            final ActionEvent actionEvent) throws IOException {

        Alert alert = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Are you sure you want create a new canvas?"
                        + " Any unsaved changes will be deleted!",
                ButtonType.YES,
                ButtonType.NO);

        alert.showAndWait();
        if (alert.getResult() == ButtonType.NO) {
            return;
        }
        NewImagePopupController controller = NewImagePopupController
                .launchPopup();

        controller.getStage().setOnHiding(event -> {
            if (controller.isSuccessful()) {
                createNewCanvas(
                        controller.getWidth(),
                        controller.getHeight(),
                        controller.getColor());
            }
        });
    }

    /**
     * JavaFX method called with the open file button, will open
     * a system file chooser and allow the user to import
     * and image to the paint program.
     * @param actionEvent teh event generated by the button
     */
    @FXML private void handleOpenFile(final ActionEvent actionEvent) {
        Alert alert = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Are you sure you want to open this file?"
                        + "Any unsaved changes will be deleted!",
                ButtonType.YES,
                ButtonType.NO);

        alert.showAndWait();

        if (alert.getResult() == ButtonType.NO) {
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image");

        FileChooser.ExtensionFilter extFilter = new FileChooser
                .ExtensionFilter(
                        "Image files", "*.png", "*.jpg", "*.jpeg", "*.gif"
        );
        FileChooser.ExtensionFilter allFilter = new FileChooser
                .ExtensionFilter(
                        "All files", "*"
        );

        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.getExtensionFilters().add(allFilter);
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            String openImage = file.getPath();
            try {
                paintCanvas.open(openImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * JavaFX method. Created with press of the save button.
     * Handles saving the current canvas to a file in the
     * user's file system
     * @param actionEvent the event generated by this button press
     */
    @FXML
    private void handleSaveButton(final ActionEvent actionEvent) {
        // Get save dialog
        if (saveFilePath == null) {
            handleSaveAsButton(actionEvent);
            return;
        }

        // Check extension
        final List<String> extensionTest = Arrays.asList(
                "jpg", "png", "jpeg", "gif"
        );

        String[] split = saveFilePath.split("\\.");
        String format;
        if (split.length == 0
                || !extensionTest.contains(split[split.length - 1])) {
            saveFilePath += ".png";
            format = "png";
        } else {
            format = split[split.length - 1];
        }

        // Do save
        try {
            paintCanvas.save(saveFilePath, format);
            System.out.println("Image saved");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * JavaFX method. Created with press of the save as button.
     * Handles saving the current canvas to a file in the
     * user's file system by allowing them to choose a path
     * @param actionEvent the event generated by this button press
     */
    @FXML private void handleSaveAsButton(final ActionEvent actionEvent) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Image");


        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "Image files", "*.png", "*.jpg", "*.jpeg", "*.gif");
        FileChooser.ExtensionFilter allFilter = new FileChooser.ExtensionFilter(
                "All files", "*");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.getExtensionFilters().add(allFilter);
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            saveFilePath = file.getPath();
            System.out.println(saveFilePath);
            handleSaveButton(actionEvent);
        }
    }

    /**
     * JavaFX method. Created with press of the save avatar button.
     * Handles saving the current canvas as a custom avatar for the
     * user using the paint program
     * @param actionEvent the event generated by this button press
     */
    @FXML private void handleSaveAvatar(final ActionEvent actionEvent) {
        this.avatarImage = SwingFXUtils.fromFXImage(
                paintCanvas.getImage(), null);

        Alert alert = new Alert(
                Alert.AlertType.INFORMATION,
                "This image will be added as a custom avatar.",
                ButtonType.OK
        );
        alert.showAndWait();
        onClose(actionEvent);
    }

    /**
     * JavaFX method. Called with a press of the undo button.
     * Attempts to undo the last action.
     * @param actionEvent the event generated by this button
     */
    @FXML private void handleUndo(final ActionEvent actionEvent) {
        paintCanvas.undo();
    }

    /**
     * JavaFX method. Called with a press of the redo button.
     * Attempts to redo the last action.
     * @param actionEvent the event generated by this button
     */
    @FXML private void handleRedo(final ActionEvent actionEvent) {
        paintCanvas.redo();
    }

    /**
     * JavaFX method. Called with a press of the close button.
     * Attempts to close the window.
     * @param actionEvent the event generated by this button
     */
    @FXML private void onClose(final ActionEvent actionEvent) {
        stage.fireEvent(
                new WindowEvent(
                        stage,
                        WindowEvent.WINDOW_CLOSE_REQUEST
                )
        );
    }

    /**
     * Prompts a user to confirm exiting the program and
     * allows them to prevent the window closing.
     * @param event assumed to be a window closing event.
     */
    private void checkAndExit(final WindowEvent event) {
        Alert alert = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Are you sure you want to exit?"
                        + " Any unsaved changes will be deleted!",
                ButtonType.YES,
                ButtonType.CANCEL);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.CANCEL) {
            event.consume();
        }
    }

    /**
     * Gets the image created in the paint program.
     *
     * @return value the image painted.
     */
    public BufferedImage getAvatarImage() {
        return avatarImage;
    }

}
