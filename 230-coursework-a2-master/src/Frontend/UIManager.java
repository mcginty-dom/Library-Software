package Frontend;

import Frontend.Control.LoginPageController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * The root of the UI, starts the GUI and holds paths to resources
 */
public class UIManager extends Application {

    /**
     * The universal ui icon.
     */
    public static final String UI_ICON = "src/res/images/ui/TL_Icon.png";

    /**
     * A full star image.
     */
    public static final String FULL_STAR_ICON = "src/res/images/ui/star_image.png";
    /**
     * A half star image.
     */
    public static final String HALF_STAR_ICON = "src/res/images/ui/star_half.png";
    /**
     * An empty star image.
     */
    public static final String EMPTY_STAR_ICON = "src/res/images/ui/star_empty.png";

    /**
     * A trash can icon.
     */
    public static final String TRASH_BIN_ICON = "src/res/images/ui/rubbish-bin.png";
    /**
     * A clapperboard icon.
     */
    public static final String FILM_ICON = "src/res/images/ui/film_icon.png";
    /**
     * An exclamation mark icon.
     */
    public static final String EXCLAMATION_MARK = "src/res/images/ui/exclamation-mark.png";

    /**
     * Entry point to the program, should not be used as a main method.
     * @param args system arguments
     */
    public static void main(String... args){
        launch(args);
    }

    /**
     * JavaFX method. Starts the UI.
     * Opens the login page.
     * @param stage the main stage is created here
     * @throws Exception if there is problem with javaFX
     * an error will be thrown
     */
    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login_Page.fxml"));
        loader.load();
        Parent loginPageRoot = loader.getRoot();
        Scene scene = new Scene(loginPageRoot);

        stage.setTitle(LoginPageController.LOGIN_PAGE_TITLE);
        stage.setScene(scene);
        stage.show();

        stage.getIcons().add(new Image("file:" + UI_ICON));
    }
    

}
