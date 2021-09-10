package Frontend.Nodes;

import Backend.Users.Librarian;
import Backend.Users.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.IOException;

/**
 * A JavaFX node wrapper for users, allows users
 * to be converted into easily viewable Nodes to be displayed
 * on a gui.
 * @author matt
 */
public class UserViewer {

    @FXML private ImageView profileImage;
    @FXML private Label userSince;
    @FXML private Label isLibrarian;
    @FXML private Label userNameLabel;

    private User user;

    /**
     * Populate all user-based parameters of the user viewer.
     * @param user the user to view
     */
    private void setUser(final User user) {
        this.user = user;

        profileImage.setImage(
                new Image("file:" + user.getProfileImageLocation())
        );
        userNameLabel.setText(user.getUsername());
        userSince.setText("User since: " + user.getUSER_CREATION_DATE());
        isLibrarian.setText("Standard User");
        if (user instanceof Librarian) {
            isLibrarian.setText("Librarian");
        }
    }

    /**
     * Create a node from a given user that represents the user graphically.
     * @param user the user to build a viewer for, not null
     * @return a Node representation of the provided User
     * @throws IOException if there is an error loading the FXML this will
     * be thrown
     */
    public static HBox generate(final User user) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                UserViewer.class.getResource("/fxml/nodes/User_View.fxml")
        );
        HBox resourceView = loader.load();
        UserViewer controller = loader.getController();

        controller.setUser(user);

        return resourceView;
    }

}
