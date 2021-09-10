package Frontend.Paint.Brushes;

import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.image.Image;

import java.util.Arrays;

/**
 * Fill node is an essential element in the flood
 * fill algorithm and represents nothing more
 * than a pixel location on a 2D cartesian
 * surface.
 * @author matt
 */
public class FillNode {

    private int x;
    private int y;

    /**
     * Create a fill node at the given (x, y) coordinate.
     * @param x the x position
     * @param y the y position
     */
    public FillNode(final int x, final int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Equals method, compare two nodes.
     * @param obj other object to compare to
     * @return true if the nodes are similar, false otherwise.
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof FillNode) {
            FillNode other = (FillNode) obj;
            return other.x == this.x && other.y == this.y;
        }
        return false;
    }

    /**
     * Generate hash code.
     * @return the hash code for this node.
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(new int[]{x, y});
    }

    /**
     * Get the x value of this node.
     * @return the x value of this node
     */
    public int getX() {
        return x;
    }

    /**
     * Get the y value of this node.
     * @return the y value of this node
     */
    public int getY() {
        return y;
    }

}
