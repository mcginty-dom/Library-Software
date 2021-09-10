package Frontend.Paint.Brushes;

import Frontend.Paint.Tools.PaintCanvas;
import javafx.scene.Cursor;
import javafx.scene.paint.Color;

/**
 * Eraser brush takes mouse inputs draws a brush trace
 * in the color of the original background.
 * @author matt
 */
public class EraserBrush extends PaintBrush {

    /**
     * Get the color of the eraser.
     * @param canvas the canvas this eraser is attached to.
     * @return the color the eraser will draw in.
     */
    @Override
    public Color getPaintColor(final PaintCanvas canvas) {
        return canvas.getBackgroundColor();
    }

    /**
     * Get the eraser cursor.
     * @return get the cursor associated with this brush.
     */
    public Cursor getCursor() {
        return Cursor.CROSSHAIR;
    }

    /**
     * To String
     * @return eraser as a String
     */
    @Override
    public String toString() {
        return "Eraser Brush | Weight: " + super.getLineWeight();
    }
}
