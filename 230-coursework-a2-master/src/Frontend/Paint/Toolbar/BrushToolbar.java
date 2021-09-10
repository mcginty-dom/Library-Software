package Frontend.Paint.Toolbar;

import Frontend.Paint.Brushes.Brush;
import Frontend.Paint.Brushes.PaintBrush;
import com.sun.javafx.util.Utils;
import javafx.geometry.Orientation;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

/**
 * A PaintProgramToolbar for handling the attributes of a paint brush.
 * @author matt
 */
public class BrushToolbar extends PaintProgramToolbar {

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
    private static final double WEIGHT_SLIDER_EXP_BASE = 1.06d; // This was calculated experimentally
    /**
     * Lowest value for the weight slider.
     */
    private static final double WEIGHT_SLIDER_MIN = 0.01d;
    /**
     * Highest value for the height slider.
     */
    private static final double WEIGHT_SLIDER_MAX = 100d;
    /**
     * Lowest value for the opacity slider.
     */
    private static final double OPACITY_SLIDER_MIN = 0d;
    /**
     * Highest value for the opacity slider.
     */
    private static final double OPACITY_SLIDER_MAX = 1d;

    private PaintBrush brush;

    private Slider weightSlider;
    private ComboBox lineWeightComboBox;

    private Slider opacitySlider;
    private TextField opacityInput;

    /**
     * Create a new toolbar with a new brush.
     */
    public BrushToolbar() {
        this(null);
    }

    /**
     * Create a new toolbar with the given paintbrush.
     * @param paintBrush a paintbrush to assign to this toolbar
     */
    public BrushToolbar(PaintBrush paintBrush) {
        super();
        if (paintBrush == null) {
            paintBrush = new PaintBrush();
        }
        this.brush = paintBrush;
        createComponents();
        getChildren().add(new Separator(Orientation.VERTICAL));

    }

    /**
     * Create all of the components for the toolbar.
     */
    private void createComponents() {
        createLineWeightComponents();
        getChildren().add(new Separator(Orientation.VERTICAL));
        createOpacityComponents();
    }

    /**
     * Create and add components for altering the line weight.
     */
    private void createLineWeightComponents() {

        // Line weight
        Label lineWeightLabel = new Label("Line weight:");

        weightSlider = new Slider();
        weightSlider.setMin(WEIGHT_SLIDER_MIN);
        weightSlider.setMax(WEIGHT_SLIDER_MAX);
        weightSlider.setValue(Math.log(PaintBrush.DEFAULT_WEIGHT) / Math.log(WEIGHT_SLIDER_EXP_BASE));
        weightSlider.valueProperty().addListener(e->{

            double newValue = Math.pow(WEIGHT_SLIDER_EXP_BASE, weightSlider.getValue());
            double sliderMaxValue = Math.pow(WEIGHT_SLIDER_EXP_BASE, WEIGHT_SLIDER_MAX);
            double epsilon = 0.01d;
            // Important check, or setting the property would create a loop
            // Will ignore values outside slider range as these cannot be presented

            boolean valueOverSliderMax = brush.getLineWeight() > sliderMaxValue;
            boolean sliderAtMax = (weightSlider.getValue() == WEIGHT_SLIDER_MAX);
            boolean brushAlreadyAtSlider = Math.abs(newValue - brush.getLineWeight()) < epsilon;

            boolean shouldUpdate = (!valueOverSliderMax || !sliderAtMax) && !brushAlreadyAtSlider;

            if(shouldUpdate) {
                setBrushWeight(newValue);
            }
        });

        lineWeightComboBox = new ComboBox();
        lineWeightComboBox.getItems().addAll((Object[]) DEFAULT_WEIGHTS);
        lineWeightComboBox.setEditable(true);
        lineWeightComboBox.setPrefWidth(80);
        lineWeightComboBox.setValue(Double.toString(PaintBrush.DEFAULT_WEIGHT));
        lineWeightComboBox.valueProperty().addListener(e->{
            double comboBoxValue;
            try{
                comboBoxValue = Double.parseDouble((String) lineWeightComboBox.getValue());
            } catch (NumberFormatException nfe){
                return;
            }
            comboBoxValue = Math.max(WEIGHT_SLIDER_MIN, comboBoxValue);
            double epsilon = 0.01d;
            // Important check, or setting the property would create a loop
            if(Math.abs(comboBoxValue - brush.getLineWeight()) > epsilon) {
                setBrushWeight(comboBoxValue);
            }
        });

        getChildren().addAll(lineWeightLabel, weightSlider, lineWeightComboBox);

    }

    /**
     * Create and add components for altering the line weight.
     */
    protected void createOpacityComponents() {
        // Line weight
        Label opacityLabel = new Label("Opacity:");

        opacitySlider = new Slider();
        opacitySlider.setMin(OPACITY_SLIDER_MIN);
        opacitySlider.setMax(OPACITY_SLIDER_MAX);
        opacitySlider.setValue(PaintBrush.DEFAULT_OPACITY);
        opacitySlider.valueProperty().addListener(e->{
            opacityInput.setText(String.format("%.2f", opacitySlider.getValue()));
            double newValue = opacitySlider.getValue();
            double epsilon = 0.01d;
            // Important check, or setting the property would create a loop
            // Will ignore values outside slider range as these cannot be presented

            boolean brushAlreadyAtSlider = Math.abs(newValue - brush.getOpacity()) < epsilon;

            if(!brushAlreadyAtSlider) {
                brush.setOpacity(newValue);
            }
        });

        opacityInput = new TextField(String.format("%.2f", PaintBrush.DEFAULT_OPACITY));
        opacityInput.setOnAction(event -> {
            double value;
            try{
                value = Double.parseDouble((String) lineWeightComboBox.getValue());
            } catch (NumberFormatException nfe){
                return;
            }
            value = Utils.clamp(OPACITY_SLIDER_MIN, value, OPACITY_SLIDER_MAX);
            opacitySlider.setValue(value);
            brush.setOpacity(value);
        });
        opacityInput.setPrefWidth(40);
        setHgrow(opacityInput, Priority.NEVER);

        getChildren().addAll(opacityLabel, opacitySlider, opacityInput);
    }

    /**
     * Set the weight value for the paint brush.
     * @param value the new value for the brush weight
     */
    private void setBrushWeight(double value){
        brush.setLineWeight(value);
        weightSlider.setValue(Math.log(value)/Math.log(WEIGHT_SLIDER_EXP_BASE));
        lineWeightComboBox.setValue(String.format("%.2f", value));
    }

    /**
     * Get this toolbar's paint brush.
     * @return the paintbrush assigned to this toolbar
     */
    public Brush getBrush() {
        return brush;
    }


}
