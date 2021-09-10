package Frontend.Paint.Tools;

import Frontend.Paint.Brushes.Brush;
import com.sun.javafx.util.Utils;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Stack;

/**
 * The Paint Canvas is the area that is drawn to in the paint program and the hub for handling drawing.
 * This acts as a managed canvas that fits into the node structure of a JavaFx hierarchy.
 *
 * @author matt
 */
public class PaintCanvas extends BorderPane {

    /**
     * Reference to the designations of colors on the palette. Colors can be either
     * on the Primary palette or Secondary palette
     * @author matt
     */
    public enum ColorPaletteDesignation {
        PRIMARY,
        SECONDARY
    }

    // Maximum and minimum zoom values as powers of 2 (where negative numbers are fractional)
    private static final float MAX_ZOOM = 10;
    private static final float MIN_ZOOM = -10;
    private static final float INV_ZOOM_RATE = 200;

    private static final Color DEFAULT_PRIMARY_COLOR = Color.BLACK;
    private static final Color DEFAULT_SECONDARY_COLOR = Color.WHITE;
    private static final Color DEFAULT_CANVAS_COLOR = Color.WHITE;
    private static final String BACKGROUND_COLOR = "#222222";

    private static final int UNDO_STACK_MAX_LENGTH = 100;

    private final Stack<PaintAction> undoStack = new Stack<>();
    private final Stack<PaintAction> redoStack = new Stack<>();

    // Layout of node
    private StackPane layerStack;

    // The main canvas
    private Color backgroundColor;
    private Canvas mainCanvas;
    private Canvas temporaryCanvas;

    // Drawing
    private ColorProperty activeColorProperty = new ColorProperty(DEFAULT_PRIMARY_COLOR, ColorPaletteDesignation.PRIMARY);
    private Color alternateColor = DEFAULT_SECONDARY_COLOR;
    private Brush activeBrush;

    // Transform tools
    private final Transform transform = new Transform();
    private double currentMouseXPosition;
    private double currentMouseYPosition;
    private boolean disableScroll;


    /**
     * Construct a new canvas with a given width, height, and the default background color.
     *
     * @param width the width of the canvas, must be a positive integer
     * @param height the height of the canvas, must be a positive integer
     */
    public PaintCanvas(int width, int height){
        this(width, height, DEFAULT_CANVAS_COLOR);
    }

    /**
     * Construct a new canvas with a given width, height, and chosen background color.
     *
     * @param width the width of the canvas, must be a positive integer
     * @param height the height of the canvas, must be a positive integer
     * @param backgroundColor the color to set as the background for the image. Immutable once set.
     */
    public PaintCanvas(int width, int height, Color backgroundColor){
        super();
        // Initialize containers
        setStyle("-fx-background-color: "+BACKGROUND_COLOR+";");

        if(backgroundColor == null){
            backgroundColor = DEFAULT_CANVAS_COLOR;
        }

        // Initialize event handlers
        widthProperty().addListener((observable, oldValue, newValue) -> centerVisibleCanvas());
        heightProperty().addListener((observable, oldValue, newValue) -> centerVisibleCanvas());
        addEventFilter(ScrollEvent.ANY, event -> {
            if(!disableScroll){
                this.handleScrollEvent(event);
            }
        });
        addEventFilter(MouseEvent.ANY, this::handleMouseEvent);

        clear(width, height, backgroundColor);
    }

