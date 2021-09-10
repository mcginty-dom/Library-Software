package Frontend.Control;

import Backend.Databases.ResourceDatabase;
import Backend.Resources.Copy;
import Backend.Resources.Resource;
import Backend.Sounds.SoundEffects;
import Backend.Users.User;
import Frontend.Nodes.ResourceViewer;
import Frontend.Nodes.SortToggle;
import Frontend.Nodes.ViewerEffect;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Controller for the search area for overdue copies.
 * Linked to Overdue_Search_Page.fxml
 * Allows librarians to search through all resources that are
 * overdue.
 * <br>
 * Should never need to be created manually, use the launchSearchOverdue
 * method to open the search page
 * @author matt
 */
public class OverdueSearchPageController {

    private static final String WHITE = "#ffffff";
    private static final String GRAY  = "#dddddd";

    // Search bar
    @FXML private TextField searchBar;

    // Toggles
    @FXML private CheckBox booksToggle;
    @FXML private CheckBox dvdsToggle;
    @FXML private CheckBox laptopsToggle;

    // Table
    private ToggleGroup sortGroup;
    @FXML private HBox resourceSortArea;
    @FXML private VBox resourceDisplayArea;

    // Results
    private HashMap<Node, Copy> results = new HashMap<>();
    private HashMap<String, Comparator<Node>> sorts = new HashMap<>();

    private User user;

    /**
     * JavaFX method. Called when the page is first opened, sets the initial
     * state of all nodes. Should never need to be called manually
     * @throws IOException if these is an error handling search terms
     * this will be thrown
     */
    @FXML
    private void initialize() throws IOException {
        fillSorts();
        createSortArea();
        resourceDisplayArea.getChildren().add(makeQueryLabel());
        handleSearch(null);
    }

    /**
     * Populate the sorts map with comparators linked to the name of their
     * sorting parameter.
     */
    private void fillSorts() {
        sorts.put("Title", Comparator.comparing(
                n -> results.get(n).getResource().getTitle()
        ));
        sorts.put("Year", Comparator.comparing(
                n -> results.get(n).getResource().getYear()
        ));
        sorts.put("ID", Comparator.comparing(
                n -> results.get(n).getResource().getID()
        ));
        sorts.put("Type", Comparator.comparing(
                n -> results.get(n).getResource().getTypeString()
        ));
        sorts.put("Charge", Comparator.comparing(
                n -> results.get(n).getOverdueCharge()
        ));
        sorts.put("Due Date", Comparator.comparing(
                n -> results.get(n).getDueDateEpoch()
        ));
    }

    /**
     * Adds toggles to the sort area, to allow sorting
     * of the search results.
     */
    private void createSortArea() {

        sortGroup = new ToggleGroup();
        for (String s : sorts.keySet()) {
            SortToggle toggle = new SortToggle(s);
            sortGroup.getToggles().add(toggle);
            resourceSortArea.getChildren().add(toggle);
        }
        sortGroup.selectedToggleProperty().addListener(
                (observable, oldValue, newValue) ->
                        Platform.runLater(() -> doSort(newValue)));

    }

    /**
     * Action to perform when a toggle button is pressed.
     * Performs the sort represented by the on the current search results and
     * displays the results.
     * @param toggle the sortToggle that was clicked.
     */
    private void doSort(final Toggle toggle) {
        if (toggle != null) {
            SortToggle sortToggle = (SortToggle) toggle;
            ArrayList<Node> sorted = sortNodes(
                    sorts.get(sortToggle.getText()),
                    sortToggle.getSortOrder() == SortToggle.SortOrder.ASCENDING);
            showResources(sorted);
        }
    }

    /**
     * JavaFX method. Called when the search button is pressed, performs
     * a search using the search bar term and filters
     * @param actionEvent the event created by hitting this button
     * @throws IOException if there is an error loading the
     * resource displays this error will be thrown.
     */
    @FXML
    private void handleSearch(
            final ActionEvent actionEvent) throws IOException {

        results.clear();

        ArrayList<Copy> found = doSearch();
        ArrayList<Node> resourceViews = new ArrayList<>();
        for (Copy c: found) {
            Node resourceView = ResourceViewer.overdueOverviewStyle(
                    c,
                    c.getDueDate(),
                    c.getOverdueCharge(),
                    (int) -c.getDaysUntilDue(),
                    c.getCurrentTransaction().getUSERNAME(),
                    user);
            results.put(resourceView, c);
            processNode(resourceView);
            resourceViews.add(resourceView);
        }
        if (sortGroup.getSelectedToggle() != null) {
            sortGroup.getSelectedToggle().setSelected(false);
        }
        showResources(resourceViews);
    }

