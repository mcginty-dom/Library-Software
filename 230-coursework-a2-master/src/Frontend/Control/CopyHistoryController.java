package Frontend.Control;

import Backend.Databases.UserDatabase;
import Backend.Resources.Copy;
import Backend.Sounds.SoundEffects;
import Backend.Transactions.Transaction;
import Backend.Users.User;
import Frontend.Tools.TransactionTableDataModel;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Controller for the copy history page. Linked to Copy_History_Popup.fxml
 * performs the all the logic for the page
 * <br>
 * Should never need to be created manually, use the launchViewHistory
 * method to open the page and acquire the controller
 * @author matt
 */
public class CopyHistoryController {

    @FXML private TableColumn<TransactionTableDataModel, String> viewUserColumn;
    @FXML private TableColumn<Object, Object> userColumn;
    @FXML private TableColumn<Object, Object> actionColumn;
    @FXML private TableColumn<Object, Object> dateColumn;

    @FXML private Label headerLabel;
    @FXML private TableView<TransactionTableDataModel> historyTable;

    private Stage stage;
    private User callingUser;
    private Copy copy;

    /**
     * Set the user who called this window to allow context based
     * operations. Assumed not to be null
     * @param user the user who in logged in currently.
     */
    public void setUser(final User user) {
        this.callingUser = user;
    }

    /**
     * Set the copy of a resource that this window is referencing
     * the history of.
     * @param setCopy the copy to acquire the history from.
     */
    private void setCopy(final Copy setCopy) {
        this.copy = setCopy;

        headerLabel.setText(
                String.format(
                        "History for copy %d of %s",
                        copy.getID(),
                        copy.getResource().getTitle()
                )
        );

        if (copy.getCurrentTransaction() == null
                && copy.getHistory().size() == 0) {
            // Copy has no history. Terminate early
            historyTable.setPlaceholder(new Label("Copy has no history"));
            return;
        }
        ArrayList<TransactionTableDataModel> data = new ArrayList<>();

        final String alignment = "-fx-alignment: CENTER;";

        viewUserColumn.setCellValueFactory(new PropertyValueFactory<>(""));
        viewUserColumn.setStyle(alignment);
        userColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        userColumn.setStyle(alignment);
        actionColumn.setCellValueFactory(
                new PropertyValueFactory<>("transactionType")
        );
        actionColumn.setStyle(alignment);
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateColumn.setStyle(alignment);
        viewUserColumn.setCellFactory(param -> createButtonColumnCallback());

        for (Transaction t: copy.getHistory()) {
            data.addAll(TransactionTableDataModel.getData(t));
        }
        if (copy.getCurrentTransaction() != null) {
            data.addAll(TransactionTableDataModel.getData(
                    copy.getCurrentTransaction())
            );
        }
        // We are currently in chronological, we want reverse chronological
        Collections.reverse(data);

        historyTable.setItems(FXCollections.observableArrayList(data));
    }

    /**
     * Refresh the page, updating any fields that might have changed in
     * reference to the set copy.
     */
    private void refresh() {
        setCopy(copy);
    }

    /**
     * Open up a user inspection page for any user with a given username.
     * @param username the username of the user you want to inspect.
     * @throws IOException if there was an error opening this inspector
     */
    private void createUserInspector(final String username) throws IOException {
        SoundEffects.submitButton.play();
        UserInspectController controller = UserInspectController
                .launchUserInspector(
                        UserDatabase.queryUserByUsername(username), callingUser
                );
        controller.getStage().setOnHidden(event -> refresh());
    }

    /**
     * Creates a callback used by table view to add a button to a table.
     * @return the table cell to be used by the table.
     */
    private TableCell<TransactionTableDataModel, String> createButtonColumnCallback() {
        final Button viewUserButton = new Button("View User");
        return new TableCell<TransactionTableDataModel, String>() {

            /**
             * Called when the table is updated, adds a button to a cell.
             * @param item the string given to the table cell.
             * @param empty if the cell has no value.
             */
            @Override
            public void updateItem(final String item, final boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    viewUserButton.setOnAction(event -> onViewUserButton());
                    setGraphic(viewUserButton);
                    setText(null);
                }
            }

            private void onViewUserButton() {
                String username = getTableView().getItems().get(
                        getIndex()
                ).getUsername();
                try {
                    createUserInspector(username);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    /**
     * Get the window that this controller manages.
     * @return the stage that this controller manages.
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * Open the view copy history page for a specified copy as called
     * by a given user as a popup. This is the preferred method of
     * opening the page.
     * Returns the page controller for the new window.
     * @param user The calling user for context.
     * @param copy The copy to acquire history from.
     * @return the controller of the newly created window
     * @throws IOException if there was an error loading the page
     */
    public static CopyHistoryController launchViewHistory(
            final User user, final Copy copy) throws IOException {

        FXMLLoader loader = new FXMLLoader(
                DashboardController.class.getResource(
                        "/fxml/Copy_History_Popup.fxml"
                )
        );
        loader.load();
        Parent root = loader.getRoot();

        CopyHistoryController controller = loader.getController();
        controller.setCopy(copy);
        controller.setUser(user);

        Scene newScene = new Scene(root);
        Stage stage = new Stage();

        controller.stage = stage;
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("History for " + copy.getResource().getTitle());
        stage.setScene(newScene);
        stage.show();
        stage.getIcons().add(new Image(
                "file:" + copy.getResource().getThumbnail()
        ));
        return controller;
    }
}