    /**
     * Core functionality, when a MouseEvent is triggered, passes that information on to the current active brush.
     * @param e the mouse event on the canvas that triggers this method.
     */
    private void handleMouseEvent(MouseEvent e) {
        currentMouseXPosition = e.getX();
        currentMouseYPosition = e.getY();

        if(e.getButton() == MouseButton.PRIMARY) {
            // If the mouse was pressed down
            if (e.getEventType() == MouseEvent.MOUSE_PRESSED) {
                activeBrush.takeLeftMouseDown(this, e.getX(), e.getY());
            }
            // If the mouse is pressed AND moved
            if (e.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                activeBrush.takeLeftMouseDrag(this, e.getX(), e.getY());
            }
            // If the mouse was released
            if (e.getEventType() == MouseEvent.MOUSE_RELEASED) {
                activeBrush.takeLeftMouseUp(this, e.getX(), e.getY());
            }

        } else if(e.getButton() == MouseButton.SECONDARY) {
            // If the right mouse was pressed down
            if (e.getEventType() == MouseEvent.MOUSE_PRESSED) {
                activeBrush.takeRightMouseDown(this, e.getX(), e.getY());
            }
            // If the right mouse is pressed AND moved
            if (e.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                activeBrush.takeRightMouseDrag(this, e.getX(), e.getY());
            }
            // If the right mouse was released
            if (e.getEventType() == MouseEvent.MOUSE_RELEASED) {
                activeBrush.takeRightMouseUp(this, e.getX(), e.getY());
            }
        }
    }

    /**
     * The canvas has been scrolled on
     * @param e the Scroll Event that triggered this event
     */
    private void handleScrollEvent(ScrollEvent e){
        zoomOnPoint(e.getDeltaY() / INV_ZOOM_RATE, currentMouseXPosition, currentMouseYPosition);

    }

    /**
     * Disables canvas zooming from scrolling, this is used for touch support to ignore zoom gestures
     * @param scrollDisable
     */
    public void setScrollDisable(boolean scrollDisable){
        this.disableScroll = scrollDisable;
    }

    /**
     * Reset and replace the current working canvas. This should be treated as making a entirely new instance
     * of the canvas.
     * @param width The new width of the canvas, must be a positive integer
     * @param height The new height of the canvas, must be a positive integer
     * @param backgroundColor the clear color for the canvas
     */
    public void clear(int width, int height, Color backgroundColor) {
        assert width > 0 && height > 0;

        // Remove all working canvases.
        getChildren().clear();

        // Add new working canvases.
        this.mainCanvas = new Canvas(width,height);
        this.temporaryCanvas = new Canvas(width,height);
        this.layerStack = new StackPane(mainCanvas, temporaryCanvas);
        this.layerStack.setAlignment(Pos.TOP_LEFT);
        getChildren().add(layerStack);

        // Clear canvas
        GraphicsContext gc = mainCanvas.getGraphicsContext2D();
        gc.setFill(backgroundColor);
        this.backgroundColor = backgroundColor;
        gc.fillRect(0,0, mainCanvas.getWidth(), mainCanvas.getHeight());

        // Add the blank canvas to the undo stack
        undoStack.clear();
        redoStack.clear();
        undoStack.push(new PaintAction(mainCanvas.snapshot(null, null).getPixelReader(), mainCanvas));

        // Center the canvas once its loaded
        Platform.runLater(this::fitCanvasToScreen);
    }

    /*
  _____ _ _        _    __
 |  ___(_) | ___  (_)  / /__
 | |_  | | |/ _ \ | | / / _ \
 |  _| | | |  __/ | |/ / (_) |
 |_|   |_|_|\___| |_/_/ \___/
     */

    /**
     * Save the image on the active canvas to a file at a given location
     * @param path path to save the image to
     * @param format the image format, should be a string with an image extension (png, jpg, jpeg, gif...)
     * @throws IOException if there was an error saving the file
     */
    public void save(String path, String format) throws IOException {
        WritableImage image = getImage();
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        File outFile = new File(path);
        ImageIO.write(bufferedImage, format, outFile);


    }

    /**
     * Open an image file and set as the background for the active canvas
     * @param path path to the image to load
     * @throws IOException if there is an error loading the file
     */
    public void open(String path) throws IOException {
        BufferedImage image = ImageIO.read(new File(path));
        clear(image.getWidth(null), image.getHeight(null), DEFAULT_CANVAS_COLOR);
        GraphicsContext gc = mainCanvas.getGraphicsContext2D();
        gc.drawImage(SwingFXUtils.toFXImage(image, null), 0,0);
        commitToUndo(mainCanvas);
    }

