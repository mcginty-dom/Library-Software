package Frontend.Paint.Brushes;

import Frontend.Paint.Tools.PaintCanvas;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Line brush takes mouse inputs and constructs a
 * line drawing on the canvas from two discrete points.
 * @author matt
 */
public class LineBrush extends Brush {

    /**
     * The default line weight of this brush.
     */
    public static final double DEFAULT_WEIGHT = 10;


    private double lineWeight = DEFAULT_WEIGHT;

    private Double xStart;
    private Double yStart;

    /**
     * Take mouse input for left mouse down. Record this as the start point of
     * a new line.
     * @param canvas the canvas calling this brush
     * @param x the x position of the mouse event
     * @param y the y position of the mouse event
     */
    @Override
    public void takeLeftMouseDown(
            final PaintCanvas canvas,
            final double x,
            final double y) {
        xStart = x;
        yStart = y;
    }

    /**
     * Take mouse input for left mouse up. Create and
     * commit the line between the start point and this (x, y)
     * coordinate.
     * @param canvas the canvas calling this brush
     * @param x the x position of the mouse event
     * @param y the y position of the mouse event
     */
    @Override
    public void takeLeftMouseUp(
            final PaintCanvas canvas,
            final double x,
            final double y) {
        if (xStart != null && yStart != null) {
            drawLine(canvas, x, y, canvas.getActiveColor());
            canvas.mergeTemporaryLayer();
            xStart = null;
            yStart = null;
            canvas.commitToUndo(canvas.getActiveLayer());
        }
    }

    /**
     * Take mouse input for right mouse up. Treat this
     * as a cancel operation.
     * @param canvas the canvas calling this brush
     * @param x the x position of the mouse event
     * @param y the y position of the mouse event
     */
    @Override
    public void takeRightMouseUp(
            final PaintCanvas canvas,
            final double x,
            final double y) {
        onCancel(canvas, x, y);
    }

    /**
     * Take mouse input for left mouse drag. Draw a temporary guide line
     * for this line.
     * @param canvas the canvas calling this brush
     * @param x the x position of the mouse event
     * @param y the y position of the mouse event
     */
    @Override
    public void takeLeftMouseDrag(
            final PaintCanvas canvas,
            final double x,
            final double y) {
        drawLine(canvas, x, y, canvas.getActiveColor());
    }

    /**
     * Cancels all operations on this brush, clears un-drawn lines
     * and start positions.
     * @param canvas the canvas calling this brush
     * @param x the x position at the time of cancel
     * @param y the y position at the time of cancel
     */
    @Override
    public void onCancel(
            final PaintCanvas canvas,
            final double x,
            final double y) {
        clear(canvas);
        canvas.mergeTemporaryLayer();
        xStart = null;
        yStart = null;
    }

    /**
     * Draw a line on the canvas from the start point to the provided
     * (x, y) co-ordinates in the given color
     * @param canvas the canvas to draw to
     * @param x the mouse current x pos
     * @param y the mouse current y pos
     * @param activeColor the color to draw in.
     */
    private void drawLine(
            final PaintCanvas canvas,
            final double x,
            final double y,
            final Color activeColor) {
        double[] startLocation = canvas.transformLocalToLayer(xStart, yStart);
        double[] canvasLocation = canvas.transformLocalToLayer(x, y);

        GraphicsContext gc = canvas.getTemporaryLayer().getGraphicsContext2D();
        gc.setStroke(activeColor);
        gc.setLineWidth(lineWeight);
        gc.strokeLine(
                startLocation[0], startLocation[1],
                canvasLocation[0], canvasLocation[1]
        );
    }

    /**
     * Manually clean the temporary canvas.
     * @param canvas the canvas to clean
     */
    private void clear(final PaintCanvas canvas) {
        Canvas tempCanvas = canvas.getTemporaryLayer();
        tempCanvas.getGraphicsContext2D().clearRect(
                0, 0, tempCanvas.getWidth(), tempCanvas.getHeight()
        );
    }

    /**
     * Set draw line weight, lower weights produce thinner lines. Should be
     * any float > 0
     * @param lineWeight the weight draw with.
     */
    public void setLineWeight(final double lineWeight) {
        this.lineWeight = lineWeight;
    }

    /**
     * Get the current draw line weight.
     * @return the current line weight of this brush.
     */
    public double getLineWeight() {
        return this.lineWeight;
   }

    /**
     * Get the cursor for this brush.
     * @return the cursor associated with this brush.
     */
    public Cursor getCursor() {
        return Cursor.CROSSHAIR;
    }

    /**
     * To String.
     * @return the string representation of this brush.
     */
    @Override
    public String toString(){
        return "Line Brush | Weight: " + lineWeight;
    }

}

