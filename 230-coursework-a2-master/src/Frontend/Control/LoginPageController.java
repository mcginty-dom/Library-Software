package Frontend.Control;

import Backend.Databases.UserDatabase;
import Backend.Sounds.SoundEffects;
import Backend.Users.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.List;

/**
 * JavaFX controller for the Login Page
 * linked with Login_Page.fxml.
 * Lets a user login to the system
 * <br>
 * Should never need to be created manually, use the launchLoginPage
 * method to open the form and acquire the controller
 * @author Matt
 */
public class LoginPageController {

    /**
     * Title for the scene.
     */
    public static final String LOGIN_PAGE_TITLE = "Welcome to TAWE-LIB";

    @FXML private TextField usernameField;
    @FXML private Label errorMessageText;

    /**
     * JavaFX method. Activates when a user clicks the login button.
     * Takes the given username and attempts to bring them
     * to the dashboard
     * @param actionEvent the event triggered by this button
     * @throws IOException an exception will be thrown if there
     * is a error loading the dashboard information.
     */
    @FXML
    private void handleLogin(
            final ActionEvent actionEvent) throws IOException {
        String username = usernameField.getText();
        User foundUser = null;
        int index = 0;
        List<User> users = UserDatabase.queryUser(username);
        while (foundUser == null && index < users.size()) {
            User checkUser = users.get(index++);
            if (checkUser.getUsername().equals(username)) {
                foundUser = checkUser;
            }
        }
        if (foundUser == null) {
            SoundEffects.errorMessage2.play();
            errorMessageText.setText(
                    "User not found. Username is case sensitive!"
            );
            return;
        }
        SoundEffects.newPage.play();

        Node node = (Node) actionEvent.getSource();
        Stage stage = (Stage) (node).getScene().getWindow();
        LoginAs(foundUser, stage);
    }

    /**
     * Login to the user dashboard as a given user.
     * @param user the user to login as, should not be null
     * @param stage the current stage to load the dashboard into
     * @throws IOException an error will be thrown if the
     * dashboard context can't be loaded
     */
    private void LoginAs(User user, Stage stage) throws IOException {
        DashboardController.launchDashboard(stage, user);
    }

    /**
     * JavaFX method. Called when the cancel button is pressed.
     * Close the gui, and by extension the whole program
     * @param actionEvent the event created by the button click
     */
    @FXML
    private void HandleCancel(final ActionEvent actionEvent) {
        SoundEffects.goodbye.play();

        Node node = (Node) actionEvent.getSource();
        Stage stage = (Stage) (node).getScene().getWindow();
        stage.fireEvent(
                new WindowEvent(
                        stage,
                        WindowEvent.WINDOW_CLOSE_REQUEST
                )
        );
    }

    /**
     * JavaFX method. Called when the create account button is
     * pressed. Navigates to the create account form
     * @param actionEvent the event created by this button press
     * @throws IOException an exception will be thrown if
     * there was an error loading the fxml context
     */
    @FXML
    private void HandleCreateAccount(
            final ActionEvent actionEvent) throws IOException {
        SoundEffects.createPageOpen.play();
        Node node = (Node) actionEvent.getSource();
        Stage stage = (Stage) (node).getScene().getWindow();
        CreateAccountPageController.launchCreateAccountPage(stage);
    }

    /**
     * Launch and display the login page into the given stage.
     * @param stage the stage to display the login page in
     * @throws IOException an exception will be thrown if
     * if there is an error loading the fxml context
     */
    public static void launchLoginPage(
            final Stage stage) throws IOException {

        FXMLLoader loader = new FXMLLoader(
                LoginPageController.class.getResource(
                        "/fxml/Login_Page.fxml"
                )
        );
        loader.load();
        Parent loginPageRoot = loader.getRoot();
        Scene scene = new Scene(loginPageRoot);
        stage.setTitle(LoginPageController.LOGIN_PAGE_TITLE);
        stage.setScene(scene);
        stage.show();
    }
}
