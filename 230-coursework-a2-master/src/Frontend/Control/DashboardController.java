package Frontend.Control;

import Backend.Databases.ResourceDatabase;
import Backend.Databases.TransactionDatabase;
import Backend.Library;
import Backend.Resources.Copy;
import Backend.Resources.Resource;
import Backend.Sounds.SoundEffects;
import Backend.Transactions.FinancialTransaction;
import Backend.Transactions.FineTransaction;
import Backend.Transactions.PaymentTransaction;
import Backend.Transactions.Transaction;
import Backend.Users.Librarian;
import Backend.Users.User;
import Frontend.Nodes.ResourceViewer;
import Frontend.Nodes.SortToggle;
import Frontend.Nodes.ViewerEffect;
import Frontend.Tools.FinancialTransactionTableDataModel;
import Frontend.UIManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * JavaFX controller for the User's dashboard page,
 * linked with Dashboard.fxml
 * Controls the display of user information.
 * Handles:
 * <ul>
 *     <li>Buttons for search</li>
 *     <li>Buttons for create resource</li>
 *     <li>Viewing a user's active behaviors</li>
 *     <li>Viewing a user's transaction history</li>
 *     <li>Inspecting a user's account button</li>
 * </ul>
 *
 * <br>
 * Should never need to be created manually, use the launchCreateResource
 * method to open the page.
 * @author Matt
 */
public class DashboardController {

    /**
     * Hex color white.
     */
    private static final String WHITE = "#ffffff";
    /**
     * Hex color gray.
     */
    private static final String GRAY  = "#dddddd";


    private User user;

    private UIManager manager;
    private Scene searchResources;
    private Scene searchUsers;

    // User attribs
    @FXML private ImageView avatarImage;
    @FXML private Label usernameLabel;
    @FXML private TextField accountBalanceField;

    // Dashboard attribs
    @FXML private TextField searchBar;
    @FXML private CheckBox booksToggle;
    @FXML private CheckBox dvdsToggle;
    @FXML private CheckBox laptopToggle;

    // Buttons
    @FXML private Button searchUsersButton;
    @FXML private Button viewOverdueItemsButton;
    @FXML private Button createNewResourceButton;
    
    // Tables
    @FXML private VBox borrowedResourceDisplayArea;
    @FXML private HBox borrowedResourceSortArea;
    @FXML private VBox requestedResourceDisplayArea;
    @FXML private HBox requestedResourceSortArea;
    @FXML private VBox reservedResourceDisplayArea;
    @FXML private HBox reservedResourceSortArea;
    @FXML private VBox overdueResourceDisplayArea;
    @FXML private HBox overdueResourceSortArea;

    @FXML private TableView historyTable;
    @FXML private TableColumn typeColumn;
    @FXML private TableColumn dateColumn;
    @FXML private TableColumn amountColumn;
    @FXML private TableColumn titleColumn;
    @FXML private TableColumn copyColumn;
    @FXML private TableColumn daysOverdueColumn;

    private ToggleGroup borrowedSortGroup;
    private ToggleGroup requestedSortGroup;
    private ToggleGroup reservedSortGroup;
    private ToggleGroup overdueSortGroup;

    private HashMap<String, Comparator<Node>> resourceSorts = new HashMap<>();
    private HashMap<String, Comparator<Node>> copySorts = new HashMap<>();
    private HashMap<Node, Resource> resourceResults = new HashMap<>();
    private HashMap<Node, Copy> copyResults = new HashMap<>();
    
    private ArrayList<Node> borrowedNodes = new ArrayList<>();
    private ArrayList<Node> requestedNodes = new ArrayList<>();
    private ArrayList<Node> reservedNodes = new ArrayList<>();
    private ArrayList<Node> overdueNodes = new ArrayList<>();

    private ArrayList<Copy> borrowedResults = new ArrayList<>();
    private ArrayList<Resource> requestedResults = new ArrayList<>();
    private ArrayList<Copy> reservedResults = new ArrayList<>();
    private ArrayList<Copy> overdueResults = new ArrayList<>();


    private Stage stage;

