package Frontend.Paint.Tools;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * A color property is a wrapper for a color and a designation
 * with the additional functionality of being able to assign listeners
 * to receive callbacks when the value of the color has changed.
 * @author matt
 */
public class ColorProperty {

    private final List<ColorPropertyListener> listeners = new ArrayList<>();
    private PaintCanvas.ColorPaletteDesignation designation;
    private Color color;

    /**
     * Create a new color property to wrap the given color and assign it
     * the given designation.
     * @param color the color of the color property.
     * @param designation the designation of the color property
     */
    public ColorProperty(Color color, PaintCanvas.ColorPaletteDesignation designation) {
        this.color = color;
        this.designation = designation;
    }

    /**
     * Get the color stored in this property.
     * @return the color of the property.
     */
    public Color getColor(){
        return color;
    }

    /**
     * Set the color of the color property
     * @param color the new color for the property.
     */
    public void setColor(Color color) {
        Color oldColor = this.color;
        this.color = color;
        for (ColorPropertyListener listener: listeners) {
            listener.update(oldColor, this.color, getDesignation());
        }
    }

    /**
     * Gets designation.
     *
     * @return value of designation
     */
    public PaintCanvas.ColorPaletteDesignation getDesignation() {
        return designation;
    }

    /**
     * Sets the value of designation.
     *
     * @param designation the new value
     */
    public void setDesignation(PaintCanvas.ColorPaletteDesignation designation) {
        this.designation = designation;
    }

    /**
     * Add a listener to the property, this listener will be called whenever the color
     * in the property is changed.
     * @param colorPropertyListener the listener to the color property.
     */
    public void addListener(ColorPropertyListener colorPropertyListener) {
        listeners.add(colorPropertyListener);
    }

    /**
     * Remove a given property listener from the listeners if it has already been added.
     * @param colorPropertyListener the listener to remove from the property.
     */
    public void removeListener(ColorPropertyListener colorPropertyListener) {
        listeners.remove(colorPropertyListener);
    }
}
