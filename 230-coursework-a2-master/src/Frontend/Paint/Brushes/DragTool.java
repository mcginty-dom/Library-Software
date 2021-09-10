package Frontend.Paint.Brushes;

import Frontend.Paint.Tools.PaintCanvas;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;

/**
 * Drag tool takes mouse inputs and uses it
 * to perform translations on the canvas area.
 * @author matt
 */
public class DragTool extends Brush {

    private Double lastX;
    private Double lastY;
    private boolean down;

    /**
     * Take mouse input for left mouse down. Record this as the start point of
     * the movement.
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
        lastY = y;
        down = true;
    }

    /**
     * Take mouse input for left mouse up. Take this as the end of a movement
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
        lastY = null;
        down = false;
    }

    /**
     * Take mouse input for left mouse drag. Translate the canvas
     * the delta since the last call to this method.
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
            canvas.translateX(lastX - x);
            canvas.translateY(lastY - y);
        }
        lastX = x;
        lastY = y;
    }

    /**
     * Get the drag hand cursor.
     * @return the cursor designed for this brush.
     */
    @Override
    public Cursor getCursor() {
        if (down) {
            return Cursor.CLOSED_HAND;
        }
        return Cursor.OPEN_HAND;
    }

    /**
     * To String.
     * @return string representation of the tool.
     */
    @Override
    public String toString(){
        return "Drag Tool |";
    }
}
