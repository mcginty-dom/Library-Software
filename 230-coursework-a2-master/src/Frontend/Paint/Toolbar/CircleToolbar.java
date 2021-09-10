package Frontend.Paint.Toolbar;

import Frontend.Paint.Brushes.Brush;
import Frontend.Paint.Brushes.CircleBrush;
import Frontend.Paint.Brushes.PaintBrush;
import Frontend.Paint.Tools.PaintCanvas.ColorPaletteDesignation;
import javafx.geometry.Orientation;
import javafx.scene.control.*;

/**
 * A PaintProgramToolbar for handling the attributes of the circle  brush.
 * @author matt
 */
public class CircleToolbar extends PaintProgramToolbar {

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
     * Default draw circles flag.
     */
    public static final boolean DEFAULT_DRAW_CIRCLE = false;

    private CircleBrush brush;

    private Slider weightSlider;
    private ComboBox lineWeightComboBox;

    private CheckBox fillShape;
    private ToggleButton fillColorToggle;
    private ColorPaletteDesignation fillColor = DEFAULT_COLOR;
    private CheckBox drawCircle;

    /**
     * Create a new toolbar with a new brush.
     */
    public CircleToolbar(){
        this(null);
    }

    /**
     * Create a new toolbar with the given circle brush.
     * @param circleBrush a circle brush to assign to this toolbar
     */
    public CircleToolbar(CircleBrush circleBrush) {
        super();
        if (circleBrush == null) {
            circleBrush = new CircleBrush();
        }
        this.brush = circleBrush;
        createComponents();


    }

    /**
     * Create and add all components to the toolbar.
     */
    private void createComponents()  {
        createLineWeightComponents();
        getChildren().add(new Separator(Orientation.VERTICAL));
        createFillComponents();
        getChildren().add(new Separator(Orientation.VERTICAL));
        createDrawComponents();
    }


    /**
     * Create and add components concerning line weight.
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
     * Create and add components concerning the fill state of the
     * circleBrush.
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
     * Create and add components that affect the drawing
     * style of the circle brush.
     */
    private void createDrawComponents() {
        Label drawFromCenterLabel = new Label("Circle:");
        drawCircle = new CheckBox();
        drawCircle.setSelected(DEFAULT_DRAW_CIRCLE);
        drawCircle.setOnAction(event ->
                brush.setDrawCircle(drawCircle.isSelected()));

        getChildren().addAll(drawFromCenterLabel, drawCircle);
    }

    /**
     * Set the line weight of the circle brush.
     * @param value the new value for the circle brush line weight.
     */
    private void setBrushWeight(double value) {
        brush.setLineWeight(value);
        weightSlider.setValue(Math.log(value) / Math.log(SLIDER_EXP_BASE));
        lineWeightComboBox.setValue(String.format("%.2f", value));
    }

    /**
     * Get the circle brush associated with this toolbar.
     * @return this bar's circle brush
     */
    public Brush getBrush() {
        return brush;
    }
}
