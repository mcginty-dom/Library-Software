package Frontend.Paint.Toolbar;

import Frontend.Paint.Brushes.*;
import Frontend.Paint.Tools.PaintCanvas.ColorPaletteDesignation;
import Frontend.Paint.Tools.PaintCanvas;
import javafx.geometry.Orientation;
import javafx.scene.control.*;

/**
 * A PaintProgramToolbar for handling the attributes of the square brush.
 * @author matt
 */
public class SquareToolbar extends PaintProgramToolbar {

    /**
     * Default line weight options.
     */
    private static final String[] DEFAULT_WEIGHTS = new String[]{
            "1", "2", "4", "6", "8", "10", "12", "14", "16", "18", "22", "26", "30",
            "36", "42", "52", "64", "78", "92", "120"
    };
    /**
     * Exponent to raise the value of the slider to for logarithmic sliding.
     */
    private static final double SLIDER_EXP_BASE = 1.06d; // This was calculated experimentally
    /**
     * Lowest value for the weight slider.
     */
    private static final double SLIDER_MIN = 0.01d;
    /**
     * Highest value for the height slider.
     */
    private static final double SLIDER_MAX = 100d;
    /**
     * Default draw filled value.
     */
    public static final boolean DEFAULT_FILL = false;
    /**
     * Default draw color designation.
     */
    public static final ColorPaletteDesignation DEFAULT_COLOR = ColorPaletteDesignation.PRIMARY;
    /**
     * Default draw square flag.
     */
    public static final boolean DEFAULT_DRAW_SQUARE = false;


    private SquareBrush brush;

    private Slider weightSlider;
    private ComboBox lineWeightComboBox;

    private CheckBox fillShape;
    private ToggleButton fillColorToggle;
    private ColorPaletteDesignation fillColor = DEFAULT_COLOR;
    private CheckBox drawCircle;

    /**
     * Create a new toolbar with a new tool.
     */
    public SquareToolbar(){
        this(null);
    }

    /**
     * Create a new toolbar with the given square brush.
     * @param squareBrush a squareBrush to assign to this toolbar
     */
    public SquareToolbar(SquareBrush squareBrush) {
        super();
        if (squareBrush == null) {
            squareBrush = new SquareBrush();
        }
        this.brush = squareBrush;
        createComponents();


    }

    /**
     * Create and add all components for this toolbar.
     */
    private void createComponents() {
        createLineWeightComponents();
        getChildren().add(new Separator(Orientation.VERTICAL));
        createFillComponents();
        getChildren().add(new Separator(Orientation.VERTICAL));
        createDrawComponents();
    }

    /**
     * Create and add all components concerned with the brush's line weight.
     */
    private void createLineWeightComponents() {

        // Line weight
        Label lineWeightLabel = new Label("Line weight:");

        weightSlider = new Slider();
        weightSlider.setMin(SLIDER_MIN);
        weightSlider.setMax(SLIDER_MAX);
        weightSlider.setValue(Math.log(PaintBrush.DEFAULT_WEIGHT)/Math.log(SLIDER_EXP_BASE));
        weightSlider.valueProperty().addListener(e->{

            double newValue = Math.pow(SLIDER_EXP_BASE, weightSlider.getValue());
            double sliderMaxValue = Math.pow(SLIDER_EXP_BASE, SLIDER_MAX);
            double epsilon = 0.01d;

            // Important check, or setting the property would create a loop
            // Will ignore values outside slider range as these cannot be presented
            System.out.println(weightSlider.getValue());
            boolean valueOverSliderMax = brush.getLineWeight() > sliderMaxValue;
            boolean sliderAtMax = weightSlider.getValue() == SLIDER_MAX;
            boolean brushAlreadyAtSlider = Math.abs(newValue - brush.getLineWeight()) < epsilon;

            boolean shouldUpdate = (!valueOverSliderMax || !sliderAtMax) && !brushAlreadyAtSlider;

            if(shouldUpdate) {
                System.out.println("Setting brush weight from slider");
                setBrushWeight(newValue);
            }
        });

        lineWeightComboBox = new ComboBox();
        lineWeightComboBox.getItems().addAll((Object[]) DEFAULT_WEIGHTS);
        lineWeightComboBox.setEditable(true);
        lineWeightComboBox.setPrefWidth(80);
        lineWeightComboBox.setValue(Double.toString(PaintBrush.DEFAULT_WEIGHT));
        lineWeightComboBox.valueProperty().addListener(e->{
            double comboBoxValue= 0;
            try{
                comboBoxValue = Double.parseDouble((String) lineWeightComboBox.getValue());
            } catch (NumberFormatException nfe){
                System.out.println("Oops that isn't a double!");
            }
            comboBoxValue = Math.max(1, comboBoxValue);
            double epsilon = 0.01d;
            // Important check, or setting the property would create a loop
            if(Math.abs(comboBoxValue - brush.getLineWeight()) > epsilon) {
                System.out.println("Setting brush weight from combco box");
                setBrushWeight(comboBoxValue);
            }
        });

        getChildren().addAll(lineWeightLabel, weightSlider, lineWeightComboBox);

    }

    /**
     * Create and add all components concerned with the fill of the shape.
     */
    private void createFillComponents() {
        // Line weight
        Label lineWeightLabel = new Label("Fill shape:");

        fillShape = new CheckBox();
        fillShape.setSelected(DEFAULT_FILL);
        fillShape.setOnAction(event ->
                brush.setFill(fillShape.isSelected()));

        fillColorToggle = new ToggleButton();
        if(DEFAULT_COLOR == ColorPaletteDesignation.PRIMARY){
            fillColorToggle.setText("Primary");
        } else {
            fillColorToggle.setText("Secondary");
        }
        fillColorToggle.setOnAction(event -> {
            if(fillColor == ColorPaletteDesignation.PRIMARY){
                fillColor = ColorPaletteDesignation.SECONDARY;
                fillColorToggle.setText("Secondary");
            } else {
                fillColor = ColorPaletteDesignation.PRIMARY;
                fillColorToggle.setText("Primary");
            }
            brush.setFillColor(fillColor);

        });


        getChildren().addAll(lineWeightLabel, fillShape, fillColorToggle);
    }

    /**
     * Create and add all components concerned with the drawing style of the
     * square brush.
     */
    private void createDrawComponents() {
        Label drawFromCenterLabel = new Label("Square:");
        drawCircle = new CheckBox();
        drawCircle.setSelected(DEFAULT_DRAW_SQUARE);
        drawCircle.setOnAction(event ->
                brush.setDrawSquare(drawCircle.isSelected()));

        getChildren().addAll(drawFromCenterLabel, drawCircle);
    }

    /**
     * Set the line weight of the brush.
     * @param value the new line weight for the brush.
     */
    private void setBrushWeight(double value){
        brush.setLineWeight(value);
        weightSlider.setValue(Math.log(value)/Math.log(SLIDER_EXP_BASE));
        lineWeightComboBox.setValue(String.format("%.2f", value));
    }

    /**
     * Get the square tool associated with this this toolbar.
     * @return this toolbar's square brush.
     */
    public Brush getBrush() {
        return brush;
    }
}
