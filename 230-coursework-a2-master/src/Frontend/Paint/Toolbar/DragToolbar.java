package Frontend.Paint.Toolbar;

import Frontend.Paint.Brushes.Brush;
import Frontend.Paint.Brushes.DragTool;
import Frontend.Paint.Brushes.PaintBrush;
import Frontend.Paint.Tools.PaintCanvas;
import javafx.scene.control.Button;

/**
 * A PaintProgramToolbar for handling the attributes of the drag tool.
 * @author matt
 */
public class DragToolbar extends PaintProgramToolbar {

    private DragTool brush;

    /**
     * Create a new toolbar with a fresh brush.
     * @param canvas the canvas to link this brush to.
     */
    public DragToolbar(PaintCanvas canvas){
        this(canvas, null);
    }

    /**
     * Create a new toolbar with a specific brush.
     * @param canvas the canvas to link this brush to.
     * @param dragTool the brush to give to this toolbar
     */
    public DragToolbar(PaintCanvas canvas, DragTool dragTool) {
        super();
        if (dragTool == null) {
            dragTool = new DragTool();
        }
        this.brush = dragTool;

        Button centerCanvas = new Button("Center Canvas");
        centerCanvas.setOnAction(event -> canvas.fitCanvasToScreen());
        getChildren().addAll(centerCanvas);
    }

    /**
     * Get the drag tool associated with this toolbar.
     * @return
     */
    public Brush getBrush() {
        return brush;
    }
}
