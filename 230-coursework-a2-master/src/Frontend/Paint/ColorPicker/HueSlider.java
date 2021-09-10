package Frontend.Paint.ColorPicker;

import Frontend.Paint.Tools.PaintCanvas;
import com.sun.javafx.util.Utils;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * Hue slider is a Java FX Node color chooser that allows a user to select a hue
 * by scrubbing horizontally along a colored bar.
 * @author matt
 */
public class HueSlider extends StackPane {

    private final Canvas hueCanvas;
    private final Canvas hueSlider;
    private double hue;

    /**
     * Create a new hue slider with a preferred width and with
     * it's color linked to a given paint canvas.
     * @param width the preferred width of the hue slider.
     * @param paintCanvas the canvas to link the color through.
     */
    public HueSlider(final int width, final PaintCanvas paintCanvas) {
        super();

        final int setHeight = 20;
        final int hueDegrees = 360;

        setAlignment(Pos.TOP_CENTER);
        hueCanvas = new Canvas(width, setHeight);
        hueSlider = new Canvas(width, setHeight);
        hueSlider.addEventFilter(MouseEvent.ANY, event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                if (event.getEventType() == MouseEvent.MOUSE_DRAGGED
                        || event.getEventType() == MouseEvent.MOUSE_PRESSED) {
                    hue = Utils.clamp(
                            0,
                            hueDegrees * event.getX() / hueSlider.getWidth(),
                            hueDegrees
                    );
                    Color currentColor = paintCanvas.getActiveColor();
                    paintCanvas.setActiveColor(Color.hsb(
                            hue,
                            currentColor.getSaturation(),
                            currentColor.getBrightness())
                    );
                }
            }

        });

        widthProperty()
                .addListener((observable, oldValue, newValue) -> resizePane());
        paintCanvas.getActiveColorProperty()
                .addListener((oldColor, newColor, d) -> {
            hue = newColor.getHue();
            drawHueSlider();
        });


        getChildren().addAll(hueCanvas, hueSlider);
        drawHueSlider();
    }

    /**
     * Paint the hue slider, draws a hue bar on the canvas.
     */
    private void drawHueSlider() {
        final int hueDegrees = 360;

        double width = hueCanvas.getWidth();
        double height = hueCanvas.getHeight();
        if (width <= 0 || height <= 0) {
            return;
        }

        GraphicsContext canvasGraphicsContext = hueCanvas
                .getGraphicsContext2D();
        WritableImage canvasImage = hueCanvas.snapshot(null, null);
        PixelReader canvasImageReader = canvasImage.getPixelReader();
        PixelWriter canvasImageWriter = canvasImage.getPixelWriter();


        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                canvasImageWriter.setColor(
                        x, y, Color.hsb(hueDegrees * x / width, 1, 1)
                );
            }
        }

        GraphicsContext sliderGraphicsContext = hueSlider.getGraphicsContext2D();
        sliderGraphicsContext.clearRect(
                0, 0, hueSlider.getWidth(), hueSlider.getHeight());
        sliderGraphicsContext.setStroke(Color.BLACK);
        System.out.println(hue * hueSlider.getWidth());
        double hueLocation = hue * hueSlider.getWidth() / hueDegrees;
        sliderGraphicsContext.strokeLine(
                hueLocation, 0, hueLocation, hueSlider.getHeight());

        WritableImage result = new WritableImage(
                canvasImageReader, (int) width, (int) height
        );
        canvasGraphicsContext.drawImage(result, 0, 0);
    }

    /**
     * When the panel resizes, resize the hue slider to match.
     */
    private void resizePane() {
        final int padding = 20;

        Platform.runLater(() -> {
            hueCanvas.setWidth(getWidth() - padding);
            hueSlider.setWidth(getWidth() - padding);
            drawHueSlider();
        });

    }
}
