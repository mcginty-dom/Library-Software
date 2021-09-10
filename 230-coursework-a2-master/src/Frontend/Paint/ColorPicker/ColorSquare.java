package Frontend.Paint.ColorPicker;

import Frontend.Paint.Tools.PaintCanvas;
import com.sun.javafx.util.Utils;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

/**
 * Color square is a JavaFX node implementation of a typical HSB color
 * square for picking colors. It should be paired with a hue slider for
 * maximum usability.
 * The color square allows a user to choose a color by clicking within
 * the square to get a saturation and brightness value.
 * @author matt
 */
public class ColorSquare extends StackPane {


    private final PaintCanvas paintCanvas;
    private final Canvas saturationValueBox;
    private final Canvas crosshairs;

    private Color currentColor;

    /**
     * Create a new Color Square with a preferred width and height,
     * bound to the given PaintCanvas.
     * @param width the pref width of the Color Square
     * @param height the pref height of the Color Square
     * @param paintCanvas the canvas to bind the color square to
     */
    public ColorSquare(
            final int width,
            final int height,
            final PaintCanvas paintCanvas) {
        super();
        setAlignment(Pos.TOP_CENTER);


        this.paintCanvas = paintCanvas;
        this.currentColor = paintCanvas.getActiveColor();

        saturationValueBox = new Canvas(width, height);
        crosshairs = new Canvas(width, height);
        getChildren().addAll(saturationValueBox, crosshairs);


        crosshairs.addEventFilter(MouseEvent.ANY, this::takeMouseInput);
        widthProperty()
                .addListener((observable, oldValue, newValue) -> resizePane());


        paintSVBox();
        drawCrosshairs();
        paintCanvas.getActiveColorProperty().addListener((oldColor, newColor, d) -> {
            this.currentColor = newColor;
            if (Math.abs(oldColor.getHue() - newColor.getHue()) > 1) {
                paintSVBox();
            }
            drawCrosshairs();
        });

    }

    /**
     * Adjust color based on a mouse event. Use the coordinates of the mouse event to
     * calculate the new color
     * @param e a Mouse Event on the color square, not null
     */
    private void takeMouseInput(final MouseEvent e) {
        if (e.getButton() == MouseButton.PRIMARY) {
            if (e.getEventType() == MouseEvent.MOUSE_PRESSED
                    || e.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                selectColor(e.getX(), e.getY());
            }
        }
    }

    /**
     * given an x and y value in color square space, set the
     * crosshairs on the position and update the current color.
     * @param x the x position in color square space
     * @param y the y position in color square space
     */
    private void selectColor(double x, double y) {
        double width = crosshairs.getWidth();
        double height = crosshairs.getHeight();

        x = Utils.clamp(0, x, width);
        y = Utils.clamp(0, y, height);


        double saturation = x / crosshairs.getWidth();
        double brightness = 1 - y / crosshairs.getHeight();
        this.currentColor = Color.hsb(
                currentColor.getHue(), saturation, brightness
        );
        this.paintCanvas.setActiveColor(this.currentColor);
    }

    /**
     * When the panel resizes, resize the color square to match.
     */
    private void resizePane() {
        final int padding = 20;
        Platform.runLater(() -> {
            saturationValueBox.setWidth(getWidth() - padding);
            saturationValueBox.setHeight(getWidth() - padding);
            crosshairs.setWidth(getWidth() - padding);
            crosshairs.setHeight(getWidth() - padding);
            paintSVBox();
            drawCrosshairs();
        });
    }

    /**
     * Paint the Saturation/Value box to match the current color selection.
     */
    private void paintSVBox() {
        double width = saturationValueBox.getWidth();
        double height = saturationValueBox.getHeight();
        if (width <= 0 || height <= 0) {
            // Can't paint a non-existent box.
            return;
        }
        GraphicsContext gc = saturationValueBox.getGraphicsContext2D();
        WritableImage image = saturationValueBox.snapshot(null, null);
        PixelReader imageReader = image.getPixelReader();
        PixelWriter imageWriter = image.getPixelWriter();


        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                imageWriter.setColor(
                        x,
                        y,
                        Color.hsb(
                                currentColor.getHue(),
                                x / width,
                                (1 - y) / height)
                );
            }
        }

        WritableImage result = new WritableImage(
                imageReader, (int) width, (int) height
        );
        gc.drawImage(result, 0, 0);

    }

    /**
     * Redraw the crosshairs to match the current color selection
     */
    private void drawCrosshairs() {
        final int boxSize = 6;

        double width = crosshairs.getWidth();
        double height = crosshairs.getHeight();
        double x = currentColor.getSaturation() * width;
        double y = (1 - currentColor.getBrightness()) * height;

        GraphicsContext gc = crosshairs.getGraphicsContext2D();
        gc.clearRect(0, 0, width, height);

        gc.setStroke(Color.hsb(0, 0, Utils.clamp(0, y / height, 1)));
        gc.setLineWidth(1);
        gc.strokeLine(0, y, x - (boxSize / 2), y);
        gc.strokeLine(x + (boxSize / 2), y, width, y);
        gc.strokeLine(x, 0, x, y - (boxSize / 2));
        gc.strokeLine(x, y + (boxSize / 2), x, height);
        gc.strokeRect(x - (boxSize / 2), y - (boxSize / 2), boxSize, boxSize);
    }
}
