package Frontend.Control;

import Backend.Resources.Element;
import Backend.Resources.Laptop;
import Backend.Resources.Resource;
import Backend.Users.User;
import Frontend.Tools.StringSanitiser;
import Frontend.UIManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

/**
 * Controller for the read reviews popup
 * Linked to Read_Reviews_Popup.fxml
 * Allows a user to browse all reviews associated with a resource.
 * <br>
 * Should never need to be created manually, use the launchReadReviews
 * method to open the page and get the controller
 * @author matt
 */
public class ReadReviewsController {

    @FXML private Label headerLabel;
    @FXML private VBox contentBox;

    /**
     * Set the resource who's reviews should be browseable.
     * @param resource the resource of focus.
     */
    public void setResource(final Resource resource) {
        headerLabel.setText(
                "Read "
                        + resource.getReview().getReviewList().size()
                        + " reviews for "
                        + resource.getTitle()
        );
        for (Element e: resource.getReview().getReviewList()) {
            addReview(e);
        }
    }

    /**
     * Add a readable review to the list of reviews in the display area.
     * @param element the review element to add
     */
    private void addReview(final Element element) {
        BorderPane content = new BorderPane();
        BorderPane top = new BorderPane();
        content.setTop(top);
        Label userLabel = new Label(element.getPostedBy() + " said:");
        userLabel.setFont(Font.font(16));
        top.setLeft(userLabel);
        top.setRight(generateRating(element.getRating()));

        Label reviewText = new Label(element.getReviewText());
        reviewText.setWrapText(true);
        content.setCenter(reviewText);
        BorderPane.setAlignment(reviewText, Pos.TOP_LEFT);

        contentBox.getChildren().add(content);
        contentBox.getChildren().add(new Separator(Orientation.HORIZONTAL));
    }

    /**
     * Create a JavaFX node representing a star rating of the value given.
     * This will be n consecutive filled stars followed by 5-n consecutive
     * empty stars running left to right.
     * @param score the score to represent.
     * @return a node in the form of a 5 star rating.
     */
    private HBox generateRating(final int score) {
        final int starSize = 30;
        final int dropShadowSize = 15;
        final int dropShadowIndex = 2;
        final int ratingSize = 5;

        HBox ratingBox = new HBox();
        DropShadow dropShadow = new DropShadow();
        dropShadow.setWidth(dropShadowSize);
        dropShadow.setHeight(dropShadowSize);
        dropShadow.setOffsetX(dropShadowIndex);
        dropShadow.setOffsetY(dropShadowIndex);
        for (int i = 0; i < ratingSize; i++) {
            ImageView starView = new ImageView();
            starView.setFitWidth(starSize);
            starView.setFitHeight(starSize);
            starView.setEffect(dropShadow);
            if (score > i) {
                starView.setImage(
                        new Image("file:" + UIManager.FULL_STAR_ICON)
                );
            } else {
                starView.setImage(
                        new Image("file:" + UIManager.EMPTY_STAR_ICON)
                );
            }
            ratingBox.getChildren().add(starView);
        }
        return ratingBox;
    }

    /**
     * Launch the review reader, reading the reviews of the given resource.
     * @param resource the resource to read reviews from.
     * @throws IOException if there is an error loading FXML contexts this will
     * be thrown
     */
    public static void launchReadReviews(
            final Resource resource) throws IOException {

        FXMLLoader loader = new FXMLLoader(
                DashboardController.class.getResource(
                        "/fxml/Read_Reviews_Popup.fxml"
                )
        );
        loader.load();
        Parent root = loader.getRoot();

        ReadReviewsController controller = loader.getController();
        controller.setResource(resource);

        Scene newScene = new Scene(root);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Reviews for " + resource.getTitle());
        stage.setScene(newScene);
        stage.show();
        stage.getIcons().add(new Image("file:" + UIManager.FULL_STAR_ICON));

    }
}
