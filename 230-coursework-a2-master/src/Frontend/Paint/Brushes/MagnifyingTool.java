package Frontend.Paint.Brushes;

import Frontend.Paint.Tools.PaintCanvas;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.image.Image;

/**
 * Magnifying tool takes mouse inputs and uses it
 * to perform zoom operations on the canvas area.
 * @author matt
 */
public class MagnifyingTool extends Brush {

    /**
     * Multiplier for zoom rate, lowering makes zooming slower,
     * increasing makes zoom faster.
     */
    private static final double ZOOM_SCALER = 40;


    private Double firstX;
    private Double firstY;

    private Double lastX;

    /**
     * Take mouse input for left mouse down. Record this as the start
     * point of a swipe.
     * @param canvas the canvas calling this brush
     * @param x the x position of the mouse event
     * @param y the y position of the mouse event
     */
    @Override
    public void takeLeftMouseDown(
            final PaintCanvas canvas,
            final double x,
            final double y) {
        lastX = x;
        firstX = x;
        firstY = y;
    }

    /**
     * Take mouse input for left mouse up. Record this as the end
     * point of a swipe.
     * @param canvas the canvas calling this brush
     * @param x the x position of the mouse event
     * @param y the y position of the mouse event
     */
    @Override
    public void takeLeftMouseUp(
            final PaintCanvas canvas,
            final double x,
            final double y) {
        lastX = null;

    }

    /**
     * Take mouse input for left mouse drag. Use distance
     * to zoom.
     * @param canvas the canvas calling this brush
     * @param x the x position of the mouse event
     * @param y the y position of the mouse event
     */
    @Override
    public void takeLeftMouseDrag(
            final PaintCanvas canvas,
            final double x,
            final double y) {

        if (lastX != null) {
            canvas.zoomOnPoint((x - lastX) / ZOOM_SCALER, firstX, firstY);
        }
        lastX = x;
    }

    /**
     * Get the cursor associated with this brush.
     * @return Get the cursor for this brush
     */
    public Cursor getCursor() {
        return Cursor.E_RESIZE;
    }

    /**
     * To String.
     * @return string output representing this brush.
     */
    @Override
    public String toString(){
        return "Magnifying Tool |";
    }
}
