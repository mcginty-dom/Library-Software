package Frontend.Paint.Toolbar;

import Frontend.Paint.Brushes.Brush;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;

/**
 * Paint program toolbars are a mechanism to control the parameters
 * of a brush through a javafx gui. All toolbars have a brush
 * which they own, and can alter the properties of.
 * <br>
 * A toolbar can be displayed like any other javafx node.
 * @author matt
 */
public abstract class PaintProgramToolbar extends HBox {

    /**
     * Create a new toolbar that is ready to be used.
     */
    public PaintProgramToolbar() {
        final int insets = 5;
        final int spacing = 5;
        setPrefHeight(getHeight());
        setPrefWidth(getWidth());
        setFillHeight(false);
        setPadding(new Insets(insets, insets, insets, insets));
        setSpacing(spacing);
        setAlignment(Pos.CENTER_LEFT);
    }

    /**
     * Get the brush that this toolbar controls.
     * @return a brush to be used with this toolbar.
     */
    public abstract Brush getBrush();
}
