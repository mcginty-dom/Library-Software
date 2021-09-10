package Frontend.Paint.Toolbar;

import Frontend.Paint.Brushes.Brush;
import Frontend.Paint.Brushes.LineBrush;
import Frontend.Paint.Brushes.PaintBrush;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

/**
 * A PaintProgramToolbar for handling the attributes of the
 * line brush.
 * @author matt
 */
public class LineToolbar extends PaintProgramToolbar {

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

    private LineBrush brush;

    private Slider weightSlider;
    private ComboBox lineWeightComboBox;

    /**
     * Create a new toolbar with a new brush.
     */
    public LineToolbar(){
        this(null);
    }

    /**
     * Create a new toolbar with the given line brush.
     * @param lineBrush a line brush to assign to this toolbar
     */
    public LineToolbar(LineBrush lineBrush) {
        super();
        if (lineBrush == null) {
            lineBrush = new LineBrush();
        }
        this.brush = lineBrush;
        createComponents();

    }

    /**
     * Create and add all components for this brush.
     */
    private void createComponents(){
        createLineWeightComponents();
    }

    /**
     * Create and add all components concerned with line weight.
     */
    private void createLineWeightComponents() {

        // Line weight
        Label lineWeightLabel = new Label("Line weight:");

        weightSlider = new Slider();
        weightSlider.setMin(SLIDER_MIN);
        weightSlider.setMax(SLIDER_MAX);
        weightSlider.setValue(Math.log(PaintBrush.DEFAULT_WEIGHT) / Math.log(SLIDER_EXP_BASE));
        weightSlider.valueProperty().addListener(e -> {

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
     * Set the weight of the line brush.
     * @param value the new line weight value for the line brush.
     */
    private void setBrushWeight(double value) {
        brush.setLineWeight(value);
        weightSlider.setValue(Math.log(value) / Math.log(SLIDER_EXP_BASE));
        lineWeightComboBox.setValue(String.format("%.2f", value));
    }

    /**
     * Get the brush associated with this toolbar
     * @return this toolbar's line brush.
     */
    public Brush getBrush() {
        return brush;
    }
}
