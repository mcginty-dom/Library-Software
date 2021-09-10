package Frontend.Paint.Toolbar;

import Frontend.Paint.Brushes.Brush;
import Frontend.Paint.Brushes.FillBrush;
import javafx.scene.control.*;

/**
 * A PaintProgramToolbar for handling the attributes of the
 * fill bucket.
 * @author matt
 */
public class FillToolbar extends PaintProgramToolbar {

    /**
     * Overfill default value. Overfill is a technique to
     * reduce antialiasing fragments.
     */
    private static final boolean OVERFILL_DEFAULT = true;
    private FillBrush brush;

    /**
     * Create a new toolbar with a new brush.
     */
    public FillToolbar() {
        this(null);
    }

    /**
     * Create a new toolbar with the given fill brush.
     * @param fillBrush a fill brush to assign to this toolbar
     */
    public FillToolbar(FillBrush fillBrush) {
        super();
        if (fillBrush == null) {
            fillBrush = new FillBrush();
        }
        this.brush = fillBrush;


        CheckBox overFill = new CheckBox("Overfill");
        overFill.setSelected(OVERFILL_DEFAULT);
        overFill.setOnAction(event ->
            brush.setOverFill(overFill.isSelected())
        );
        Tooltip overFillTooltip = new Tooltip("Reduces thin lines caused by anti aliasing");
        overFill.setTooltip(overFillTooltip);

        getChildren().addAll(overFill);
    }

    /**
     * Get the fill bucket associated with this toolbar.
     * @return this toolbar's fill bucket.
     */
    public Brush getBrush() {
        return brush;
    }
}
