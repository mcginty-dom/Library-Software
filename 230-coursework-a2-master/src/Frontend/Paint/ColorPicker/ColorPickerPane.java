package Frontend.Paint.ColorPicker;

import Frontend.Paint.Tools.PaintCanvas.ColorPaletteDesignation;
import Frontend.Paint.Tools.PaintCanvas;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

/**
 * The ColorPicker pane is a collection of nodes used for selecting colors,
 * it is designed to be stored as a sub-node in a paint program.
 * The color picker pane holds:
 * <ol>
 *     <li>Color selector buttons</li>
 *     <li>A color square</li>
 *     <li>A hue slider</li>
 *     <li>Direct color input fields</li>
 * </ol>
 * These are all linked by the pane.
 */
public class ColorPickerPane extends VBox {


    /**
     * The largest width that this node will occupy.
     */
    private final static double MAX_WIDTH = 600;
    /**
     * The smallest width that this node will occupy.
     */
    private final static double MIN_WIDTH = 100;
    /**
     * The default size for the pane.
     */
    private final static int DEFAULT_SIZE = 150;

    private final ColorSquare colorSquare;
    private final HueSlider hueSlider;
    private final DirectInputField directInputField;

    /**
     * Create a new pane linked to the specified canvas.
     * @param paintCanvas a canvas to link all nodes through.
     */
    public ColorPickerPane(final PaintCanvas paintCanvas) {
        super();

        setAlignment(Pos.TOP_CENTER);
        setMaxWidth(MAX_WIDTH);
        setMinWidth(MIN_WIDTH);

        final int insets = 10;
        final int spacing = 10;

        setPadding(new Insets(insets, insets, insets, insets));
        setSpacing(spacing);

        createColorButtons(paintCanvas);

        colorSquare = new ColorSquare(DEFAULT_SIZE, DEFAULT_SIZE, paintCanvas);
        getChildren().add(colorSquare);

        hueSlider = new HueSlider(DEFAULT_SIZE, paintCanvas);
        getChildren().add(hueSlider);

        directInputField = new DirectInputField(paintCanvas);
        getChildren().add(directInputField);


    }

    /**
     * Handle creating and adding the color buttons to the pane.
     * @param paintCanvas the canvas to link the color buttons though.
     */
    private void createColorButtons(PaintCanvas paintCanvas) {
        final int switchGraphicSize = 15;
        final int spacing = 10;

        Label primaryLabel = new Label("Primary");
        Label secondaryLabel = new Label("Secondary");


        ToggleGroup colorPickerGroup = new ToggleGroup();
        ColorButton colorButtonPrimary = new ColorButton(
                paintCanvas.getActiveColor(),
                ColorPaletteDesignation.PRIMARY,
                paintCanvas
        );
        ColorButton colorButtonSecondary = new ColorButton(
                paintCanvas.getAlternateColor(),
                ColorPaletteDesignation.SECONDARY,
                paintCanvas
        );
        ImageView switchGraphic = new ImageView(
                "file:src/res/images/ui/switchIcon.png");
        switchGraphic.setPreserveRatio(true);
        switchGraphic.setFitWidth(switchGraphicSize);
        switchGraphic.setFitHeight(switchGraphicSize);
        Button switchButton = new Button();
        switchButton.setGraphic(switchGraphic);
        switchButton.setOnAction(event -> {
            colorButtonPrimary.switchColors(colorButtonSecondary);
        });

        colorButtonPrimary.setToggleGroup(colorPickerGroup);
        colorButtonSecondary.setToggleGroup(colorPickerGroup);
        colorButtonPrimary.setSelected(true);


        HBox colorPickerBox = new HBox(
                new VBox(primaryLabel, colorButtonPrimary),
                switchButton,
                new VBox(secondaryLabel, colorButtonSecondary));
        colorPickerBox.setSpacing(spacing);
        colorPickerBox.setAlignment(Pos.CENTER);
        colorButtonPrimary.setPrefWidth(Integer.MAX_VALUE);
        colorButtonSecondary.setPrefWidth(Integer.MAX_VALUE);

        getChildren().addAll(colorPickerBox);
    }

}
