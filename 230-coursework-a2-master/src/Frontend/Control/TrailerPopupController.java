package Frontend.Control;

import Frontend.UIManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controller for the trailer view popup
 * Linked to Trailer_Popup.fxml
 * Allows raw html content to be displayed as a popup
 * <br>
 * Should never need to be created manually, use the launchUserInspector
 * method to open the page and get the controller
 * @author matt
 */
public class TrailerPopupController {


    @FXML private WebView webviewpanel;

    /**
     * Set the raw html of the popup.
     * @param HTML a string in HTML format to be displayed.
     */
    public void setHTML(String HTML) {
        WebEngine webEngine = webviewpanel.getEngine();
        webEngine.loadContent(HTML);
    }

    /**
     * Launch the trailer popup window with a given title,
     * size and content. Will display any correctly formatted
     * html.
     * @param title the title of the popup
     * @param html the content of the popup
     * @param width the width of the popup
     * @param height the height of the popup
     * @throws IOException if there was an error reading
     * the FXML for the popup this will be thrown.
     */
    public static void launchTrailerPopup(
            final String title,
            final String html,
            final int width,
            final int height) throws IOException {

        FXMLLoader loader = new FXMLLoader(
                DashboardController.class.getResource(
                        "/fxml/Trailer_Popup.fxml"
                )
        );
        loader.load();
        Parent webviewRoot = loader.getRoot();

        TrailerPopupController controller = loader.getController();
        controller.setHTML(html);

        Scene newScene = new Scene(webviewRoot);
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(newScene);
        stage.getIcons().add(new Image("file:" + UIManager.FILM_ICON));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setWidth(width);
        stage.setHeight(height);
        stage.show();
        stage.setOnHidden(event -> {
            controller.webviewpanel.getEngine().loadContent("");
        });
    }
}