    /**
     * Perform a search, using the query in the search bar
     * and the toggle information. All results will be:
     * <ol>
     *     <li>Copies that match the query</li>
     *     <li>Copies that are currently overdue</li>
     * </ol>
     * @return a list of copies matching these parameters.
     */
    private ArrayList<Copy> doSearch() {
        ArrayList<Resource> found = new ArrayList<>();
        if (booksToggle.isSelected()) {
            found.addAll(ResourceDatabase.queryBook(searchBar.getText()));
        }
        if (dvdsToggle.isSelected()) {
            found.addAll(ResourceDatabase.queryDVD(searchBar.getText()));
        }
        if (laptopsToggle.isSelected()) {
            found.addAll(ResourceDatabase.queryLaptop(searchBar.getText()));
        }
        ArrayList<Copy> copies = new ArrayList<>();
        for (Resource resource: found) {
            for (Copy c: resource.getCopyList()) {
                if (c.isOverdue()) {
                    copies.add(c);
                }
            }
        }

        return copies;
    }

    /**
     * Make a node clickable.
     * @param node the node to make clickable
     */
    private void processNode(final Node node) {
        node.setOnMouseClicked(event -> {
            try {
                SoundEffects.createPageOpen.play();
                Stage popup = ResourceInspectController.launchResourceInspector(
                        results.get(node).getResource(),
                        user,
                        true);
                popup.setOnHidden(e -> refresh());
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
     * Refresh the page and update any changes.
     */
    private void refresh() {
        try {
            handleSearch(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Display a list of nodes in the display area, in order highest to lowest.
     * @param resourceViews the nodes to display
     */
    private void showResources(final ArrayList<Node> resourceViews) {
        resourceDisplayArea.getChildren().clear();

        if (resourceViews.size() == 0) {
            resourceDisplayArea.getChildren().add(
                    makeNoResultsFoundLabel(searchBar.getText())
            );
        } else {
            boolean alternate = false;
            for (Node n : resourceViews) {
                if (alternate) {
                    n.setStyle("-fx-background-color: " + GRAY + ";");
                } else {
                    n.setStyle("-fx-background-color: " + WHITE + ";");
                }
                resourceDisplayArea.getChildren().add(n);
                alternate = !alternate;
            }
        }

    }

    /**
     * JavaFX method, called with a back button click. Returns to the dashboard.
     * @param actionEvent the action event created by this button click.
     * @throws IOException if there is an error loading the dashboard this will be thrown
     */
    @FXML
    private void handleBackButton(
            final ActionEvent actionEvent) throws IOException {
        SoundEffects.errorMessage1.play();

        Node node = (Node) actionEvent.getSource();
        Stage stage = (Stage) (node).getScene().getWindow();
        DashboardController.launchDashboard(stage, user);
    }

    /**
     * Create a label prompting a query
     * @return the label
     */
    private HBox makeQueryLabel() {
        return makeLabel("Enter a search term to find resources.");
    }

    /**
     * Make a query indicating a search produced no results
     * @param searchTerm the search term
     * @return the label
     */
    private HBox makeNoResultsFoundLabel(String searchTerm) {
        return makeLabel("No results found for " + searchTerm + ".");
    }

    /**
     * Create a label centered in a JavaFX hbox with the given text.
     * @param label the text
     * @return the label
     */
    private HBox makeLabel(final String label) {
        final int insets = 20;

        HBox labelArea = new HBox(new Label(label));
        labelArea.setAlignment(Pos.CENTER);
        labelArea.setPadding(new Insets(insets));
        return labelArea;
    }

    /**
     * Sort all search results using the supplied comparator.
     * @param comparator the method of comparing two nodes
     * @param ascending should the results be in ascending or descending order
     * @return the sorted nodes
     */
    private ArrayList<Node> sortNodes(
            final Comparator<Node> comparator, final boolean ascending) {

        ArrayList<Node> nodes = new ArrayList<>(results.keySet());
        nodes.sort(comparator);
        if (ascending) {
            Collections.reverse(nodes);
        }
        return nodes;
    }

    /**
     * Set the user viewing the search page and update all contexts
     * based on them.
     * @param user the user viewing the page. Should not be null.
     */
    public void setUser(User user) {
        this.user = user;
        refresh();
    }

    /**
     * Launch the search page into the given window being viewed by the
     * given user.
     * This will replace anything currently in the window's stage.
     * @param stage the window to launch into, not null
     * @param user the user viewing the search page, not null
     * @throws IOException if there is an error loading the data for the
     * page throw this error.
     */
    public static void launchSearchOverdue(
            final Stage stage, final User user) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                DashboardController.class.getResource(
                        "/fxml/Overdue_Search_Page.fxml"
                )
        );
        loader.load();
        Parent root = loader.getRoot();
        OverdueSearchPageController controller = loader.getController();

        Scene newScene = new Scene(root);
        stage.setScene(newScene);

        controller.setUser(user);
        stage.show();
        stage.setTitle("Search Overdue Items");
    }

}
