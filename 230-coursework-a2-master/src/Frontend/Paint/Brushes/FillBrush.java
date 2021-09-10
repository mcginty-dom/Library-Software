package Frontend.Paint.Brushes;

import Frontend.Paint.Tools.PaintCanvas;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Fill brush takes mouse inputs will flood fill
 * ann area when used.
 * @author matt
 */
public class FillBrush extends Brush {

    private Double xPos;
    private Double yPos;
    private boolean overFill = true;

    /**
     * Take mouse input for left mouse down. Record this as the start point of
     * the fill operation.
     * @param canvas the canvas calling this brush
     * @param x the x position of the mouse event
     * @param y the y position of the mouse event
     */
    @Override
    public void takeLeftMouseDown(
            final PaintCanvas canvas,
            final double x,
            final double y) {
        xPos = x;
        yPos = y;
    }

    /**
     * Take mouse input for left mouse up. Do the fill operation.
     * @param canvas the canvas calling this brush
     * @param x the x position of the mouse event
     * @param y the y position of the mouse event
     */
    @Override
    public void takeLeftMouseUp(
            final PaintCanvas canvas,
            final double x,
            final double y) {
        doFill(canvas);
        canvas.commitToUndo(canvas.getActiveLayer());
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
     * Cancel fill operation.
     * @param canvas the canvas calling this brush
     * @param x the x position at the time of cancel
     * @param y the y position at the time of cancel
     */
    @Override
    public void onCancel(
            final PaintCanvas canvas,
            final double x,
            final double y) {
        xPos = null;
        yPos = null;
    }

    /**
     * Perform the fill operation on the supplied canvas.
     * @param canvas the canvas to perform the fill on.
     */
    private void doFill(
            final PaintCanvas canvas) {
        Canvas drawCanvas = canvas.getActiveLayer();
        double[] canvasLocation = canvas.transformLocalToLayer(xPos, yPos);
        int canvasX = (int) canvasLocation[0];
        int canvasY = (int) canvasLocation[1];
        if (canvasX < 0 || canvasX >= drawCanvas.getWidth()
                || canvasY < 0 || canvasY >= drawCanvas.getHeight()) {
            return;
        }

        GraphicsContext gc = drawCanvas.getGraphicsContext2D();
        WritableImage image = drawCanvas.snapshot(null, null);
        PixelReader imageReader = image.getPixelReader();

        final double tolerance = 0.01d;
        Color foundColor = imageReader.getColor(canvasX, canvasY);

        Color newColor = canvas.getActiveColor();

        // Don't fill if already filled
        if (!withinTolerance(foundColor, newColor, tolerance)) {
            gc.setFill(newColor);
            floodFill(
                    new FillNode(canvasX, canvasY),
                    image,
                    foundColor,
                    newColor,
                    tolerance
            );
        }

        WritableImage result = new WritableImage(
                imageReader,
                (int) drawCanvas.getWidth(),
                (int) drawCanvas.getHeight()
        );
        gc.drawImage(result, 0,0);
        System.out.println(imageReader.getColor(canvasX, canvasY));
    }

    /**
     * Flood fill algorithm.
     * Starting at the start node coordinates do a flood read on all
     * connected coordinates in the image reader that are within a tolerance
     * shade of targetColor and replacing them with the new color.
     * <br>
     * The output of the fill is written to the imageWriter.
     * @param startNode the node to start filling at
     * @param image an image to perform flood fill on
     * @param targetColor the color of the origin of the fill
     * @param newColor the color to replace with in the fill
     * @param tolerance the shade allowance to include different colors
     *                  in the fill
     */
    private void floodFill(
            final FillNode startNode,
            final WritableImage image,
            final Color targetColor,
            final Color newColor,
            final double tolerance) {


        final PixelReader imageReader = image.getPixelReader();
        final PixelWriter imageWriter = image.getPixelWriter();
        final int maxWidth = (int) image.getWidth();
        final int maxHeight = (int) image.getHeight();

        /*
        Queue based flood fill operation.
        > Take a queue and enter the start point
        > While the queue isn't empty:
            > Fet the point at the head of the queue
            > If the pixel at that point is not already the new color:
                > If the pixel at that point is similar to the target color:
                    > Change the point to the new color
                    > Add all the neighbours to the queue
         */

        final Queue<FillNode> queue = new LinkedList<>();
        queue.add(startNode);

        while (!queue.isEmpty()) {
            FillNode node = queue.poll();

            int x = node.getX();
            int y = node.getY();

            boolean outOfBounds = (
                    x < 0 || x >= maxWidth
                    || y < 0 || y >= maxHeight
            );
            if (!outOfBounds) {
                Color pixelColor = imageReader.getColor(x, y);
                boolean alreadyFilled = withinTolerance(
                        pixelColor, newColor, tolerance);
                boolean wrongColor = !withinTolerance(
                        pixelColor, targetColor, tolerance);
                if (overFill || !wrongColor) {
                    imageWriter.setColor(x, y, newColor);
                }
                if (!alreadyFilled && !wrongColor) {
                    queue.add(new FillNode(x, y + 1));
                    queue.add(new FillNode(x, y - 1));
                    queue.add(new FillNode(x + 1, y));
                    queue.add(new FillNode(x - 1, y));
                }
            }
        }
    }

    /**
     * Check if two colors are similar to each other. This will return true if
     * the colors have a shade difference no greater than the provided
     * tolerance.
     * @param primaryColor first color to compare
     * @param secondaryColor second color to compare
     * @param tolerance the allowable shade difference, should be >= 0
     * @return true if the colors are similar, false otherwise.
     */
    private static boolean withinTolerance(
            final Color primaryColor,
            final Color secondaryColor,
            final double tolerance) {
        double delta = 0;
        delta += Math.abs(primaryColor.getRed() - secondaryColor.getRed());
        delta += Math.abs(primaryColor.getGreen() - secondaryColor.getGreen());
        delta += Math.abs(primaryColor.getBlue() - secondaryColor.getBlue());
        return delta < tolerance;
    }

    /**
     * Get the fill brush cursor.
     * @return the cursor associated with this brush
     */
    public Cursor getCursor() {
        Image fillbucket = new Image(
                "file:src/res/images/ui/brush_Bucket_Cursor.png");
        return new ImageCursor(fillbucket);
    }

    /**
     * Set the overfill property.
     * If true, flood fill will extend one pixel past its usual boundary,
     * this is partially destructive, but will remove ugly lines
     * caused by anti-aliasing.
     * @param overFill the overfill flag.
     */
    public void setOverFill(
            final boolean overFill) {
        this.overFill = overFill;
    }

    /**
     * To String.
     * @return Get the fill brush as a string.
     */
    @Override
    public String toString(){
        return "Fill Brush | Overfill: " + overFill;
    }
}
