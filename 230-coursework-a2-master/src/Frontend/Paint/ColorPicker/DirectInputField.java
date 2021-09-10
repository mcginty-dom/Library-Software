package Frontend.Paint.ColorPicker;

import Frontend.Paint.Tools.PaintCanvas;
import com.sun.javafx.util.Utils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.awt.event.KeyEvent;

/**
 * Direct input fields allow color data
 * to be generated using text fields to enter literal
 * values which are interpreted as color data later.
 * @author matt
 */
public class DirectInputField extends HBox {

    private final TextField redField;
    private final TextField greenField;
    private final TextField blueField;
    private final TextField hueField;
    private final TextField saturationField;
    private final TextField brightnessField;

    private final PaintCanvas paintCanvas;

    /**
     * Create and populate a new direct input with color values linked to
     * the supplied paint canvas.
     * @param paintCanvas the canvas to link color values to.
     */
    public DirectInputField(final PaintCanvas paintCanvas) {
        super();
        this.paintCanvas = paintCanvas;

        Label redLabel = new Label("red:");
        redField = createRGBField("r");

        Label greenLabel = new Label("green:");
        greenField  = createRGBField("g");

        Label blueLabel = new Label("blue:");
        blueField = createRGBField("b");

        Label hueLabel = new Label("hue:");
        hueField = createHSBField("h");

        Label saturationLabel = new Label("saturation:");
        saturationField = createHSBField("s");

        Label brightnessLabel = new Label("brightness:");
        brightnessField = createHSBField("b");


        GridPane directInputPane = new GridPane();

        final int gridGap = 10;

        directInputPane.setHgap(gridGap);
        directInputPane.setVgap(gridGap);
        directInputPane.add(redLabel, 0, 0);
        directInputPane.add(redField, 1, 0);
        directInputPane.add(greenLabel, 0, 1);
        directInputPane.add(greenField, 1, 1);
        directInputPane.add(blueLabel, 0, 2);
        directInputPane.add(blueField, 1, 2);
        directInputPane.add(new Separator(Orientation.HORIZONTAL), 0, 3, 2, 1);
        directInputPane.add(hueLabel, 0, 4);
        directInputPane.add(hueField, 1, 4);
        directInputPane.add(saturationLabel, 0, 5);
        directInputPane.add(saturationField, 1, 5);
        directInputPane.add(brightnessLabel, 0, 6);
        directInputPane.add(brightnessField, 1, 6);

        directInputPane.getColumnConstraints().add(new ColumnConstraints());
        directInputPane.getColumnConstraints().add(new ColumnConstraints());

        directInputPane.getColumnConstraints().get(0)
                .setHgrow(Priority.ALWAYS);
        directInputPane.getColumnConstraints().get(0)
                .setMinWidth(Region.USE_PREF_SIZE);
        directInputPane.getColumnConstraints().get(1)
                .setHgrow(Priority.SOMETIMES);
        directInputPane.getColumnConstraints().get(1)
                .setMinWidth(0);

        getChildren().addAll(directInputPane);

        paintCanvas.getActiveColorProperty().addListener((oldColor, newColor, designation) -> updateFields(newColor));
    }

    /**
     * Match the input fields to a given color.
     * @param newColor the color to match input fields text to.
     */
    private void updateFields(final Color newColor) {
        final double mod8Bit = 255;

        redField.setText(
                Integer.toString((int) (mod8Bit * newColor.getRed())));
        greenField.setText(
                Integer.toString((int) (mod8Bit * newColor.getGreen())));
        blueField.setText(
                Integer.toString((int) (mod8Bit * newColor.getBlue())));

        hueField.setText(
                String.format("%.2f", newColor.getHue()));
        saturationField.setText(
                String.format("%.2f", newColor.getSaturation()));
        brightnessField.setText(
                String.format("%.2f", newColor.getBrightness()));
    }

    /**
     * Create a text field for handling rgb colors.
     * @param prompt the prompt text for the textfield
     * @return the created field
     */
    private TextField createRGBField(final String prompt) {
        TextField textField = createField(prompt);
        textField.setOnAction(event -> rgbValuesChanged());
        return textField;
    }

    /**
     * Create a text field for handling hsb colors.
     * @param prompt the prompt text for the text field
     * @return the created field.
     */
    private TextField createHSBField(final String prompt) {
        TextField textField = createField(prompt);
        textField.setOnAction(event -> hsbValuesChanged());
        return textField;
    }

    /**
     * Create a text field for direct input.
     * @param prompt the prompt text for the field.
     * @return the newly created field.
     */
    private TextField createField(final String prompt) {
        TextField textField = new TextField();
        textField.setMinWidth(0);
        textField.setPromptText(prompt);
        return textField;
    }

    /**
     * Update the current color based on the rgb input fields' values.
     */
    private void rgbValuesChanged() {
        final double mod8Bit = 255;

        double r = 0;
        double g = 0;
        double b = 0;

        try {
            if (!redField.getText().equals("")) {
                r = Utils.clamp(
                        0, Double.parseDouble(redField.getText()), mod8Bit
                );
            }
        } catch (NumberFormatException e) {
            redField.setText("");
            return;
        }

        try {
            if (!greenField.getText().equals("")) {
                g = Utils.clamp(
                        0, Double.parseDouble(greenField.getText()), mod8Bit
                );
            }
        } catch (NumberFormatException e) {
            greenField.setText("");
            return;
        }

        try {
            if (!blueField.getText().equals("")) {
                b = Utils.clamp(
                        0, Double.parseDouble(blueField.getText()), mod8Bit
                );
            }
        } catch (NumberFormatException e) {
            blueField.setText("");
            return;
        }

        paintCanvas.getActiveColorProperty().setColor(Color.rgb((int) r, (int) g, (int) b));
    }

    /**
     * Update the current color based on the hsb input fields' values.
     */
    private void hsbValuesChanged() {
        double h = 0;
        double s = 0;
        double b = 0;

        try {
            if (!hueField.getText().equals("")) {
                h = Double.parseDouble(hueField.getText());
            }
            if (!saturationField.getText().equals("")) {
                s = Utils.clamp(
                        0, Double.parseDouble(saturationField.getText()), 1);
            }
            if (!brightnessField.getText().equals("")) {
                b = Utils.clamp(
                        0, Double.parseDouble(brightnessField.getText()), 1);
            }

        } catch (NumberFormatException e) {
            hueField.setText("");
            saturationField.setText("");
            brightnessField.setText("");
            return;
        }
        paintCanvas.getActiveColorProperty().setColor(Color.hsb(h, s, b));
    }


}
