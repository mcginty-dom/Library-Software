package Frontend.Paint.Toolbar;

import Frontend.Paint.Brushes.Brush;
import Frontend.Paint.Brushes.EyedropperTool;
import javafx.scene.control.Label;

/**
 * A PaintProgramToolbar for handling the attributes of an eyedropper.
 * @author matt
 */
public class EyedropperToolbar extends PaintProgramToolbar {

    private EyedropperTool brush;

    /**
     * Create a new toolbar with a new brush.
     */
    public EyedropperToolbar(){
        this(null);
    }

    /**
     * Create a new toolbar with the given eyedropper.
     * @param eyedropperTool an eyedropper to assign to this toolbar
     */
    public EyedropperToolbar(EyedropperTool eyedropperTool) {
        super();
        if (eyedropperTool == null) {
            eyedropperTool = new EyedropperTool();
        }
        this.brush = eyedropperTool;
        getChildren().add(new Label("Eye dropper"));
    }

    /**
     * get the eyedropper associated with this toolbar.
     * @return this toolbar's eyedropper.
     */
    public Brush getBrush() {
        return brush;
    }
}
