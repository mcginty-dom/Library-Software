package Frontend.Paint.Tools;

import javafx.scene.paint.Color;

/**
 * A listener for the color property.
 * Interface is designed to facilitate callbacks around color changes
 * @author matt
 */
public interface ColorPropertyListener {

    /**
     * Receive an update that a color has changed, the old color, new color
     * and the color designation of the new color.
     * @param oldColor the color before the change
     * @param newColor the color after the change
     * @param designation the color designation associated with the changed color.
     */
    void update(Color oldColor, Color newColor, PaintCanvas.ColorPaletteDesignation designation);

}