    /**
     * Convert the current active canvas into a javafx Writable image in its current state.
     * @return the canvas as an image
     */
    public WritableImage getImage() {
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        return mainCanvas.snapshot(params, null);
    }
    /*
  _   _           _
 | | | |_ __   __| | ___     ___  _ __  ___
 | | | | '_ \ / _` |/ _ \   / _ \| '_ \/ __|
 | |_| | | | | (_| | (_) | | (_) | |_) \__ \
  \___/|_| |_|\__,_|\___/   \___/| .__/|___/
                                 |_|
     */

    /**
     * Save the given canvas's current state to the undo stack. This may displace an event if the stack is full.
     * Clears the redo stack.
     * @param canvas the canvas to save
     */
    public void commitToUndo(Canvas canvas){
        redoStack.clear();
        undoStack.push(new PaintAction(canvas.snapshot(null, null).getPixelReader(), canvas));
        if(undoStack.size() > UNDO_STACK_MAX_LENGTH){
            undoStack.remove(0);
        }
    }

    /**
     * Return to the last state that an undo was committed.
     * Adds the undone action to the redo stack.
     */
    public void undo(){
        if(undoStack.size() > 1) {
            redoStack.push(undoStack.pop());
            PaintAction paintAction = undoStack.peek();
            WritableImage result = new WritableImage(paintAction.getImage(), (int) paintAction.getCanvas().getWidth(), (int) paintAction.getCanvas().getHeight());
            GraphicsContext gc = paintAction.getCanvas().getGraphicsContext2D();
            gc.drawImage(result, 0, 0);
        }
    }

    /**
     * Return the image to the last undone state.
     */
    public void redo(){
        if(redoStack.size() > 0) {
            PaintAction paintAction = redoStack.pop();
            undoStack.push(paintAction);
            WritableImage result = new WritableImage(paintAction.getImage(), (int) paintAction.getCanvas().getWidth(), (int) paintAction.getCanvas().getHeight());
            GraphicsContext gc = paintAction.getCanvas().getGraphicsContext2D();
            gc.drawImage(result, 0, 0);
        }
    }

    /*
   ____      _   _
  / ___| ___| |_| |_ ___ _ __ ___
 | |  _ / _ \ __| __/ _ \ '__/ __|
 | |_| |  __/ |_| ||  __/ |  \__ \
  \____|\___|\__|\__\___|_|  |___/
     */

    /**
     * Return the layer that is currently selected to be drawn to.
     * @return the JavaFX canvas node with current drawing request.
     */
    public Canvas getActiveLayer() {
        // Method allows for native layer switching if implemented
        return mainCanvas;
    }

    /**
     * Acquire and clear the temporary layer. This layer should be cleared after being drawn to.
     * The temporary layer can be used to draw guide images that are not to be immediately committed to
     * the active layer.
     * @return the JavaFX canvas node representing the temporary layer
     */
    public Canvas getTemporaryLayer() {
        clearTemporaryLayer();
        return temporaryCanvas;
    }

    /**
     * Clear the current temporary layer buffer to full transparency
     */
    public void clearTemporaryLayer() {
        temporaryCanvas.getGraphicsContext2D().clearRect(0,0,temporaryCanvas.getWidth(), temporaryCanvas.getHeight());
    }

    /**
     * Commit the image on the temporary layer to the active canvas, adding it to the working image
     */
    public void mergeTemporaryLayer() {
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        GraphicsContext gc = getActiveLayer().getGraphicsContext2D();
        gc.drawImage(temporaryCanvas.snapshot(params, null), 0,0);
        temporaryCanvas.getGraphicsContext2D().clearRect(0,0,temporaryCanvas.getWidth(), temporaryCanvas.getHeight());
    }

    /**
     * Return the current working color. This could be either the primary or secondary color, and is the color that
     * has active draw request.
     * @return the color the the user has requested to draw with
     */
    public Color getActiveColor() {
        return activeColorProperty.getColor();
    }

    /**
     * Change the active color to the given value
     * @param color the new active color
     */
    public void setActiveColor(Color color) {
        activeColorProperty.setColor(color);
    }

    /**
     * Get the color currently NOT requested to draw with
     * @return
     */
    public Color getAlternateColor() {
        return alternateColor;
    }

