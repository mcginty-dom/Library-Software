package Frontend.Control;

import Backend.Databases.TransactionDatabase;
import Backend.Databases.UserDatabase;
import Backend.Library;
import Backend.Resources.*;
import Backend.Sounds.SoundEffects;
import Backend.Transactions.Transaction;
import Backend.Users.Librarian;
import Backend.Users.User;
import Frontend.Tools.CopyTableDataModel;
import Frontend.Tools.StringSanitiser;
import Frontend.UIManager;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.stage.*;
import javafx.util.Callback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.function.UnaryOperator;

/**
 * Controller for the user inspector popup
 * Linked to User_Inspect_Page.fxml
 * Allows a user to:
 * <ul>
 *     <li>See the their details</li>
 *     <li>Change their own details</li>
 *     <li>View all open transactions</li>
 *     <li>Change status (if librarian)</li>
 *     <li>Pay fines</li>
 * </ul>
 * <br>
 * Should never need to be created manually, use the launchUserInspector
 * method to open the page and get the controller
 * @author matt
 */
public class UserInspectController {

    /**
     * Sanitiser, maps any string to itself, except the empty string,
     * which is mapped to null.
     */
    private static final StringSanitiser NOT_EMPTY = s -> {
        if (s.equals("")) {
            return null;
        }
        return s;
    };


    @FXML private TitledPane infoTitle;

    @FXML private ImageView userThumbnail;
    @FXML private ImageView editThumbnailIcon;
    @FXML private Label usernameLabel;
    @FXML private Label librarianLabel;
    @FXML private Button changeStatusButton;
    @FXML private Button makePaymentButton;
    @FXML private TextField accountBalanceField;
    @FXML private GridPane infoDisplayGrid;
    @FXML private GridPane librarianInfoDisplayGrid;

    @FXML private TitledPane copiesTitle;
    @FXML private TableView openTransactionsTable;

    @FXML private ToggleButton enableEditingButton;


    @FXML private TableColumn titleColumn;
    @FXML private TableColumn typeColumn;
    @FXML private TableColumn startDateColumn;
    @FXML private TableColumn dueDateColumn;
    @FXML private TableColumn overdueColumn;
    @FXML private TableColumn returnColumn;

    private ObservableList<Node> infoPaneDefaultState;
    private ObservableList<Node> librarianInfoPaneDefaultState;

    private User user;
    private User caller;
    private Stage stage;

