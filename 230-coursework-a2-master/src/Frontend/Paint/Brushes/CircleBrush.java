package Frontend.Paint.Brushes;

import Frontend.Paint.Toolbar.CircleToolbar;
import Frontend.Paint.Tools.PaintCanvas;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Circle brush takes mouse inputs and constructs an
 * ellipse from two discrete points. Can be used to
 * draw ovals and circles.
 * @author matt
 */
public class CircleBrush extends Brush {

    /**
     * The default line weight of this brush.
     */
    public static final double DEFAULT_WEIGHT = 10;


    private double lineWeight = DEFAULT_WEIGHT;
    private boolean fill = CircleToolbar.DEFAULT_FILL;
    private PaintCanvas.ColorPaletteDesignation fillColor = CircleToolbar.DEFAULT_COLOR;
    private boolean drawCircle = CircleToolbar.DEFAULT_DRAW_CIRCLE;

    private Double xStart;
    private Double yStart;

    /**
     * Take mouse input for left mouse down. Record this as the start point of
     * the shape.
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
     * Take mouse input for left mouse up. Take this as the endpoint of
     * the shape.
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
            drawEllipse(canvas, x, y, canvas.getActiveColor(),
                    canvas.getAlternateColor());
            canvas.mergeTemporaryLayer();
            xStart = null;
            yStart = null;
            canvas.commitToUndo(canvas.getActiveLayer());
        }
    }

    /**
     * Take mouse input for right mouse up. Takes as a termination signal and
     * breaks the drawing of the current shape.
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
     * Take mouse input for left mouse drag. Draw the shape to the temporary
     * canvas as a preview.
     * @param canvas the canvas calling this brush
     * @param x the x position of the mouse event
     * @param y the y position of the mouse event
     */
    @Override
    public void takeLeftMouseDrag(
            final PaintCanvas canvas,
            final double x,
            final double y) {
        drawEllipse(canvas, x, y, canvas.getActiveColor(),
                canvas.getAlternateColor());
    }

    /**
     * Clear cache and wipe shape.
     * @param canvas the canvas calling this brush
     * @param x the x position at the time of cancel
     * @param y the y position at the time of cancel
     */
    @Override
    public void onCancel(PaintCanvas canvas, double x, double y) {
        clear(canvas);
        canvas.mergeTemporaryLayer();
        xStart = null;
        yStart = null;
    }

    /**
     * Draw the ellipse constructed from the start position of the shape
     * and the current position of the mouse to the provided
     * canvas.
     * @param canvas the canvas to draw to
     * @param x the current mouse x position
     * @param y the current mouse y position
     * @param activeColor the active color to draw with
     * @param secondaryColor the secondary color to draw with
     */
    private void drawEllipse(
            final PaintCanvas canvas,
            final double x,
            final double y,
            final Color activeColor,
            final Color secondaryColor) {

        double[] startLocation = canvas.transformLocalToLayer(xStart, yStart);
        double[] canvasLocation = canvas.transformLocalToLayer(x, y);

        GraphicsContext gc = canvas.getTemporaryLayer().getGraphicsContext2D();

        gc.setLineWidth(lineWeight);

        double canvasStartX = startLocation[0];
        double canvasStartY = startLocation[1];
        double canvasEndX = canvasLocation[0];
        double canvasEndY = canvasLocation[1];

        double width = Math.abs(canvasEndX - canvasStartX);
        double height = Math.abs(canvasEndY - canvasStartY);

        if (drawCircle) {
            width = 2 * distance(
                    canvasStartX, canvasStartY,
                    canvasEndX, canvasEndY
            );
            height = width;
            canvasStartX -= width / 2;
            canvasStartY -= height / 2;
        } else {
            if (canvasEndX < canvasStartX) {
                canvasStartX = canvasEndX;
            }

            if (canvasEndY < canvasStartY) {
                canvasStartY = canvasEndY;
            }
        }


        if (fill) {
            if (fillColor == PaintCanvas.ColorPaletteDesignation.PRIMARY) {
                gc.setFill(activeColor);
            } else {
                gc.setFill(secondaryColor);
            }
            gc.fillOval(canvasStartX, canvasStartY, width, height);
        }
        gc.setStroke(activeColor);
        gc.strokeOval(canvasStartX, canvasStartY, width, height);
    }

    /**
     * Clear the temporary canvas manually.
     * @param canvas the PaintCanvas to clear the temp layer of.
     */
    private void clear(final PaintCanvas canvas) {
        Canvas tempCanvas = canvas.getTemporaryLayer();
        tempCanvas.getGraphicsContext2D().clearRect(
                0, 0, tempCanvas.getWidth(), tempCanvas.getHeight()
        );
    }

    /**
     * Set the line weight to draw with.
     * @param lineWeight the new line weight, assumed > 0
     */
    public void setLineWeight(final double lineWeight) {
        this.lineWeight = lineWeight;
    }

    /**
     * Get the current line weight.
     * @return the brushes' current line weight
     */
    public double getLineWeight() {
        return this.lineWeight;
   }

    /**
     * Set fill flag, should the ellipse be filled.
     * @param fill the fill state.
     */
    public void setFill(final boolean fill) {
        this.fill = fill;
    }

    /**
     * Set the color designation to fill the ellipse with.
     * @param fillColor a color palette designation to
     *                  acquire a color from the paint canvas.
     */
    public void setFillColor(
            final PaintCanvas.ColorPaletteDesignation fillColor) {
        this.fillColor = fillColor;
    }

    /**
     * Set the draw circle flag. If true the brush will only draw circles, false
     * the brush will only draw ellipses
     * @param drawCircle the draw circle flag.
     */
    public void setDrawCircle(final boolean drawCircle) {
        this.drawCircle = drawCircle;
    }

    /**
     * Get the cursor associated with this brush
     * @return the brushes' cursor.
     */
    public Cursor getCursor() {
        return Cursor.CROSSHAIR;
    }

    /**
     * ToString.
     * @return String representation of circle brush.
     */
    @Override
    public String toString() {
        return "Circle Brush | Weight: " + lineWeight + ", Fill: " + fill
                + ", Fill Color: " + fillColor + ", Draw Circle: " + drawCircle;
    }
}

