package Frontend.Paint.Brushes;

import Frontend.Paint.Tools.PaintCanvas;
import com.sun.javafx.util.Utils;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Paint brush takes mouse inputs draws a brush trace
 * following the movement of the brush along the canvas.
 * @author matt
 */
public class PaintBrush extends Brush {

    /**
     * Default brush weight, higher values draw thicker lines.
     */
    public static final double DEFAULT_WEIGHT = 10;
    /**
     * Default opacity, a double between {0..1}, the transparency of the
     * brush.
     */
    public static final double DEFAULT_OPACITY = 1;

    private double lineWeight = DEFAULT_WEIGHT;
    private double opacity = DEFAULT_OPACITY;

    private Double xBack1Iteration;
    private Double yBack1Iteration;

    private Canvas temporaryLayer;

    /**
     * Take mouse input for left mouse down. Record this as the start
     * point of a smooth movement.
     * @param canvas the canvas calling this brush
     * @param x the x position of the mouse event
     * @param y the y position of the mouse event
     */
    @Override
    public void takeLeftMouseDown(
            final PaintCanvas canvas,
            final double x,
            final double y) {
        temporaryLayer = canvas.getTemporaryLayer();
        temporaryLayer.setOpacity(opacity);
        xBack1Iteration = x;
        yBack1Iteration = y;
        performLineDrawOps(canvas, x, y, getPaintColor(canvas));
    }

    /**
     * Take mouse input for left mouse up. Record this as the last
     * point of a smooth movement and commit the drawing.
     * @param canvas the canvas calling this brush
     * @param x the x position of the mouse event
     * @param y the y position of the mouse event
     */
    @Override
    public void takeLeftMouseUp(
            final PaintCanvas canvas,
            final double x,
            final double y) {
        performLineDrawOps(canvas, x, y, getPaintColor(canvas));
        onCancel(canvas, x, y);
        canvas.mergeTemporaryLayer();
        temporaryLayer.setOpacity(1d);
        temporaryLayer = null;
        canvas.commitToUndo(canvas.getActiveLayer());
    }

    /**
     * Take mouse input for left mouse drag. Draw smoothly
     * along the path of the drag.
     * @param canvas the canvas calling this brush
     * @param x the x position of the mouse event
     * @param y the y position of the mouse event
     */
    @Override
    public void takeLeftMouseDrag(
            final PaintCanvas canvas,
            final double x,
            final double y) {

        if (xBack1Iteration != null) {
            performLineDrawOps(canvas, x, y, getPaintColor(canvas));
        }
        xBack1Iteration = x;
        yBack1Iteration = y;
    }

    /**
     * Stop drawing
     * @param canvas the canvas calling this brush
     * @param x the x position at the time of cancel
     * @param y the y position at the time of cancel
     */
    @Override
    public void onCancel(
            final PaintCanvas canvas,
            final double x,
            final double y) {
        xBack1Iteration = null;
        yBack1Iteration = null;
    }

    /**
     * Get the color to draw with.
     * @param canvas the canvas that will be drawn on
     * @return a color to draw with
     */
    public Color getPaintColor(final PaintCanvas canvas) {
        return canvas.getActiveColor();
    }

    /**
     * Construct a smooth trace using the current mouse position and the last
     * mouse position, on the provided canvas in the given color.
     * @param canvas the canvas to draw on
     * @param x the mouse x position currently.
     * @param y the mouse y position currently
     * @param color the color to draw the trace in.
     */
    private void performLineDrawOps(
            final PaintCanvas canvas,
            final double x,
            final double y,
            final Color color) {

        double[] lastCanvasLocation = canvas.transformLocalToLayer(
                xBack1Iteration, yBack1Iteration
        );
        double[] canvasLocation = canvas.transformLocalToLayer(x, y);

        double lastCanvasX = lastCanvasLocation[0];
        double lastCanvasY = lastCanvasLocation[1];
        double canvasX = canvasLocation[0];
        double canvasY = canvasLocation[1];


        if (distance(
                lastCanvasX, lastCanvasY,canvasX, canvasY
        ) > lineWeight / 2) {
            drawLine(temporaryLayer, lastCanvasX, lastCanvasY,
                    canvasX, canvasY, color);
        } else {
            drawPoint(temporaryLayer, canvasX, canvasY, color);
        }
    }

    /**
     * Draw a point on the canvas in the given position and color.
     * @param canvas the canvas to draw on
     * @param x the x position
     * @param y the y position
     * @param color the color to draw the point in
     */
    private void drawPoint(
            final Canvas canvas,
            final double x,
            final double y,
            final Color color) {

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(color);
        gc.setStroke(color);
        gc.fillOval(
                x - lineWeight / 2,
                y - lineWeight / 2,
                lineWeight,
                lineWeight
        );
    }

    /**
     * Draw a line on the given canvas that connect the two points provided
     * in the form (x1, y1), (x2, y2) - in the given color.
     * @param canvas the canvas to draw onto
     * @param x1 point x1
     * @param y1 point y1
     * @param x2 point x2
     * @param y2 point y2
     * @param color the color of the line
     */
    private void drawLine(
            final Canvas canvas,
            final double x1, final double y1,
            final double x2, final double y2,
            Color color) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setStroke(color);
        gc.setLineWidth(lineWeight);
        gc.strokeLine(x1, y1, x2, y2);
    }

    /**
     * Set draw brush weight, lower weights produce thinner strokes. Should be
     * any double > 0
     * @param lineWeight the weight draw with.
     */
    public void setLineWeight(final double lineWeight) {
        this.lineWeight = lineWeight;
    }

    /**
     * Get the current draw stroke weight.
     * @return the current stroke weight of this brush.
     */
    public double getLineWeight() {
        return this.lineWeight;
   }

    /**
     * Set draw opacity.
     * @param opacity a value between {0..1} where 1 is fully opaque and
     *                0 is fully transparent.
     */
    public void setOpacity(final double opacity) {
        this.opacity = opacity;
    }

    /**
     * Get the current opacity level of this brush.
     * @return the current opacity level of the brush
     */
    public double getOpacity() {
        return this.opacity;
    }

    /**
     * Get the cursor associated with this brush.
     * @return PaintBrush cursor
     */
    public Cursor getCursor() {
        return Cursor.CROSSHAIR;
    }

    /**
     * To string.
     * @return this brush in String format.
     */
    @Override
    public String toString(){
        return "Paint Brush | Weight: " + lineWeight;
    }

}

