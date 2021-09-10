package Frontend.Nodes;

import Backend.Databases.UserDatabase;
import Backend.Resources.*;
import Backend.Users.User;
import Frontend.Control.UserInspectController;
import Frontend.UIManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import java.io.IOException;

/**
 * A JavaFX node wrapper for resources, allows resources
 * to be converted into easily viewable Nodes to be displayed
 * on a gui.
 * @author matt
 */
public class ResourceViewer {

    /**
     * Placeholder for resources with no due date.
     */
    private static final String DUE_DATE_NOT_SET = "-";


    /* All view should have these attributes */
    @FXML private ImageView resourceThumbnail;
    @FXML private Label resourceYear;
    @FXML private Label resourceType;
    @FXML private Label resourceID;
    @FXML private Label resourceTitle;

    /* Borrowed. Requested. Reserved. Search */
    @FXML private Label resourceMinimumLoanDuration;
    @FXML private Label copyID;

    /* Borrowed. Overdue. History */
    @FXML private Label borrowedOnLabel;

    /* Reserved */
    @FXML private Label reservedOnLabel;

    /* Borrowed. Overdue */
    @FXML private Label dueOnLabel;

    /* Borrowed */
    @FXML private Label overdueLabel;

    /* Reserved */
    @FXML private Label expectedArrivalDateLabel;

    /* Overdue */
    @FXML private Label costPerDayLabel;
    @FXML private Label chargeLabel;

    /* Overdue overview */
    @FXML private Label borrowerLabel;
    @FXML private Button viewProfileButton;
    @FXML private Label daysOverdueLabel;

    /* Search */
    @FXML private Label copiesInLibraryLabel;
    @FXML private Label copiesAvailableLabel;
    @FXML private ImageView star0;
    @FXML private ImageView star1;
    @FXML private ImageView star2;
    @FXML private ImageView star3;
    @FXML private ImageView star4;
    @FXML private Label noRatingLabel;



    private Resource resource;

    /**
     * Set the chosen resource, populating all basic fields
     * with the information that can be gained from the resource.
     * @param resource the resource to represent.
     */
    private void setResource(final Resource resource) {
        this.resource = resource;

        resourceThumbnail.setImage(
                new Image("file:" + resource.getThumbnail())
        );
        resourceYear.setText(Integer.toString(resource.getYear()));
        resourceType.setText(resource.getTypeString());
        resourceID.setText("#" + resource.getID());
        resourceTitle.setText(resource.getTitle());
    }

    /**
     * Get the resource associated with this view
     * @return the resource this view represents.
     */
    public Resource getResource(){
        return this.resource;
    }

    /**
     * Set the copy id field value.
     * @param id the value to set
     */
    private void setCopyID(final int id) {
        copyID.setText("(Copy " + Integer.toString(id) + ")");
    }

    /**
     * Set the minimum duration field value
     * @param minLoanDuration the minimum duration field value.
     */
    private void setMinLoanDuration(final String minLoanDuration) {
        resourceMinimumLoanDuration.setText(minLoanDuration);
    }

    /**
     * Set the borrow date field value.
     * @param borrowDate the value for the field.
     */
    private void setBorrowDate(final String borrowDate) {
        borrowedOnLabel.setText(borrowDate);
    }

    /**
     * Set the reserve date field value
     * @param reservedDate the value for the field.
     */
    private void setReservedDate(final String reservedDate) {
        reservedOnLabel.setText(reservedDate);
    }

    /**
     * Set the value for the due date field.
     * @param dueDate the due date value, can be null.
     */
    private void setDueDate(final String dueDate) {
        if (dueDate == null) {
            dueOnLabel.setText(DUE_DATE_NOT_SET);
            return;
        }
        dueOnLabel.setText(dueDate);
    }

    /**
     * Set the value of the expected date field.
     * @param expectedDate the value of the field.
     */
    private void setExpectedDate(final String expectedDate) {
        expectedArrivalDateLabel.setText(expectedDate);
    }

    /**
     * Set the value of the overdue field.
     * @param overdue the value of the field.
     */
    private void setOverdue(final boolean overdue) {
        overdueLabel.setVisible(overdue);
    }