    /**
     * JavaFX method called when the page opens, sets
     * the initial state of all nodes.
     * Should never be called directly.
     */
    @FXML
    private void initialize() {
        infoTitle.setExpanded(true);
        infoTitle.expandedProperty()
                .addListener((observable, oldValue, newValue) -> {

            if (!newValue && !copiesTitle.isExpanded()) {
                Platform.runLater(() -> copiesTitle.setExpanded(true));
            }
        });
        copiesTitle.expandedProperty()
                .addListener((observable, oldValue, newValue) -> {
            if (!newValue && !infoTitle.isExpanded()) {
                Platform.runLater(() -> infoTitle.setExpanded(true));
            }
        });


        // Imageview
        enableEditingButton.selectedProperty()
                .addListener((observable, oldValue, newValue) -> {
            editThumbnailIcon.setVisible(newValue);
        });
        editThumbnailIcon.setOnMouseClicked(event -> {
            if (enableEditingButton.isSelected()) {
                try {
                    loadAvatarSelection();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        // Defaults
        infoPaneDefaultState = FXCollections.observableArrayList(
                infoDisplayGrid.getChildren()
        );
        librarianInfoPaneDefaultState = FXCollections.observableArrayList(
                librarianInfoDisplayGrid.getChildren()
        );
    }

    /**
     * Change the user's profile image to the image at the specified location
     * @param avatarPreviewImage the location of the new profile image.
     */
    public void setAvatarPreviewImage(final String avatarPreviewImage) {
        this.user.setProfileImageLocation(avatarPreviewImage);
        refresh();
    }

    /**
     * JavaFX method, called when the exit button is clicked. Closes the window.
     * @param actionEvent the event created by the button click.
     */
    @FXML private void handleExitButton(ActionEvent actionEvent) {
        stage.fireEvent(
                new WindowEvent(
                        stage,
                        WindowEvent.WINDOW_CLOSE_REQUEST
                )
        );
    }

    /**
     * JavaFX method, called when the enable editing toggle is clicked.
     * Enables field editing.
     * @param actionEvent the event created by the button click.
     */
    @FXML private void onEnableEditing(final ActionEvent actionEvent) {
        SoundEffects.submitButton.play();
        if (enableEditingButton.isSelected()) {
            enableEditingButton.setText("Disable Editing");
        } else {
            enableEditingButton.setText("Enable Editing");
        }
    }

    /**
     * JavaFX method. Called when the make payment button is clicked.
     * Open a dialog and accept payment to the user's account.
     * @param actionEvent the event generated by this button.
     */
    @FXML private void handleMakePayment(final ActionEvent actionEvent) {
        final Font submitFont = Font.font(16);

        BorderPane inputBox = new BorderPane();
        TextField input = new TextField("£");
        UnaryOperator<TextFormatter.Change> filter = change -> {
            if (change.getControlNewText().startsWith("£")) {
                return change;
            } else {
                return null;
            }
        };
        TextFormatter<String> formatter = new TextFormatter<>(filter);
        input.setTextFormatter(formatter);
        input.setFont(submitFont);
        inputBox.setCenter(input);

        Button submit = new Button("Submit Payment");
        submit.setDefaultButton(true);
        submit.setFont(submitFont);
        inputBox.setRight(submit);

        Scene newScene = new Scene(inputBox);
        Stage stage = new Stage();
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(newScene);
        Node caller = ((Node) actionEvent.getSource());
        Bounds bounds = caller.getBoundsInLocal();
        stage.setX(caller.localToScreen(bounds).getMinX());
        stage.setY(caller.localToScreen(bounds).getMinY());
        stage.show();

        submit.setOnAction(event -> performPaymentOps(input));
        stage.focusedProperty()
                .addListener((observable, oldValue, newValue) ->
                stage.fireEvent(
                        new WindowEvent(
                                stage,
                                WindowEvent.WINDOW_CLOSE_REQUEST)
                )
        );
    }

    /**
     * Use the input from the payment field to attempt to
     * credit an account, assuming some valid value was entered.
     * @param input the input field to read.
     */
    private void performPaymentOps(final TextField input) {
        float value;
        try {
            value = Float.parseFloat(
                    String.format(
                            "%.2f",
                            Float.parseFloat(input.getText().substring(1))
                    )
            );
        } catch (NumberFormatException e) {
            return;
        }
        if (value <= 0) {
            return;
        }
        if (value > -user.getAccountBalance()) {
            Alert alert = new Alert(
                    Alert.AlertType.ERROR,
                    String.format("Cannot pay more than is owed!"),
                    ButtonType.CLOSE
            );
            alert.showAndWait();
            return;
        }

        Alert alert = new Alert(
                Alert.AlertType.CONFIRMATION,
                String.format(
                        "Make payment of £%.2f to %s",
                        value, user.getUsername()),
                ButtonType.YES,
                ButtonType.CANCEL
        );
        alert.setTitle("Make payment?");
        alert.showAndWait();
        if (alert.getResult() == ButtonType.CANCEL) {
            return;
        }
        SoundEffects.moneyDeposit1.play();
        Library.makePayment(user, value);
        stage.fireEvent(new WindowEvent(
                stage,
                WindowEvent.WINDOW_CLOSE_REQUEST)
        );
        refresh();
    }

    /**
     * Set the caller parameter and update any caller-specific
     * contextual items. This is the user that requested to
     * see the inspected user's page.
     * @param caller the user who requested this page
     */
    public void setCaller(final User caller) {
        this.caller = caller;
        boolean calledSelf = caller.equals(user);
        boolean librarianCalled = caller instanceof Librarian;

        enableEditingButton.setVisible(calledSelf);
        changeStatusButton.setVisible(!calledSelf && librarianCalled);
        makePaymentButton.setVisible(!calledSelf && librarianCalled);

    }

    /**
     * Set the user being inspected and update any user specific
     * context items.
     * @param user the user being inspected
     */
    private void setUser(final User user) {
        this.user = user;
        infoDisplayGrid.getChildren().removeIf(
                node -> !infoPaneDefaultState.contains(node)
        );
        librarianInfoDisplayGrid.getChildren().removeIf(
                node -> !librarianInfoPaneDefaultState.contains(node)
        );

        usernameLabel.setText(user.getUsername());

        userThumbnail.setImage(
                new Image("file:" + user.getProfileImageLocation())
        );

        accountBalanceField.setText(user.getPrintableAccountBalance());
        makePaymentButton.setDisable(user.getAccountBalance() >= 0);

        Label firstNameLabel = makeEditableLabel(
                user.getFirstName(),
                enableEditingButton.selectedProperty(),
                NOT_EMPTY
        );
        firstNameLabel.textProperty()
                .addListener((observable, oldValue, newValue) -> {
            user.setFirstName(newValue);
        });
        infoDisplayGrid.add(firstNameLabel, 1, 0);

        Label lastNameLabel = makeEditableLabel(
                user.getLastName(),
                enableEditingButton.selectedProperty(),
                NOT_EMPTY);
        lastNameLabel.textProperty()
                .addListener((observable, oldValue, newValue) -> {
            user.setLastName(newValue);
        });
        infoDisplayGrid.add(lastNameLabel, 3, 0);

        Label emailLabel = makeEditableLabel(
                user.getEmailAddress(),
                enableEditingButton.selectedProperty(),
                NOT_EMPTY
        );
        emailLabel.textProperty()
                .addListener((observable, oldValue, newValue) -> {
            user.setEmailAddress(newValue);
        });
        infoDisplayGrid.add(emailLabel, 1, 1);

        Label phoneNumberLabel = makeEditableLabel(
                user.getMobileNumber(),
                enableEditingButton.selectedProperty(),
                NOT_EMPTY
        );
        phoneNumberLabel.textProperty()
                .addListener((observable, oldValue, newValue) -> {
            user.setMobileNumber(newValue);
        });
        infoDisplayGrid.add(phoneNumberLabel, 3, 1);

        Label addressLine1Label = makeEditableLabel(
                user.getAddressLine1(),
                enableEditingButton.selectedProperty(),
                NOT_EMPTY
        );
        addressLine1Label.textProperty()
                .addListener((observable, oldValue, newValue) -> {
            user.setAddressLine1(newValue);
        });
        infoDisplayGrid.add(addressLine1Label, 1, 2, Integer.MAX_VALUE, 1);

        Label addressLine2Label = makeEditableLabel(
                user.getAddressLine2(),
                enableEditingButton.selectedProperty(),
                s -> s
        );
        addressLine2Label.textProperty()
                .addListener((observable, oldValue, newValue) -> {
            user.setAddressLine2(newValue);
        });
        infoDisplayGrid.add(addressLine2Label, 1, 3, Integer.MAX_VALUE, 1);

        Label townLabel = makeEditableLabel(
                user.getPostTown(),
                enableEditingButton.selectedProperty(),
                NOT_EMPTY
        );
        townLabel.textProperty()
                .addListener((observable, oldValue, newValue) -> {
            user.setPostTown(newValue);
        });
        infoDisplayGrid.add(townLabel, 1, 4);

        Label postcodeLabel = makeEditableLabel(
                user.getPostcode(),
                enableEditingButton.selectedProperty(),
                NOT_EMPTY
        );
        postcodeLabel.textProperty()
                .addListener((observable, oldValue, newValue) -> {
            user.setPostcode(newValue);
        });
        infoDisplayGrid.add(postcodeLabel, 3, 4);

        Label userSinceLabel = new Label(user.getUSER_CREATION_DATE());
        infoDisplayGrid.add(userSinceLabel, 1, 5);

        setTransactionPaneData(user);

        checkStatus(user);

    }

    /**
     * Set contexts based on user status.
     * @param user the user to check.
     */
    private void checkStatus(final User user) {
        if (user instanceof Librarian) {
            librarianInfoDisplayGrid.setVisible(true);
            librarianInfoDisplayGrid.setManaged(true);

            librarianLabel.setText("Librarian");
            changeStatusButton.setText("Revoke Status");
            changeStatusButton.setOnAction(event -> revokeStatus(user));
            setLibrarian((Librarian) user);
        } else {
            librarianInfoDisplayGrid.setVisible(false);
            librarianInfoDisplayGrid.setManaged(false);

            librarianLabel.setText("Standard User");
            changeStatusButton.setText("Promote to Librarian");
            changeStatusButton.setOnAction(event -> promote(user));
        }
    }

    /**
     * Convert a Librarian to a user.
     * @param user the librarian to convert
     */
    private void revokeStatus(final User user) {
        SoundEffects.errorMessage2.play();
        User revoked = Library.revokeLibrarian(user);
        setUser(revoked);
    }

    /**
     * Convert a User to a Librarian
     * @param user the user to convert
     */
    private void promote(final User user) {
        SoundEffects.resetButton.play();
        Librarian promoted = Library.promoteToLibrarian(user);
        setUser(promoted);
    }

    /**
     * Set librarian specific contexts for the inspector.
     * @param librarian the inspected librarian.
     */
    private void setLibrarian(Librarian librarian) {
        Label staffNumberLabel = new Label(
                Integer.toString(librarian.getStaffNumber())
        );
        librarianInfoDisplayGrid.add(staffNumberLabel, 1, 0);
        Label employmentDateLabel = new Label(
                librarian.getEMPLOYMENT_DATE()
        );
        librarianInfoDisplayGrid.add(employmentDateLabel, 3, 0);
    }

    /**
     * Populates the data in the transaction pane.
     * @param user the user to analyse the transactions of/
     */
    private void setTransactionPaneData(User user) {

        ArrayList<CopyTableDataModel> data = new ArrayList<>();

        titleColumn.setCellValueFactory(
                new PropertyValueFactory<>("title")
        );
        titleColumn.setStyle( "-fx-alignment: CENTER;");
        typeColumn.setCellValueFactory(
                new PropertyValueFactory<>("type")
        );
        typeColumn.setStyle( "-fx-alignment: CENTER;");
        startDateColumn.setCellValueFactory(
                new PropertyValueFactory<>("startDate")
        );
        startDateColumn.setStyle( "-fx-alignment: CENTER;");
        dueDateColumn.setCellValueFactory(
                new PropertyValueFactory<>("dueDate")
        );
        dueDateColumn.setStyle( "-fx-alignment: CENTER;");
        returnColumn.setCellValueFactory(
                new PropertyValueFactory<>("type")
        );
        returnColumn.setStyle( "-fx-alignment: CENTER;");
        returnColumn.setCellFactory(
                createButtonColumnCallback()
        );
        overdueColumn.setCellValueFactory(
                new PropertyValueFactory<>("overdue")
        );
        overdueColumn.setStyle( "-fx-alignment: CENTER;");
        overdueColumn.setCellFactory(createOverdueColumnCallback());


        ArrayList<Transaction> activeTransactions = TransactionDatabase.getAllTransactionsUser(user.getUsername());
        getActiveTransaction(activeTransactions);

        for (Transaction t: activeTransactions) {
            data.add(new CopyTableDataModel(t));
        }
        // We are currently in chronological, we want reverse chronological
        Collections.reverse(data);

        openTransactionsTable.setItems(FXCollections.observableArrayList(data));
    }

    /**
     * The callback to add a borrow or return button to a table, used to
     * set the contents of a given cell to a button.
     * @return the callback to populate a table cell.
     */
    private Callback<TableColumn,TableCell> createButtonColumnCallback() {
        return param -> new TableCell<CopyTableDataModel, String>() {
            @Override
            public void updateItem(
                    final String item, final boolean empty) {

                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    if (item.equals("Borrowed")) {
                        Button returnResourceButton = new Button(
                                "Return Copy"
                        );
                        returnResourceButton.setOnAction(event -> {
                            returnResource(getTableView()
                                    .getItems().get(getIndex()).getCopy());
                        });
                        if (!user.equals(caller)
                                && (user instanceof Librarian)) {
                            setGraphic(returnResourceButton);
                        } else {
                            setGraphic(null);
                        }
                        setText(null);

                    } else if (item.equals("Reserved")) {
                        Button cancelReservationButton = new Button(
                                "Cancel Reservation"
                        );
                        cancelReservationButton.setOnAction(event -> {
                            cancelReservation(getTableView()
                                    .getItems().get(getIndex()).getCopy());
                        });
                        if (user.equals(caller)) {
                            setGraphic(cancelReservationButton);
                        } else {
                            setGraphic(null);
                        }
                        setText(null);
                    }
                }
            }
        };

    }

    /**
     * The callback to add a overdue alert image to a table, used to
     * set the contents of a given cell to an image.
     * @return the callback to populate a table cell.
     */
    private Callback<TableColumn,TableCell> createOverdueColumnCallback() {
        return param -> new TableCell<CopyTableDataModel, String>() {

            private final int imageSize = 20;

            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    if(Boolean.parseBoolean(item)) {
                        ImageView exclamationMark = new ImageView(
                                new Image("file:" + UIManager.EXCLAMATION_MARK)
                        );
                        exclamationMark.setFitWidth(imageSize);
                        exclamationMark.setFitHeight(imageSize);
                        setGraphic(exclamationMark);
                        setText(null);
                    }
                }
            }
        };
    }

    /**
     * Select only active transactions from an array of transactions.
     * This method is not safe and will mutate the passed array.
     * @param transactions a list of transactions
     */
    private static void getActiveTransaction(ArrayList<Transaction> transactions) {
        transactions.removeIf(transaction -> !transaction.isActive());

    }

    /**
     * Create a new label with editable characteristics, so that,
     * if the supplied boolean property evaluates true, the label
     * can be edited by clicking on it.
     * The label will be initialized to contain the supplied String
     * The string sanitiser supplied will modify the edit before
     * committing it, and any null values will invalidate the edit.
     * @param text the default text of the label.
     * @param editableProperty the boolean property for editablity
     * @param sanitiser a sanitiser to modify edits
     * @return the editable label.
     */
    private Label makeEditableLabel(
            final String text,
            final BooleanProperty editableProperty,
            final StringSanitiser sanitiser) {
        Label l = new Label(text);
        return makeEditableLabel(l, editableProperty, sanitiser);
    }

    /**
     * Give a label editable characteristics, so that, if the supplied
     * boolean property evaluates true, the label can be edited by
     * clicking on it.
     * The string sanitiser supplied will modify the edit before
     * committing it, and any null values will invalidate the edit.
     * @param label the label to give editable property to
     * @param editableProperty the boolean property for editablity
     * @param sanitiser a sanitiser to modify edits
     */
    private Label makeEditableLabel(
            final Label label,
            final BooleanProperty editableProperty,
            final StringSanitiser sanitiser) {
        Label editMe = new Label("Edit...");
        Font editMeFont = editMe.getFont();
        editMe.setFont(Font.font(
                editMeFont.getName(), FontPosture.ITALIC, editMeFont.getSize())
        );
        editMe.setTextFill(Color.BLUE);

        label.setContentDisplay(ContentDisplay.RIGHT);
        label.setOnMouseClicked(event -> {
            if (editableProperty.get() && label.getGraphic() == editMe) {
                TextField textField = new TextField();
                textField.setText(label.getText());
                textField.setFont(label.getFont());
                Platform.runLater(textField::requestFocus);
                String oldText = label.getText();
                label.setText("");
                label.setGraphic(textField);
                textField.setOnKeyPressed(e -> {
                    if (e.getCode().equals(KeyCode.ENTER)) {
                        String sanitised = sanitiser.sanitise(
                                textField.getText()
                        );
                        if (sanitised == null) {
                            label.setText(oldText);
                        } else {
                            label.setText(sanitised);
                        }
                        label.setGraphic(editMe);
                    }
                });
                textField.focusedProperty()
                        .addListener((observable, oldValue, newValue) -> {
                    if (!newValue) {
                        String sanitised = sanitiser.sanitise(
                                textField.getText()
                        );
                        if (sanitised == null) {
                            label.setText(oldText);
                        } else {
                            label.setText(sanitised);
                        }
                        label.setGraphic(editMe);
                    }
                });
            }
        });
        editableProperty.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                label.setGraphic(editMe);
            } else {
                label.setGraphic(null);
            }
        });

        return label;
    }

    /**
     * Handle the process of returning a copy to the library, if successful
     * the copy will be returned.
     * @param copy the copy to return, not null;
     */
    private void returnResource(final Copy copy) {
        float overdueCharge = copy.getOverdueCharge();
        int daysOverdue = -Math.toIntExact(copy.getDaysUntilDue());
        String user = copy.getCurrentTransaction().getUSERNAME();
        Library.returnCopy(copy);
        if (overdueCharge > 0) {
            ButtonType payNow = new ButtonType("Pay now");
            Alert alert = new Alert(Alert.AlertType.INFORMATION,
                    String.format(
                            "Resource was %d days overdue. This has added a "
                                    + "charge of £%.2f to %s's account.",
                            daysOverdue,
                            overdueCharge,
                            user
                    ), ButtonType.OK, payNow);
            alert.showAndWait();
            if (alert.getResult() == payNow) {
                try {
                    UserInspectController.launchUserInspector(
                            UserDatabase.queryUserByUsername(user), this.user
                    );
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION,
                    "Resource was returned successfully.", ButtonType.OK);
            alert.showAndWait();
        }
        refresh();
    }

    /**
     * Handle the process of cancelling a reservation,
     * if successful the copy won't we reserved.
     * @param copy the copy to cancel a reservation on, not null
     */
    private void cancelReservation(final Copy copy) {
        Alert alert;
        if (copy.isOverdue()) {
            alert = new Alert(
                    Alert.AlertType.WARNING,
                    String.format(
                            "This reservation is %d overdue. "
                                    + "You will be charged £%.2f.",
                            -copy.getDaysUntilDue(),
                            copy.getOverdueCharge()
                    ),
                    ButtonType.OK,
                    ButtonType.CANCEL);
        } else {
            alert = new Alert(
                    Alert.AlertType.CONFIRMATION,
                    "Are you sure you want to cancel your reservation for: "
                            + copy.getResource().getTitle()
                            + "?",
                    ButtonType.YES,
                    ButtonType.NO
            );
        }
        alert.showAndWait();
        if (alert.getResult() == ButtonType.YES || alert.getResult() == ButtonType.OK) {
            Library.cancelReservation(copy);
            refresh();
        }
    }

    /**
     * Launch an avatar selection page session
     * @throws IOException if there is an error loading the avatar selection
     * pane this is thrown.
     */
    private void loadAvatarSelection() throws IOException {
        AvatarSelectPageController controller = AvatarSelectPageController.launchAvatarSelectPage(user);
        controller.getStage().setOnHidden(event -> {
            String selectedPath = controller.getSelectedPath();
            if(selectedPath != null){
                setAvatarPreviewImage(selectedPath);
            }
        });
    }

    /**
     * Refresh all data in the inspector and update any changes.
     */
    private void refresh(){
        setUser(user);
    }

    /**
     * Set the stage of the scene of this controller.
     * @param stage the stage for this instance.
     */
    private void setStage(final Stage stage){
        this.stage = stage;
    }

    /**
     * Get the stage of the scene of this controller
     * @return a stage, the window the popup exists in.
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * Launch the user inspector popup, inspecting the supplied user,
     * being observed by the given caller. This opens as a popup
     * @param user the user to inspect
     * @param calling the user viewing the inspector.
     * @return the controller for the instance of the popup
     * @throws IOException if there is an error loading the
     * FXML this will be thrown.
     */
    public static UserInspectController launchUserInspector(
            final User user, final User calling) throws IOException {

        FXMLLoader loader = new FXMLLoader(
                DashboardController.class.getResource(
                        "/fxml/User_Inspect_Page.fxml"
                )
        );
        loader.load();
        Parent root = loader.getRoot();

        UserInspectController controller = loader.getController();
        controller.setUser(user);
        controller.setCaller(calling);

        Scene newScene = new Scene(root);
        Stage stage = new Stage();
        stage.setTitle(user.getUsername());
        stage.setScene(newScene);
        stage.getIcons().add(
                new Image("file:" + user.getProfileImageLocation())
        );
        stage.show();
        controller.setStage(stage);
        return controller;
    }

}
