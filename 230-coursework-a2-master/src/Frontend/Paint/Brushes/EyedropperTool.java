package Frontend.Paint.Brushes;

import Frontend.Paint.Tools.PaintCanvas;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 * Eyedropper tool takes mouse input and changes the
 * active color to the color at the pixel beneath a
 * click location.
 * @author matt
 */
public class EyedropperTool extends Brush {

    /**
     * Take mouse input for left mouse down.
     * @param canvas the canvas calling this brush
     * @param x the x position of the mouse event
     * @param y the y position of the mouse event
     */
    @Override
    public void takeLeftMouseDown(
            final PaintCanvas canvas,
            final double x,
            final double y) {

    }

    /**
     * Take mouse input for left mouse up. Take the color at this point
     * @param canvas the canvas calling this brush
     * @param x the x position of the mouse event
     * @param y the y position of the mouse event
     */
    @Override
    public void takeLeftMouseUp(
            final PaintCanvas canvas,
            final double x,
            final double y) {

        double[] canvasLocation = canvas.transformLocalToLayer(x, y);

        Canvas activeCanvas = canvas.getActiveLayer();
        WritableImage image = activeCanvas.snapshot(null, null);
        PixelReader imageReader = image.getPixelReader();
        canvas.setActiveColor(imageReader.getColor(
                (int) canvasLocation[0], (int) canvasLocation[1]
        ));
    }

    /**
     * Take mouse input for left mouse drag.
     * @param canvas the canvas calling this brush
     * @param x the x position of the mouse event
     * @param y the y position of the mouse event
     */
    @Override
    public void takeLeftMouseDrag(
            final PaintCanvas canvas,
            final double x,
            final double y) {

    }

    /**
     * Get the eyedropper cursor.
     * @return the cursor associated with this brush.
     */
    public Cursor getCursor() {
        return Cursor.CROSSHAIR;
    }

    /**
     * To String.
     * @return string representation of this tool.
     */
    @Override
    public String toString(){
        return "Eyedropper tool |";
    }
}
