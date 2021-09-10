package Frontend.Paint.Brushes;

import Frontend.Paint.Tools.PaintCanvas;
import javafx.scene.Cursor;

/**
 * A brush interprets sequences of mouse events to influence
 * some change in a canvas.
 * @author matt
 */
public abstract class Brush {

    /**
     * Take mouse input for left mouse down.
     * @param canvas the canvas calling this brush
     * @param x the x position of the mouse event
     * @param y the y position of the mouse event
     */
    public abstract void takeLeftMouseDown(
            PaintCanvas canvas, double x, double y);
    /**
     * Take mouse input for left mouse up.
     * @param canvas the canvas calling this brush
     * @param x the x position of the mouse event
     * @param y the y position of the mouse event
     */
    public abstract void takeLeftMouseUp(
            PaintCanvas canvas, double x, double y);
    /**
     * Take mouse input for left mouse dragged.
     * @param canvas the canvas calling this brush
     * @param x the x position of the mouse event
     * @param y the y position of the mouse event
     */
    public abstract void takeLeftMouseDrag(
            PaintCanvas canvas, double x, double y);

    /**
     * The actions to perform when this brush is disposed of.
     * @param canvas the canvas calling this brush
     * @param x the x position at the time of cancel
     * @param y the y position at the time of cancel
     */
    public void onCancel(
            final PaintCanvas canvas,
            final double x,
            final double y) {
    }

    /**
     * Take mouse input for right mouse down.
     * @param canvas the canvas calling this brush
     * @param x the x position of the mouse event
     * @param y the y position of the mouse event
     */
    public void takeRightMouseDown(
            final PaintCanvas canvas,
            final double x,
            final double y) {
    }

    /**
     * Take mouse input for right mouse up.
     * @param canvas the canvas calling this brush
     * @param x the x position of the mouse event
     * @param y the y position of the mouse event
     */
    public void takeRightMouseUp(
            final PaintCanvas canvas,
            final double x,
            final double y) {
    }

    /**
     * Take mouse input for right mouse dragged.
     * @param canvas the canvas calling this brush
     * @param x the x position of the mouse event
     * @param y the y position of the mouse event
     */
    public void takeRightMouseDrag(
            final PaintCanvas canvas,
            final double x,
            final double y) {
    }


    /**
     * Get the cursor associated with this brush.
     * @return a Cursor representing this brush
     */
    public Cursor getCursor() {
        return Cursor.DEFAULT;
    }

    /**
     * Euclidean distance calculator, returns the distance between two
     * points (x1, y1) and (x2, y2).
     * @param x1 point x1
     * @param y1 point y1
     * @param x2 point x2
     * @param y2 point y2
     * @return
     */
    public static double distance(
            final double x1,
            final double y1,
            final double x2,
            final double y2) {
        return Math.sqrt(
                (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)
        );
    }

}