    /**
     * JavaFX method called when this controller is created, sets
     * the initial state of nodes in the system. Should never need
     * to be called directly.
     */
    @FXML
    private void initialize() {
        createSorts();
        createSearchAreas();
        booksToggle.selectedProperty().addListener((observable, oldValue, newValue) -> {
            try {
                handleSearch(null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        dvdsToggle.selectedProperty().addListener((observable, oldValue, newValue) -> {
            try {
                handleSearch(null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        laptopToggle.selectedProperty().addListener((observable, oldValue, newValue) -> {
            try {
                handleSearch(null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Set the user and all user contextual parameters of
     * the dashboard. Assumes that the user will never be null.
     * @param setUser the user who is viewing this dashboard.
     */
    private void setUser(final User setUser) {
        this.user = setUser;
        handlePermissions(user);

        usernameLabel.setText(user.getUsername());
        accountBalanceField.setText(user.getPrintableAccountBalance());
        avatarImage.setImage(new Image("file:"+user.getProfileImageLocation()));

        stage.setTitle(user.getUsername());
        try {
            handleSearch(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        fillHistory();
    }

    /**
     * Modify the dashboard to have tool access appropriate to the user's
     * permissions.
     * @param user the user of the dashboard.
     */
    private void handlePermissions(User user) {
        boolean librarian = user instanceof Librarian;
        searchUsersButton.setManaged(librarian);
        searchUsersButton.setVisible(librarian);
        viewOverdueItemsButton.setManaged(librarian);
        viewOverdueItemsButton.setVisible(librarian);
        createNewResourceButton.setManaged(librarian);
        createNewResourceButton.setVisible(librarian);
    }

    /**
     * Populate user transaction history table, assumes
     * that user has already been set and is not null.
     */
    private void fillHistory() {
        if (user == null) {
            return;
        }
        ArrayList<FinancialTransaction> userFinancialHistory = TransactionDatabase
                .getUserFinancialTransactionHistory(user.getUsername());

        if (userFinancialHistory.size() == 0) {
            historyTable.setPlaceholder(new Label(
                    "This account has no transactions yet"
            ));
            return;
        }
        ArrayList<FinancialTransactionTableDataModel> data = new ArrayList<>();

        layoutHistoryColumns();

        userFinancialHistory.sort(
                Comparator.comparing(
                        financialTransaction -> -financialTransaction.getTRANSACTION_DATE_epoch()
                )
        );
        for (FinancialTransaction fT: userFinancialHistory) {
            if (fT instanceof FineTransaction) {
                FineTransaction fine = (FineTransaction) fT;
                data.add(
                        new FinancialTransactionTableDataModel(fine)
                );
            } else if (fT instanceof PaymentTransaction) {
                PaymentTransaction payment = (PaymentTransaction) fT;
                data.add(
                        new FinancialTransactionTableDataModel(payment)
                );
            }
        }

        historyTable.setItems(FXCollections.observableArrayList(data));
    }

    /**
     * Set the layout for each column in the history table.
     */
    private void layoutHistoryColumns() {
        final String alignment = "-fx-alignment: CENTER;";

        typeColumn.setCellValueFactory(
                new PropertyValueFactory<>("transactionType")
        );
        typeColumn.setStyle(alignment);
        amountColumn.setCellValueFactory(
                new PropertyValueFactory<>("value")
        );
        amountColumn.setStyle(alignment);
        dateColumn.setCellValueFactory(
                new PropertyValueFactory<>("date")
        );
        dateColumn.setStyle(alignment);
        titleColumn.setCellValueFactory(
                new PropertyValueFactory<>("resource")
        );
        titleColumn.setStyle(alignment);
        copyColumn.setCellValueFactory(
                new PropertyValueFactory<>("copyNumber")
        );
        copyColumn.setStyle(alignment);
        daysOverdueColumn.setCellValueFactory(
                new PropertyValueFactory<>("daysOverdue")
        );
        daysOverdueColumn.setStyle(alignment);
    }

    /**
     * Create and store comparators with the name of their comparison operator.
     * Populates the copySorts and resourceSorts maps.
     */
    private void createSorts() {
        copySorts.put("Title",
                Comparator.comparing(
                        n -> copyResults.get(n).getResource().getTitle()
                )
        );
        copySorts.put("Borrow Date",
                Comparator.comparing(
                        n -> copyResults.get(n)
                                .getCurrentTransaction().startDateEpoch()
                )
        );
        copySorts.put("Reserved Date",
                Comparator.comparing(
                        n -> copyResults.get(n)
                                .getCurrentTransaction().startDateEpoch()
                )
        );
        copySorts.put("Year",
                Comparator.comparing(
                        n -> copyResults.get(n).getResource().getYear()
                )
        );
        copySorts.put("ID",
                Comparator.comparing(
                        n -> copyResults.get(n).getResource().getID()
                )
        );
        copySorts.put("Type",
                Comparator.comparing(
                        n -> copyResults.get(n)
                                .getResource().getTypeString()
                )
        );
        copySorts.put("Due Date",
                Comparator.comparing(
                        n -> copyResults.get(n).getDueDateEpoch()
                )
        );
        copySorts.put("Overdue",
                Comparator.comparing(n -> copyResults.get(n).isOverdue()
                )
        );
        copySorts.put("Charge",
                Comparator.comparing(n -> copyResults.get(n).getOverdueCharge())
        );

        resourceSorts.put("Title",
                Comparator.comparing(n -> resourceResults.get(n).getTitle())
        );
        resourceSorts.put("Year",
                Comparator.comparing(n -> resourceResults.get(n).getYear())
        );
        resourceSorts.put("ID",
                Comparator.comparing(n -> resourceResults.get(n).getID())
        );
        resourceSorts.put("Type",
                Comparator.comparing(
                        n -> resourceResults.get(n).getTypeString()
                )
        );
        resourceSorts.put("Expected Date",
                Comparator.comparing(
                        n -> resourceResults.get(n)
                                .getExpectedAvailableDateEpoch()
                )
        );
    }

    /**
     * Populate the search areas of the dashboard with sort toggle buttons.
     */
    private void createSearchAreas() {
        borrowedSortGroup = new ToggleGroup();
        String[] borrowedTerms = new String[]{"Title", "Year", "ID", "Type",
                "Borrow Date", "Due Date", "Overdue"};
        for (String s : borrowedTerms) {
            SortToggle toggle = new SortToggle(s);
            borrowedSortGroup.getToggles().add(toggle);
            borrowedResourceSortArea.getChildren().add(toggle);
        }
        borrowedSortGroup.selectedToggleProperty().addListener(
                (observable, oldValue, newValue) -> Platform.runLater(
                        () -> handleSortToggleChange(
                                newValue, borrowedNodes,
                                copySorts, borrowedResourceDisplayArea)
                )
        );

        requestedSortGroup = new ToggleGroup();
        String[] requestedTerms = new String[]{"Title", "Year", "ID",
                "Type", "Expected Date"};
        for (String s : requestedTerms) {
            SortToggle toggle = new SortToggle(s);
            requestedSortGroup.getToggles().add(toggle);
            requestedResourceSortArea.getChildren().add(toggle);
        }
        requestedSortGroup.selectedToggleProperty().addListener(
                (observable, oldValue, newValue) -> Platform.runLater(
                        () -> handleSortToggleChange(
                                newValue, requestedNodes,
                                resourceSorts, requestedResourceDisplayArea)
                )
        );

        reservedSortGroup = new ToggleGroup();
        String[] reservedTerms = new String[]{"Title", "Year", "ID", "Type",
                "Reserved Date", "Due Date", "Overdue"};
        for (String s : reservedTerms) {
            SortToggle toggle = new SortToggle(s);
            reservedSortGroup.getToggles().add(toggle);
            reservedResourceSortArea.getChildren().add(toggle);
        }
        reservedSortGroup.selectedToggleProperty().addListener(
                (observable, oldValue, newValue) -> Platform.runLater(
                        () -> handleSortToggleChange(
                                newValue, reservedNodes,
                                copySorts, reservedResourceDisplayArea)
                )
        );

        overdueSortGroup = new ToggleGroup();
        String[] overdueTerms = new String[]{"Title", "Year", "ID", "Type",
                "Borrow Date", "Due Date", "Charge"};
        for (String s : overdueTerms) {
            SortToggle toggle = new SortToggle(s);
            overdueSortGroup.getToggles().add(toggle);
            overdueResourceSortArea.getChildren().add(toggle);
        }
        overdueSortGroup.selectedToggleProperty().addListener(
                (observable, oldValue, newValue) -> Platform.runLater(
                        () -> handleSortToggleChange(
                                newValue, overdueNodes,
                                copySorts, overdueResourceDisplayArea)
                )
        );
    }

    /**
     * Action performed when a toggle button is clicked. Sort
     * the given nodes with respect to supplied
     * comparator in the order specified by the button
     * and display this in the display area
     * @param button the button that was clicked
     * @param nodes the nodes to sort
     * @param sorts the map of comparators to find an
     *              appropriate sorting mechanism in
     * @param displayArea the area to display the sorted nodes
     */
    private void handleSortToggleChange(
            final Toggle button,
            final ArrayList<Node> nodes,
            final HashMap<String, Comparator<Node>> sorts,
            final VBox displayArea) {

        if (button != null) {
            SortToggle toggle = (SortToggle) button;
            sortNodes(
                    nodes,
                    sorts.get(toggle.getText()),
                    toggle.getSortOrder() == SortToggle.SortOrder.ASCENDING);
            showResources(nodes, displayArea);
        }
    }

    /**
     * JavaFX method, called when the view account button is pressed.
     * Open the inspector for the logged in user's account.
     * @param actionEvent the event triggered by the pressing of the
     *                    button.
     * @throws IOException if there was an error loading the user's
     * inspect page an error will be thrown
     */
    @FXML
    private void handleViewAccount(final ActionEvent actionEvent) throws IOException {
        SoundEffects.newPage.play();
        UserInspectController controller = UserInspectController
                .launchUserInspector(user, user);
        Stage inspectorStage = controller.getStage();
        inspectorStage.setOnHidden(event -> refresh());
    }

    /**
     * JavaFX method, called when the logout button is pressed.
     * Log out and return to the login page
     * @param actionEvent the event triggered by the pressing of the
     *                    button.
     * @throws IOException if there was an error loading the login
     * page an error will be thrown
     */
    @FXML
    private void handleLogOut(final ActionEvent actionEvent) throws IOException {
        SoundEffects.errorMessage1.play();
        Node node = (Node) actionEvent.getSource();
        Stage stage = (Stage) (node).getScene().getWindow();
        LoginPageController.launchLoginPage(stage);
    }

    /**
     * JavaFX method, called when the search resources button is pressed.
     * Go to the resource search page
     * @param actionEvent the event triggered by the pressing of the
     *                    button.
     * @throws IOException if there was an error loading the search
     * page an error will be thrown
     */
    @FXML
    private void handleSearchResources(
            final ActionEvent actionEvent) throws IOException {

        SoundEffects.cancel.play();
        /* Cache for quick transitions */
        if (searchResources == null) {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/Resource_Search_Page.fxml")
            );
            loader.load();
            Parent resourceSearchPage = loader.getRoot();

            ResourceSearchPageController controller = loader.getController();
            controller.setUser(this.user);
            this.searchResources = new Scene(resourceSearchPage);
        }

        Node node = (Node) actionEvent.getSource();
        Stage parent = (Stage) (node).getScene().getWindow();
        parent.setTitle("Search Resources");

        parent.setScene(this.searchResources);
        parent.show();
    }

    /**
     * JavaFX method, called when the search users button is pressed.
     * Go to the user search page
     * @param actionEvent the event triggered by the pressing of the
     *                    button.
     * @throws IOException if there was an error loading the search
     * page an error will be thrown
     */
    @FXML
    private void handleSearchUsers(
            final ActionEvent actionEvent) throws IOException {
        SoundEffects.resetButton.play();

        Node node = (Node) actionEvent.getSource();
        Stage stage = (Stage) node.getScene().getWindow();

        UserSearchPageController.launchSearchUsers(stage, user);

    }

    /**
     * JavaFX method, called when the view all overdue button is pressed.
     * Go to the overdue item search page
     * @param actionEvent the event triggered by the pressing of the
     *                    button.
     * @throws IOException if there was an error loading the search
     * page an error will be thrown
     */
    @FXML
    private void handleViewOverdueItems(
            final ActionEvent actionEvent) throws IOException {
        SoundEffects.newPage.play();
        OverdueSearchPageController.launchSearchOverdue(stage, user);
    }

    /**
     * JavaFX method, called when the searchbar search button is pressed.
     * Performs a query on the contents of the search bar and context toggles
     * and populates all display areas with the results.
     * @param actionEvent the event triggered by the pressing of the
     *                    button.
     * @throws IOException if there was an error loading the search
     * results an error will be thrown
     */
    @FXML
    private void handleSearch(
            final ActionEvent actionEvent) throws IOException {
        // Clear past search
        ArrayList[] results = new ArrayList[]{borrowedResults, reservedResults,
                requestedResults, overdueResults, borrowedNodes, reservedNodes,
                requestedNodes, overdueNodes};

        for (ArrayList result: results) {
            result.clear();
        }

        // Populate results
        resourceResults.clear();
        copyResults.clear();
        doSearch();

        // Borrowed
        processBorrowed();
        processRequested();
        processReserved();
        processOverdue();
    }

    /**
     * Process and display the results of a search in the user's
     * borrowed display area.
     * @throws IOException if there was an error loading
     * resource views this will error.
     */
    private void processBorrowed() throws IOException {
        for (Copy c: borrowedResults) {
            Node resourceView = ResourceViewer.borrowedStyle(
                    c,
                    c.getCurrentTransaction().getOutputFormatTRANSACTION_DATE(),
                    c.getDueDate(),
                    c.isOverdue()
            );
            borrowedNodes.add(resourceView);
            copyResults.put(resourceView, c);
            processNode(resourceView, c.getResource(), true);
        }
        if (borrowedSortGroup.getSelectedToggle() != null) {
            borrowedSortGroup.getSelectedToggle().setSelected(false);
        }
        showResources(borrowedNodes, borrowedResourceDisplayArea);
    }

    /**
     * Process and display the results of a search in the user's
     * requested display area.
     * @throws IOException if there was an error loading
     * resource views this will error.
     */
    private void processRequested() throws IOException {
        for (Resource r: requestedResults) {
            Node resourceView = ResourceViewer.requestedStyle(
                    r, r.getExpectedAvailableDate()
            );
            Button cancelRequestButton = (Button) resourceView
                    .lookup("#cancelRequestButton");

            cancelRequestButton.setOnAction(
                    event -> cancelRequestAction(r, resourceView)
            );
            requestedNodes.add(resourceView);
            resourceResults.put(resourceView, r);
            processNode(resourceView, r, false);
        }
        if (requestedSortGroup.getSelectedToggle() != null) {
            requestedSortGroup.getSelectedToggle().setSelected(false);
        }
        System.out.println(requestedNodes);
        showResources(requestedNodes, requestedResourceDisplayArea);
    }

    /**
     * Process and display the results of a search in the user's
     * reserved display area.
     * @throws IOException if there was an error loading
     * resource views this will error.
     */
    private void processReserved() throws IOException {
        for (Copy c: reservedResults) {
            Node resourceView = ResourceViewer.reservedStyle(
                    c,
                    c.getCurrentTransaction().getOutputFormatTRANSACTION_DATE(),
                    c.getDueDate(),
                    c.isOverdue()
            );
            Button cancelReservationButton = (Button) resourceView
                    .lookup("#cancelReservationButton");
            cancelReservationButton.setOnAction(
                    event -> cancelReserveAction(c, resourceView)
            );
            reservedNodes.add(resourceView);
            copyResults.put(resourceView, c);
            processNode(resourceView, c.getResource(), true);
        }
        if (reservedSortGroup.getSelectedToggle() != null) {
            reservedSortGroup.getSelectedToggle().setSelected(false);
        }
        showResources(reservedNodes, reservedResourceDisplayArea);
    }

    /**
     * Process and display the results of a search in the user's
     * overdue display area.
     * @throws IOException if there was an error loading
     * resource views this will error.
     */
    private void processOverdue() throws IOException {
        for (Copy c: overdueResults) {
            Node resourceView = ResourceViewer.overdueStyle(
                    c,
                    c.getDueDate(),
                    c.getResource().getOverdueDayRate(),
                    c.getOverdueCharge()
            );
            overdueNodes.add(resourceView);
            copyResults.put(resourceView, c);
            processNode(resourceView, c.getResource(), true);
        }
        if (overdueSortGroup.getSelectedToggle() != null) {
            overdueSortGroup.getSelectedToggle().setSelected(false);
        }
        showResources(overdueNodes, overdueResourceDisplayArea);
    }

    /**
     * The action to be performed when the cancel request
     * button is pressed. Cancel the user's request on the
     * given item and update the viewing area
     * @param resource the resource to withdraw a request from
     * @param resourceView the node associated with this resource.
     */
    private void cancelRequestAction(
            final Resource resource, final Node resourceView) {

        if (cancelRequest(resource, user)) {
            requestedResourceDisplayArea.getChildren().remove(resourceView);
            if (requestedResourceDisplayArea.getChildren().size() == 0) {
                requestedResourceDisplayArea.getChildren().add(
                        makeNoResultsFoundLabel()
                );
            }
        }
    }

    /**
     * Start a cancel request procedure. Prompt the user, and
     * on confirmation drop the supplied user from
     * the supplied resources' request queue
     * @param resource the resource to cancel a request on
     * @param user the user to drop the request
     * @return true if the action was performed, false if the user
     * cancelled.
     */
    private boolean cancelRequest(
            final Resource resource, final User user) {
        String message = String.format(
                "Are you sure you want to cancel your request for %s?",
                resource.getTitle()
        );
        Alert alert = new Alert(
                Alert.AlertType.CONFIRMATION,
                message,
                ButtonType.YES,
                ButtonType.NO
        );
        alert.showAndWait();
        if (alert.getResult() == ButtonType.YES) {
            Library.cancelRequest(resource, user);
            return true;
        }
        return false;
    }

    /**
     * The action to be performed when the cancel reserve
     * button is pressed. Cancel the user's reservation on the
     * given item and update the viewing area
     * @param copy the resource to withdraw a reservation from
     * @param resourceView the node associated with this copy.
     */
    private void cancelReserveAction(
            final Copy copy, final Node resourceView) {
        if (cancelReservation(copy)) {
            reservedResourceDisplayArea.getChildren().remove(resourceView);
            if (reservedResourceDisplayArea.getChildren().size() == 0) {
                reservedResourceDisplayArea.getChildren().add(
                        makeNoResultsFoundLabel()
                );
            }
            refresh();
        }
    }

    /**
     * Start a cancel reservation procedure. Prompt the user, and
     * on confirmation cancel the reservation the user holds on the
     * copy.
     * @param copy the copy to cancel a reservation on
     * @return true if the action was performed, false if the user
     * cancelled.
     */
    private boolean cancelReservation(final Copy copy) {
        final Alert alert;
        final String warningMessage =  String.format(
                "This reservation is %d overdue. You will be charged £%.2f.",
                -copy.getDaysUntilDue(),
                copy.getOverdueCharge()
        );
        final String confirmMessage =  String.format(
                "Are you sure you want to cancel your reservation for: %s?",
                copy.getResource().getTitle()
        );

        if (copy.isOverdue()) {
            alert = new Alert(
                    Alert.AlertType.WARNING,
                    warningMessage,
                    ButtonType.OK,
                    ButtonType.CANCEL
            );
        } else {
            alert = new Alert(
                    Alert.AlertType.CONFIRMATION,
                    confirmMessage,
                    ButtonType.YES,
                    ButtonType.NO
            );
        }
        alert.showAndWait();
        if (alert.getResult() == ButtonType.YES
                || alert.getResult() == ButtonType.OK) {
            Library.cancelReservation(copy);
            return true;
        }
        return false;
    }

    /**
     * Perform a search query using the search bar data and
     * filter toggles. Use this to populate the results list.
     */
    private void doSearch() {
        ArrayList<Resource> found = new ArrayList<>();
        if (booksToggle.isSelected()) {
            found.addAll(ResourceDatabase.queryBook(searchBar.getText()));
        }
        if (dvdsToggle.isSelected()) {
            found.addAll(ResourceDatabase.queryDVD(searchBar.getText()));
        }
        if (laptopToggle.isSelected()) {
            found.addAll(ResourceDatabase.queryLaptop(searchBar.getText()));
        }
        borrowedResults = getBorrowedBy(found, user);
        requestedResults = getRequestedBy(found, user);
        reservedResults = getReservedBy(found, user);
        overdueResults = getOverdueBy(found, user);
    }

    /**
     * JavaFX method.
     * @param event
     */
    @FXML
    private void changedTabs(final Event event) {
        try {
            if(user != null) {
                handleSearch(null);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Filter a list of resources.
     * Return a list of copies which are:
     * <ol>
     *     <li>Copies of resources in the resource list</li>
     *     <li>Currently borrowed by the supplied user</li>
     * </ol>
     * @param resources the list of resources
     * @param user the user to filter by
     * @return the produced list of copies (which may be empty) but not null
     */
    private static ArrayList<Copy> getBorrowedBy(
            final ArrayList<Resource> resources, final User user) {
        ArrayList<Copy> copies = new ArrayList<>();
        for (Resource r: resources) {
            for (Copy c: r.getCopyList()) {
                Transaction cTransaction = c.getCurrentTransaction();
                if (!(cTransaction == null)
                        && !cTransaction.getReserved()
                        && cTransaction.getUSERNAME()
                        .equals(user.getUsername())) {

                    copies.add(c);
                }
            }
        }
        return copies;
    }

    /**
     * Filter a list of resources.
     * Return a list of resources which are:
     * <ol>
     *     <li>Copies of resources in the resource list</li>
     *     <li>Currently requested by the supplied user</li>
     * </ol>
     * @param resources the list of resources
     * @param user the user to filter by
     * @return the produced list of resources (which may be empty) but not null
     */
    private static ArrayList<Resource> getRequestedBy(
            final ArrayList<Resource> resources, final User user) {
        ArrayList<Resource> clone = new ArrayList<>(resources);
        clone.removeIf(
                resource -> !resource.getRequestQueue()
                        .contains(user.getUsername())
        );
        return clone;
    }

    /**
     * Filter a list of resources.
     * Return a list of copies which are:
     * <ol>
     *     <li>Copies of resources in the resource list</li>
     *     <li>Currently reserved by the supplied user</li>
     * </ol>
     * @param resources the list of resources
     * @param user the user to filter by
     * @return the produced list of copies (which may be empty) but not null
     */
    private static ArrayList<Copy> getReservedBy(
            final ArrayList<Resource> resources, final User user) {
        ArrayList<Copy> copies = new ArrayList<>();
        for (Resource r: resources) {
            for (Copy c: r.getCopyList()) {
                Transaction cTransaction = c.getCurrentTransaction();
                if (!(cTransaction == null)
                        && cTransaction.getReserved()
                        && cTransaction.getUSERNAME()
                        .equals(user.getUsername())) {

                    copies.add(c);
                }
            }
        }
        return copies;
    }

    /**
     * Filter a list of resources.
     * Return a list of copies which are:
     * <ol>
     *     <li>Copies of resources in the resource list</li>
     *     <li>Currently borrowed or reserved by the supplied user AND
     *     are overdue</li>
     * </ol>
     * @param resources the list of resources
     * @param user the user to filter by
     * @return the produced list of copies (which may be empty) but not null
     */
    private static ArrayList<Copy> getOverdueBy(
            final ArrayList<Resource> resources, final User user) {
        ArrayList<Copy> copies = new ArrayList<>();
        for (Resource r: resources) {
            for (Copy c: r.getCopyList()) {
                Transaction cTransaction = c.getCurrentTransaction();
                if (!(cTransaction == null)
                        && c.isOverdue()
                        && cTransaction.getUSERNAME()
                        .equals(user.getUsername())) {
                    copies.add(c);
                }
            }
        }
        return copies;
    }

    /**
     * Create a clickable resource view node using an existing resource view
     * node, the resource it represents and whether to focus the inspector
     * on copy or resource information.
     * @param node The node to be made clickable
     * @param resource the resource the node represents
     * @param copiesFocus should the inspector focus on copy information.
     */
    private void processNode(
            final Node node,
            final Resource resource,
            final boolean copiesFocus) {

        node.setOnMouseClicked(event -> {
            try {
                SoundEffects.createPageOpen.play();
                Stage popup = ResourceInspectController.launchResourceInspector(resource, user, copiesFocus);
                popup.setOnHidden(e -> {
                    refresh();
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        node.setOnMousePressed(event -> {
            node.setEffect(ViewerEffect.DEPRESSED);
        });
        node.setOnMouseReleased(event -> {
            node.setEffect(ViewerEffect.BEVEL);
        });
    }

    /**
     * Display a list of resource view nodes in a given display area, in the
     * order that they are provided
     * @param nodes the nodes to display
     * @param displayArea the area to display the nodes in
     */
    private void showResources(final ArrayList<Node> nodes,
                               final VBox displayArea) {
        displayArea.getChildren().clear();

        if(nodes.size() == 0) {
            displayArea.getChildren().add(makeNoResultsFoundLabel());
        } else {
            boolean alternate = false;
            for (Node n : nodes) {
                if (alternate) {
                    n.setStyle("-fx-background-color: " + GRAY + ";");
                } else {
                    n.setStyle("-fx-background-color: " + WHITE + ";");
                }
                displayArea.getChildren().add(n);
                alternate = !alternate;
            }
        }
    }

    /**
     * Sort an array of nodes. This method is unsafe and will modify the
     * supplied list. The array will be sorted according to the comparator
     * and can be ascending ro descending order.
     * @param nodes the nodes to sort
     * @param comparator the sorting method
     * @param ascending should the list be in order or reversed
     */
    private void sortNodes(
            final ArrayList<Node> nodes,
            final Comparator<Node> comparator,
            final boolean ascending) {

        nodes.sort(comparator);
        if (ascending) {
            Collections.reverse(nodes);
        }
    }

    /**
     * Javafx method. Handles a click on the create new resource button.
     * Launch the new resource form
     * @param actionEvent the event generated by this button click
     * @throws IOException if there is a problem loading the
     * create resource form an error is thrown
     */
    @FXML
    private void handleCreateNewResource(
            ActionEvent actionEvent) throws IOException {
        SoundEffects.imageSelect.play();
        CreateResourceController.launchCreateResourcePage();
    }

    /**
     * Get a JavaFX label indicating that no results were found in a search,
     * wrapped in a HBox for presenting.
     * @return the indicator label in a hbox.
     */
    private HBox makeNoResultsFoundLabel(){
        HBox labelArea = new HBox(new Label("No results found."));
        labelArea.setAlignment(Pos.CENTER);
        labelArea.setPadding(new Insets(20));
        return labelArea;
    }

    /**
     * Set the stage that this controller is managing.
     * @param stage the Stage which this controller manages, should
     *              never be null.
     */
    private void setStage(final Stage stage) {
        this.stage = stage;
    }

    /**
     * Refresh the scene with user context, updating any relevant information,
     * can be called after changes to ensure they're displayed.[
     */
    private void refresh() {
        System.out.println(user);
        setUser(user);
    }

    /**
     * Launch the user dashboard into the supplied stage with the
     * given user's information context.
     * @param stage the stage to use for the dashboard scene
     * @param user the user to set up the dashboard with.
     * @throws IOException an exception will be thrown if
     * there was a error loading the fxml file
     */
    public static void launchDashboard(
            final Stage stage, final User user) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                DashboardController.class.getResource("/fxml/Dashboard.fxml"));
        loader.load();
        Parent root = loader.getRoot();
        DashboardController controller = loader.getController();
        Scene newScene = new Scene(root);
        stage.setScene(newScene);

        controller.setStage(stage);
        controller.setUser(user);
        stage.show();
    }


}
