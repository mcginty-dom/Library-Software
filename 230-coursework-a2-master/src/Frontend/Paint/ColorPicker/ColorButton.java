package Frontend.Paint.ColorPicker;

import Frontend.Paint.Tools.PaintCanvas.ColorPaletteDesignation;
import Frontend.Paint.Tools.ColorProperty;
import Frontend.Paint.Tools.PaintCanvas;
import javafx.scene.control.ToggleButton;
import javafx.scene.paint.Color;

/**
 * A color button is a toggleable button used to hold
 * and set color information. A toggle button holds a color designation and is
 * therefore bound to a paint canvas.
 * @author matt
 */
public class ColorButton extends ToggleButton {

    private ColorProperty color;
    private boolean selected;
    private final PaintCanvas paintCanvas;

    /**
     * Create a new ColorButton.
     * @param baseColor the color of the button
     * @param designation the designation of the button, to control the paint
     *                    canvas
     * @param paintCanvas the paint canvas to bind to this button.
     */
    public ColorButton(
            final Color baseColor,
            final ColorPaletteDesignation designation,
            final PaintCanvas paintCanvas) {
        super();
        this.paintCanvas = paintCanvas;
        color = new ColorProperty(baseColor, designation);

        paintCanvas.getActiveColorProperty().addListener((oldColor, newColor, d) -> {
            System.out.println(d);
            if (d == getColorProperty().getDesignation()) {
                getColorProperty().setColor(newColor);
            }
        });
        selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                select();
                pushColor();
            } else {
                deselect();
            }
        });
        color.addListener((oldColor, newColor, d) -> doStyle());

        doStyle();
    }


    /**
     * Get the color property associated with this button.
     * @return the color property bound to this node.
     */
    public ColorProperty getColorProperty(){
        return this.color;
    }

    /**
     * Select this button and set it as the active button.
     */
    public void select() {
        selected = true;
        doStyle();
    }

    /**
     * Set this button as explicitly not selected and not the active button.
     */
    public void deselect() {
        selected = false;
        doStyle();
    }

    /**
     * Add color and border to the button.
     */
    private void doStyle() {
        final double mod8Bit = 255.;

        int r = (int) Math.round(color.getColor().getRed() * mod8Bit);
        int g = (int) Math.round(color.getColor().getGreen() * mod8Bit);
        int b = (int) Math.round(color.getColor().getBlue() * mod8Bit);


        String cssString = "-fx-background-color: #"
                + String.format("%02x%02x%02x", r, g, b) + ";";

        if (selected) {
            String borderColor = "black";
            if (color.getColor().getBrightness() < (1 / 2f)) {
                borderColor = "white";
            }
            cssString += "-fx-border-color: " + borderColor + ";"
                    + " -fx-border-width: 2;"
                    + "-fx-background-insets: 1;";
        }

        setStyle(cssString);
    }

    /**
     * Safely switch colors with another color button, the other color
     * button will receive the color on this button.
     * @param colorButtonSecondary the button to switch with.
     */
    public void switchColors(final ColorButton colorButtonSecondary) {
        Color cacheColor = getColorProperty().getColor();
        ColorPaletteDesignation cacheDesignation = paintCanvas
                .getActiveButtonDesignation();

        paintCanvas.switchActiveTo(getColorProperty().getDesignation());
        paintCanvas.setActiveColor(
                colorButtonSecondary.getColorProperty().getColor()
        );

        paintCanvas.switchActiveTo(
                colorButtonSecondary.getColorProperty().getDesignation()
        );
        paintCanvas.setActiveColor(cacheColor);

        paintCanvas.switchActiveTo(cacheDesignation);
    }

    /**
     * Make the color of this button the active color of the
     * paint canvas the color is bound to.
     */
    private void pushColor() {
        paintCanvas.switchActiveTo(getColorProperty().getDesignation());
        paintCanvas.setActiveColor(getColorProperty().getColor());
    }
}
