package Frontend.Nodes;

import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;

/**
 * A sort toggle is a toggle button with three states.
 * A sort toggle can be released, ascending or descending.
 * Functionally they behave similarly to regular toggle buttons in all
 * other regards.
 * @author matt
 */
public class SortToggle extends ToggleButton {

    /**
     * The sort order of a sort toggle button.
     * A sort toggle must be in a SortOrder state.
     */
    public enum SortOrder {
        ASCENDING,
        DESCENDING,
        UNSORTED;

        /**
         * Progress circularly though SortOrder states.
         * @return the next SortOrder state.
         */
        public SortOrder next() {
            switch (this) {
                case ASCENDING:
                    return DESCENDING;
                case DESCENDING:
                    return UNSORTED;
                case UNSORTED:
                    return ASCENDING;
            }
            return UNSORTED;
        }
    }

    /**
     * Unsorted icon.
     */
    private static final Image SORT_UNSORTED = new Image("file:src/res/images/ui/sort_unordered.png");

    /**
     * Ascending sort icon.
     */
    private static final Image SORT_ASCENDING = new Image("file:src/res/images/ui/sort_ascending.png");

    /**
     * Descending sort icon.
     */
    private static final Image SORT_DESCENDING = new Image("file:src/res/images/ui/sort_descending.png");

    private static final int GRAPHIC_SIZE = 20;
    private static final int FONT_SIZE = 14;

    private ImageView sortView;
    private Image unsortedImage;
    private Image ascendingImage;
    private Image descendingImage;
    private SortOrder order;

    /**
     * Create a new empty SortToggle.
     */
    public SortToggle() {
        this("");
    }

    /**
     * Create a new sort toggle with the name and text provided.
     * @param label the text for the button
     */
    public SortToggle(final String label) {
        this(label, SORT_UNSORTED, SORT_ASCENDING, SORT_DESCENDING);
    }

    /**
     * Create a new sort toggle with the name and text provided and the images
     * set for each available state.
     * @param label label the text for the button
     * @param unsorted the image to use as a graphic in the unsorted state
     * @param ascending the image to use as a graphic in the ascending state
     * @param descending the image to use as a graphic in the descending state
     */
    public SortToggle(
            final String label,
            final Image unsorted,
            final Image ascending,
            final Image descending) {
        this(label, unsorted, ascending, descending,
                GRAPHIC_SIZE, GRAPHIC_SIZE, FONT_SIZE);
    }

    /**
     * Create a new sort toggle with the name and text provided and the images
     * set for each available state. Setting the images to the give width and
     * height, and the font of the text to have to specified size.
     * @param label label the text for the button
     * @param unsorted the image to use as a graphic in the unsorted state
     * @param ascending the image to use as a graphic in the ascending state
     * @param descending the image to use as a graphic in the descending state
     * @param graphicWidth the desired width of each image
     * @param graphicHeight the desired height of each image
     * @param fontSize the desired font size for the text.
     */
    public SortToggle(
            final String label,
            final Image unsorted,
            final Image ascending,
            final Image descending,
            final int graphicWidth,
            final int graphicHeight,
            final int fontSize) {
        this(label, unsorted, ascending, descending, graphicWidth,
                graphicHeight, fontSize, SortOrder.UNSORTED);
    }

    /**
     * Create a new sort toggle with the name and text provided and the images
     * set for each available state. Setting the images to the give width and
     * height, and the font of the text to have to specified size. The
     * button starts in the given order.
     * @param label label the text for the button
     * @param unsorted the image to use as a graphic in the unsorted state
     * @param ascending the image to use as a graphic in the ascending state
     * @param descending the image to use as a graphic in the descending state
     * @param graphicWidth the desired width of each image
     * @param graphicHeight the desired height of each image
     * @param fontSize the desired font size for the text.
     */
    public SortToggle(
            final String label,
            final Image unsorted,
            final Image ascending,
            final Image descending,
            final int graphicWidth,
            final int graphicHeight,
            final int fontSize,
            final SortOrder order) {

        super(label);
        this.order = order;
        this.unsortedImage = unsorted;
        this.ascendingImage = ascending;
        this.descendingImage = descending;

        sortView = new ImageView();
        sortView.setFitWidth(graphicWidth);
        sortView.setFitHeight(graphicHeight);
        sortView.setImage(getImage());
        super.setGraphic(sortView);
        super.setContentDisplay(ContentDisplay.LEFT);

        super.setFont(new Font(super.getFont().getName(), fontSize));

        setOnAction(event -> setSortOrder(this.order.next()));
        selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                this.sortView.setImage(unsortedImage);
            }
        });
    }

    /**
     * Set the current sort order of this button.
     * @param order the new order to be at.
     */
    public void setSortOrder(final SortOrder order) {
        this.order = order;
        setSelected(order != SortOrder.UNSORTED);
        this.sortView.setImage(getImage());
    }

    /**
     * Get the current sort order of this button
     * @return the current sort order of this button.
     */
    public SortOrder getSortOrder() {
        return this.order;
    }

    /**
     * Get the image associated with the button's current order.
     * @return an image corresponding to the current button state.
     */
    private Image getImage() {
        switch (this.order) {
            case UNSORTED:
                return unsortedImage;
            case DESCENDING:
                return descendingImage;
            case ASCENDING:
                return ascendingImage;
            default:
                return unsortedImage;
        }
    }



}