    /**
     * Gets the color for the background of the canvas as set when it was made
     *
     * @return the color of the canvas' background
     */
    public Color getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * Get the property holder for the active color. Property can be used to listen to events when they're changed.
     * @return the active color property.
     */
    public ColorProperty getActiveColorProperty() {
        return activeColorProperty;
    }

    /**
     * Switch the active draw color to the primary color. If it is already the primary color, this will do nothing
     */
    public void switchActiveToPrimary(){
        if(activeColorProperty.getDesignation() != ColorPaletteDesignation.PRIMARY){
            activeColorProperty.setDesignation(ColorPaletteDesignation.PRIMARY);
            Color cache = activeColorProperty.getColor();
            activeColorProperty.setColor(alternateColor);
            alternateColor = cache;
        }
    }

    /**
     * Switch the active draw color to the secondary color. If it is already the secondary color, this will do nothing
     */
    public void switchActiveToSecondary(){
        if(activeColorProperty.getDesignation() != ColorPaletteDesignation.SECONDARY){
            activeColorProperty.setDesignation(ColorPaletteDesignation.SECONDARY);
            Color cache = activeColorProperty.getColor();
            activeColorProperty.setColor(alternateColor);
            alternateColor = cache;
        }
    }

    /**
     * Switch the active draw color to the specified color designation, primary or secondary.
     * If it is already this designation, this will do nothing.
     * @param colorPaletteDesignation the designation that is requested, Primary or Secondary
     */
    public void switchActiveTo(ColorPaletteDesignation colorPaletteDesignation){
        if(colorPaletteDesignation == ColorPaletteDesignation.PRIMARY){
            switchActiveToPrimary();
        } else {
            switchActiveToSecondary();
        }
    }

    /**
     * Get the current designation of the requested draw color, either Primary or Secondary
     * @return the designation of the current requested draw color
     */
    public ColorPaletteDesignation getActiveButtonDesignation() {
        return activeColorProperty.getDesignation();
    }

    public void setActiveBrush(Brush brush){
        if(this.activeBrush != null) {
            this.activeBrush.onCancel(this, 0, 0);
        }
        this.activeBrush = brush;
        layerStack.setCursor(activeBrush.getCursor());
    }

    /*
  _____                    _       _   _
 |_   _| __ __ _ _ __  ___| | __ _| |_(_) ___  _ __
   | || '__/ _` | '_ \/ __| |/ _` | __| |/ _ \| '_ \
   | || | | (_| | | | \__ \ | (_| | |_| | (_) | | | |
   |_||_|  \__,_|_| |_|___/_|\__,_|\__|_|\___/|_| |_|
     */

    /**
     * Center the canvas in the screen, that is move and scale the canvas so that it fits neatly in the surrounding
     * window.
     *
     * If the canvas or surrounding area has not been initialized will wait until initialization to run.
     */
    public void fitCanvasToScreen(){

        double maxCanvasWidth = getWidth()*0.95;
        double maxCanvasHeight = getHeight()*0.95;
        if(maxCanvasWidth == 0 && maxCanvasHeight == 0){
            // Keep checking for size parameters thread. This branch should only be called if the canvas hasn't been
            // loaded yet. This just puts off resizing until all other operations are complete
            Platform.runLater(this::fitCanvasToScreen);
            return;
        }

        double canvasWidth = mainCanvas.getWidth();
        double canvasHeight = mainCanvas.getHeight();
        if(canvasHeight == 0 || canvasWidth == 0){
            // This shouldn't ever happen but we want to avoid div by zero
            return;
        }

        double wRatio = canvasWidth / maxCanvasWidth;
        double hRatio = canvasHeight / maxCanvasHeight;

        double requiredScale = 1/Math.max(wRatio, hRatio);
        double requiredScrollVale = Math.log(requiredScale) / Math.log(2);

        zoom(requiredScrollVale-transform.getScrollValue());
        centerVisibleCanvas();
    }

