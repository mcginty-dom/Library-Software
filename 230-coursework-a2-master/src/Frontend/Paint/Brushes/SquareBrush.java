package Frontend.Paint.Brushes;

import Frontend.Paint.Toolbar.SquareToolbar;
import Frontend.Paint.Tools.PaintCanvas.ColorPaletteDesignation;
import Frontend.Paint.Tools.PaintCanvas;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Square brush takes mouse inputs and constructs a
 * rectangle from two discrete points. Can be used to
 * draw rectangles and squares.
 * @author matt
 */
public class SquareBrush extends Brush {

    /**
     * The default line weight of this brush.
     */
    public static final double DEFAULT_WEIGHT = 10;


    private double lineWeight = DEFAULT_WEIGHT;
    private boolean fill = SquareToolbar.DEFAULT_FILL;
    private ColorPaletteDesignation fillColor = SquareToolbar.DEFAULT_COLOR;
    private boolean drawSquare = SquareToolbar.DEFAULT_DRAW_SQUARE;

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
            drawRect(
                    canvas, x, y,
                    canvas.getActiveColor(),
                    canvas.getAlternateColor()
            );
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
        drawRect(canvas, x, y, canvas.getActiveColor(), canvas.getAlternateColor());
    }

    /**
     * Clear cache and wipe shape.
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
     * Draw the rectangle constructed from the start position of the shape
     * and the current position of the mouse to the provided
     * canvas.
     * @param canvas the canvas to draw to
     * @param x the current mouse x position
     * @param y the current mouse y position
     * @param activeColor the active color to draw with
     * @param secondaryColor the secondary color to draw with
     */
    private void drawRect(
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

        if (drawSquare) {
            width = 2 * Math.max(width, height);
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
            if (fillColor == ColorPaletteDesignation.PRIMARY) {
                gc.setFill(activeColor);
            } else {
                gc.setFill(secondaryColor);
            }
            gc.fillRect(canvasStartX, canvasStartY, width, height);
        }
        gc.setStroke(activeColor);
        gc.strokeRect(canvasStartX, canvasStartY, width, height);
    }

    /**
     * Clear the temporary canvas manually.
     * @param canvas the PaintCanvas to clear the temp layer of.
     */
    private void clear(PaintCanvas canvas) {
        Canvas tempCanvas = canvas.getTemporaryLayer();
        tempCanvas.getGraphicsContext2D().clearRect(0,0,tempCanvas.getWidth(),tempCanvas.getHeight());
    }


    /**
     * Set draw line weight, lower weights produce thinner lines. Should be
     * any double > 0
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
     * Get the cursor associated with this brush.
     * @return SquareBrush cursor
     */
    public Cursor getCursor() {
        return Cursor.CROSSHAIR;
    }

    /**
     * Set fill flag, should the rectangle be filled.
     * @param fill the fill state.
     */
    public void setFill(final boolean fill) {
        this.fill = fill;
    }

    /**
     * Set the color designation to fill the rectangle with.
     * @param fillColor a color palette designation to
     *                  acquire a color from the paint canvas.
     */
    public void setFillColor(
            final PaintCanvas.ColorPaletteDesignation fillColor) {
        this.fillColor = fillColor;
    }

    /**
     * Set the draw square flag. If true the brush will only draw squares, false
     * the brush will only draw rectangles
     * @param drawSquare the draw circle flag.
     */
    public void setDrawSquare(final boolean drawSquare) {
        this.drawSquare = drawSquare;
    }

    /**
     * To String.
     * @return the brush in String format.
     */
    @Override
    public String toString() {
        return "Square Brush | Weight: " + lineWeight + ", Fill: " + fill
                + ", Fill Color: " + fillColor + ", Draw Square: " + drawSquare;
    }
}

