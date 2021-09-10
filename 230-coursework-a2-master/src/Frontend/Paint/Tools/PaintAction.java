package Frontend.Paint.Tools;

import javafx.scene.canvas.Canvas;
import javafx.scene.image.PixelReader;

/**
 * A paint action is a single instance in time, and records one
 * committed state of a canvas. A Paint Action can be used
 * to store a copy of a canvas for later user.
 * @author matt
 */
public class PaintAction {


    private PixelReader image;
    private Canvas canvas;

    /**
     * Create a new Paint action initialized with the
     * pixel reader and canvas it.
     * @param pixelReader a reader for a snapshot of a canvas.
     * @param canvas the canvas that this snapshot was taken from.
     */
    public PaintAction(PixelReader pixelReader, Canvas canvas) {
        this.image = pixelReader;
        this.canvas = canvas;
    }


    /**
     * Gets the saved state of an image.
     * @return the saved pixel reader.
     */
    public PixelReader getImage() {
        return image;
    }

    /**
     * Gets canvas
     * @return the canvas from the action.
     */
    public Canvas getCanvas() {
        return canvas;
    }

}