    /**
     * Set the value of the cost per day field.
     * @param cost the value of the field.
     */
    private void setCostPerDay(final float cost){
        costPerDayLabel.setText(String.format("£%.2f", cost));
    }

    /**
     * Set the value of the overdue charge field.
     * @param charge the value of the field.
     */
    private void setCharge(final float charge){
        chargeLabel.setText(String.format("£%.2f", charge));
    }

    /**
     * Set the information about copies fields.
     * @param copies the number of copies of a resource.
     * @param copiesAvailable the number of available copies of a resource.
     */
    private void setCopies(final int copies, final int copiesAvailable) {
        copiesInLibraryLabel.setText(Integer.toString(copies));
        copiesAvailableLabel.setText(Integer.toString(copiesAvailable));
    }

    /**
     * Set the user data to the given values
     * @param username the user associated with the resource
     * @param caller the user viewing the node.
     */
    private void setUser(String username, User caller) {
        borrowerLabel.setText(username);
        viewProfileButton.setOnAction(event -> {
            try {
                UserInspectController.launchUserInspector(
                        UserDatabase.queryUserByUsername(username), caller);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Set the value of the days overdue field.
     * @param daysOverdue the value of the field.
     */
    private void setDaysOverdue(final int daysOverdue) {
        daysOverdueLabel.setText(Integer.toString(daysOverdue));
    }

    /**
     * Set the rating of a resource, generates a pictoral view.
     * @param rating the score of the rating
     */
    private void setRating(final float rating) {
        ImageView[] stars = new ImageView[]{star0, star1, star2, star3, star4};

        if (rating < 0) {
            noRatingLabel.setVisible(true);
            for (ImageView star: stars) {
                star.setVisible(false);
                star.setManaged(false);
            }
        } else {
            noRatingLabel.setVisible(false);
            noRatingLabel.setManaged(false);

            float roundRating = Math.round(rating * 2) / 2f;
            for (ImageView star: stars) {
                star.setVisible(true);
                star.setManaged(true);
                if (roundRating - 1 >= 0) {
                    star.setImage(new Image("file:" + UIManager.FULL_STAR_ICON));
                } else if (roundRating - (1 / 2f) >= 0) {
                    star.setImage(new Image("file:" + UIManager.HALF_STAR_ICON));
                } else {
                    star.setImage(new Image("file:" + UIManager.EMPTY_STAR_ICON));
                }
                roundRating--;
            }
        }
    }


    /**
     * The style for the resource view designed for the user borrow tab.
     * @param copy the borrowed copy to reference.
     * @param borrowDate the borrow date of the copy
     * @param dueDate the due date of the resource, null if no due date set
     * @param overdue a flag, if true the copy is overdue.
     * @return A Node representing the passed data
     * @throws IOException if there is an error loading FXML this is thrown.
     */
    public static HBox borrowedStyle(
            final Copy copy,
            final String borrowDate,
            final String dueDate,
            final boolean overdue) throws IOException {

        FXMLLoader loader = new FXMLLoader(
                ResourceViewer.class.getResource(
                        "/fxml/nodes/Resource_View_Borrow_Style.fxml"
                )
        );
        HBox resourceView = loader.load();
        ResourceViewer controller = loader.getController();

        controller.setCopyID(copy.getID());
        Resource resource = copy.getResource();
        controller.setResource(resource);
        controller.setMinLoanDuration(resource.getMinLoanDuration());
        controller.setBorrowDate(borrowDate);
        controller.setDueDate(dueDate);
        controller.setOverdue(overdue);

        return resourceView;
    }

    /**
     * The style for the resource view designed for the user requested tab.
     * @param resource the requested resource to reference.
     * @param expectedAvailableDate the date that the copy may next be in
     * @return A Node representing the passed data
     * @throws IOException if there is an error loading FXML this is thrown.
     */
    public static Node requestedStyle(
            final Resource resource,
            final String expectedAvailableDate) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                ResourceViewer.class.getResource(
                        "/fxml/nodes/Resource_View_Requested_Style.fxml"
                )
        );
        HBox resourceView = loader.load();
        ResourceViewer controller = loader.getController();

        controller.setResource(resource);
        controller.setMinLoanDuration(resource.getMinLoanDuration());
        controller.setExpectedDate(expectedAvailableDate);

        return resourceView;
    }

    /**
     * The style for the resource view designed for the user reserved tab.
     * @param copy the reserved copy to reference.
     * @param reservedDate the reserved date of the copy
     * @param dueDate the due date of the resource, null if no due date set
     * @param overdue a flag, if true the copy is overdue.
     * @return A Node representing the passed data
     * @throws IOException if there is an error loading FXML this is thrown.
     */
    public static HBox reservedStyle(
            final Copy copy,
            final String reservedDate,
            final String dueDate,
            final boolean overdue) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                ResourceViewer.class.getResource(
                        "/fxml/nodes/Resource_View_Reserved_Style.fxml"
                )
        );
        HBox resourceView = loader.load();
        ResourceViewer controller = loader.getController();

        controller.setCopyID(copy.getID());
        Resource resource = copy.getResource();
        controller.setResource(resource);
        controller.setMinLoanDuration(resource.getMinLoanDuration());
        controller.setReservedDate(reservedDate);
        controller.setDueDate(dueDate);
        controller.setOverdue(overdue);

        return resourceView;
    }

