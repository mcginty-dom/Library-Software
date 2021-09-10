package Frontend.Paint.Toolbar;

import Frontend.Paint.Brushes.Brush;
import Frontend.Paint.Brushes.MagnifyingTool;
import Frontend.Paint.Tools.PaintCanvas;
import javafx.scene.control.Button;

/**
 * A PaintProgramToolbar for handling the attributes of the
 * magnifying tool.
 * @author matt
 */
public class MagnifyingToolbar extends PaintProgramToolbar {

    private MagnifyingTool tool;

    /**
     * Create a new toolbar with a new tool.
     */
    public MagnifyingToolbar(PaintCanvas canvas){
        this(canvas, null);
    }

    /**
     * Create a new toolbar with the given magnifying tool.
     * @param canvas the canvas to link this tool to.
     * @param magnifyingTool a magnifying tool to assign to this toolbar
     */
    public MagnifyingToolbar(PaintCanvas canvas, MagnifyingTool magnifyingTool) {
        super();
        if (magnifyingTool == null) {
            magnifyingTool = new MagnifyingTool();
        }
        this.tool = magnifyingTool;

        Button centerCanvas = new Button("Center Canvas");
        centerCanvas.setOnAction(event -> canvas.fitCanvasToScreen());
        getChildren().addAll(centerCanvas);
    }

    /**
     * Get the magnifying tool associated with this toolbar.
     * @return this toolbar's magnifying tool.
     */
    public Brush getBrush() {
        return tool;
    }
}
