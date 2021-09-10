package Frontend.Paint.Toolbar;

import Frontend.Paint.Brushes.Brush;
import Frontend.Paint.Brushes.EraserBrush;

/**
 * A PaintProgramToolbar for handling the attributes of an eraser.
 * @author matt
 */
public class EraserToolbar extends BrushToolbar {

    private EraserBrush brush;

    /**
     * Create a new toolbar with a new brush.
     */
    public EraserToolbar() {
        this(null);
    }

    /**
     * Create a new toolbar with the given eraser.
     * @param eraserBrush an eraser to assign to this toolbar
     */
    public EraserToolbar(EraserBrush eraserBrush) {
        super();
        if (eraserBrush == null) {
            eraserBrush = new EraserBrush();
        }
        this.brush = eraserBrush;

    }

    /**
     * Get the eraser associated with this toolbar.
     * @return this toolbar's eraser
     */
    public Brush getBrush() {
        return brush;
    }

    /**
     * Don't add opacity components.
     * Overrides the brush toolbar to
     * ignore the addition of opacity components.
     */
    @Override
    protected void createOpacityComponents() {
        return;
    }
}