    /**
     * The style for the resource view designed for the user overdue items tab.
     * @param copy the reserved copy to reference.
     * @param dueDate the due date of the copy
     * @param costPerDay the cost per overdue day of the copy
     * @param charge the current overdue cost.
     * @return A Node representing the passed data
     * @throws IOException if there is an error loading FXML this is thrown.
     */
    public static HBox overdueStyle(
            final Copy copy,
            final String dueDate,
            final float costPerDay,
            final float charge) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                ResourceViewer.class.getResource(
                        "/fxml/nodes/Resource_View_Overdue_Style.fxml"
                )
        );
        HBox resourceView = loader.load();
        ResourceViewer controller = loader.getController();

        controller.setCopyID(copy.getID());
        Resource resource = copy.getResource();
        controller.setResource(resource);
        controller.setMinLoanDuration(resource.getMinLoanDuration());
        controller.setCostPerDay(costPerDay);
        controller.setDueDate(dueDate);
        controller.setCharge(charge);

        return resourceView;
    }

    /**
     * The style for the resource view designed for the overdue search page.
     * @param copy the reserved copy to reference.
     * @param dueDate the due date of the copy
     * @param charge the current overdue cost.
     * @param daysOverdue the days past due the item is
     * @param username user who owns the overdue book
     * @param caller the observer of the system.
     * @return A Node representing the passed data
     * @throws IOException if there is an error loading FXML this is thrown.
     */
    public static HBox overdueOverviewStyle(
            final Copy copy,
            final String dueDate,
            final float charge,
            final int daysOverdue,
            final String username,
            final User caller) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                ResourceViewer.class.getResource(
                        "/fxml/nodes/Resource_View_Overdue_Overview_Style.fxml"
                )
        );
        HBox resourceView = loader.load();
        ResourceViewer controller = loader.getController();

        controller.setCopyID(copy.getID());
        Resource resource = copy.getResource();
        controller.setResource(resource);
        controller.setDaysOverdue(daysOverdue);
        controller.setDueDate(dueDate);
        controller.setUser(username, caller);
        controller.setCharge(charge);

        return resourceView;
    }

    /**
     * The style for the resource view designed for the user requested tab.
     * @param resource the requested resource to reference.
     * @param copies the number of copies in the system.
     * @param copiesAvailable the number of available copies in the system.
     * @return A Node representing the passed data
     * @throws IOException if there is an error loading FXML this is thrown.
     */
    public static HBox searchStyle(
            final Resource resource,
            final int copies,
            final int copiesAvailable) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                ResourceViewer.class.getResource(
                        "/fxml/nodes/Resource_View_Search_Style.fxml"
                )
        );
        HBox resourceView = loader.load();
        ResourceViewer controller = loader.getController();

        controller.setResource(resource);
        controller.setMinLoanDuration(resource.getMinLoanDuration());
        controller.setCopies(copies, copiesAvailable);
        controller.setRating(resource.getReview().getRating());

        return resourceView;
    }

}