    /**
     * Apply the zoom operation such that the given point (in window space NOT canvas space) remains in the
     * same window space
     * @param zoomRate the zoom factor, positive values increase the zoom level, negative will decrease it
     * @param x the x coordinate to focus on (in screen space)
     * @param y the y coordinate to focus on (in screen space)
     */
    public void zoomOnPoint(double zoomRate, double x, double y){
        double oldZoomLevel = Math.pow(2, transform.getScrollValue());

        double[] pointOnCanvasSpace = transformLocalToLayer(x, y);
        translate(-pointOnCanvasSpace[0], -pointOnCanvasSpace[1]);
        zoom(zoomRate);

        double newZoomLevel = Math.pow(2, transform.getScrollValue());
        double scale = newZoomLevel/oldZoomLevel;

        translate(pointOnCanvasSpace[0]*scale, pointOnCanvasSpace[1]*scale);
        centerVisibleCanvas();
    }

    /**
     * Scale the entire canvas (centered on 0,0).
     * @param zoomRate the rate to zoom, positive values increase scale, negative decrease it.
     */
    private void zoom(double zoomRate){
        transform.setScrollValue(transform.getScrollValue() + zoomRate);
        transform.setScrollValue(Utils.clamp(MIN_ZOOM, transform.getScrollValue(), MAX_ZOOM));
        double zoomLevel = Math.pow(2, transform.getScrollValue());
        layerStack.setScaleX(zoomLevel);
        layerStack.setScaleY(zoomLevel);
    }

    /**
     * Move the canvas so that it's center point is in the middle of the screen space.
     *
     * This assumes that either the width or height have a scale that makes the canvas size in window space
     * smaller than the relevant dimension of window size.
     * If this is not true this method will have no effect.
     */
    private void centerVisibleCanvas(){
        double zoomLevel = Math.pow(2, transform.getScrollValue());
        double scaledCanvasWidth = mainCanvas.getWidth() * zoomLevel;
        double scaledCanvasHeight = mainCanvas.getHeight()  * zoomLevel;

        if(scaledCanvasWidth < getWidth()){
            transform.setxTransform((getWidth()/2) - (scaledCanvasWidth/2));
            layerStack.setTranslateX(transform.getxTransform());
        }
        if(scaledCanvasHeight < getHeight()){
            transform.setyTransform((getHeight()/2) - (scaledCanvasHeight/2));
            layerStack.setTranslateY(transform.getyTransform());
        }
    }

    /**
     * Move the canvas x pixels on the x axis and y pixels on the y axis in window space.
     * Negative x values will move the canvas left, Negative y values will move the canvas up
     * @param x the movement in the x axis
     * @param y the movement in the y axis
     */
    public void translate(double x, double y) {
        translateX(x);
        translateY(y);
    }

    /**
     * Move the canvas x pixels on the x axis in window space.
     * Negative x values will move the canvas left.
     * @param x the movement in the x axis
     */
    public void translateX(double x) {
        transform.setxTransform(transform.getxTransform() - x);
        layerStack.setTranslateX(transform.getxTransform());
    }

    /**
     * Move the canvas y pixels on the y axis in window space.
     * Negative y values will move the canvas up.
     * @param y the movement in the y axis
     */
    public void translateY(double y) {
        transform.setyTransform(transform.getyTransform() - y);
        layerStack.setTranslateY(transform.getyTransform());
    }

    /**
     * Takes a co-ordinate value in local pixel space and converts it to canvas space of the transformed canvas.
     * This can move an input from actual pixel location on the screen to a position on the canvas that appears
     * beneath this location.
     * @param x the x coordinate in window space
     * @param y the y coordinate in window space
     * @return int array of form [canvasX, canvasY]
     */
    public double[] transformLocalToLayer(double x, double y) {
        double afterTranslateX = x - transform.getxTransform();
        double afterTranslateY = y - transform.getyTransform();

        double afterZoomX = afterTranslateX * (Math.pow(2, - transform.getScrollValue()));
        double afterZoomY = afterTranslateY * (Math.pow(2, - transform.getScrollValue()));

        return new double[] {afterZoomX, afterZoomY};
    }

}
