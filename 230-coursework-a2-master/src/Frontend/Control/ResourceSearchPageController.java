package Frontend.Control;

import Backend.Databases.ResourceDatabase;
import Backend.Resources.Resource;
import Backend.Sounds.SoundEffects;
import Backend.Users.User;
import Frontend.Nodes.ResourceViewer;
import Frontend.Nodes.SortToggle;
import Frontend.Nodes.ViewerEffect;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
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
 * Controller for the search area for resources.
 * Linked to Resource_Search_Page.fxml
 * Allows users to search through all resources in the
 * library and get more information on them
 * by selecting any item in the view.
 * @author matt
 */
public class ResourceSearchPageController {

    private User user;

    /**
     * The color white as a HEX string.
     */
    private static final String WHITE = "#ffffff";
    /**
     * The color gray as a HEX string.
     */
    private static final String GRAY  = "#dddddd";

    // Search bar
    @FXML private TextField searchBar;

    // Toggles
    @FXML private CheckBox booksToggle;
    @FXML private CheckBox dvdsToggle;
    @FXML private CheckBox laptopsToggle;
    @FXML private CheckBox availableToggle;

    // Table
    private ToggleGroup sortGroup;
    @FXML private HBox resourceSortArea;
    @FXML private VBox resourceDisplayArea;

    // Results
    private HashMap<Node, Resource> results = new HashMap<>();
    private HashMap<String, Comparator<Node>> sorts = new HashMap<>();

    /**
     * JavaFX method, called when the program starts, sets
     * the initial states of all nodes.
     * Should never be called directly.
     * @throws IOException if there is an error in
     * initialing the controller this will be thrown.
     */
    @FXML
    private void initialize() throws IOException {
        createSortArea();
        resourceDisplayArea.getChildren().add(makeQueryLabel());
        booksToggle.selectedProperty()
                .addListener((observable, oldValue, newValue) -> {
            try {
                handleSearch(null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        dvdsToggle.selectedProperty()
                .addListener((observable, oldValue, newValue) -> {
            try {
                handleSearch(null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        laptopsToggle.selectedProperty()
                .addListener((observable, oldValue, newValue) -> {
            try {
                handleSearch(null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        availableToggle.selectedProperty()
                .addListener((observable, oldValue, newValue) -> {
            try {
                handleSearch(null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        handleSearch(null);
    }

    /**
     * Creates toggles for the sort area so that resources
     * can be re-arranged by given requirements.
     */
    private void createSortArea() {

        sorts.put(
                "Title",
                Comparator.comparing(n -> results.get(n).getTitle())
        );
        sorts.put("Year", Comparator.comparing(n -> results.get(n).getYear()));
        sorts.put("ID", Comparator.comparing(n -> results.get(n).getID()));
        sorts.put(
                "Type",
                Comparator.comparing(n -> results.get(n).getTypeString())
        );
        sorts.put(
                "Available",
                Comparator.comparing(
                        n -> results.get(n).getNumAvailableCopies()
                )
        );

        sortGroup = new ToggleGroup();
        for (String s : sorts.keySet()) {
            SortToggle toggle = new SortToggle(s);
            sortGroup.getToggles().add(toggle);
            resourceSortArea.getChildren().add(toggle);
        }
        sortGroup.selectedToggleProperty()
                .addListener((observable, oldValue, newValue) -> {
            listenForToggle(newValue);
        });
    }

    /**
     * Method to run when a sort-toggle has been changed,
     * update all resources to accommodate the sort requirements.
     * @param newValue the new state of the clicked toggle.
     */
    private void listenForToggle(final Toggle newValue) {
        Platform.runLater(() -> {
            if (newValue != null) {
                SortToggle toggle = (SortToggle) newValue;
                ArrayList<Node> sorted = sortNodes(
                        sorts.get(toggle.getText()),
                        toggle.getSortOrder() == SortToggle.SortOrder.ASCENDING
                );
                showResources(sorted);
            }
        });
    }

    /**
     * JavaFX method, called when the search button is clicked,
     * do a search using the query in the search bar and filtering
     * by the selected toggles.
     * @param actionEvent the event generated by the button.
     * @throws IOException if there was an error displaying
     * the results of the search this will be thrown.
     */
    @FXML
    private void handleSearch(ActionEvent actionEvent) throws IOException {
        results.clear();

        ArrayList<Resource> found = doSearch();
        ArrayList<Node> resourceViews = new ArrayList<>();
        for (Resource r: found) {
            Node resourceView = ResourceViewer.searchStyle(
                    r,
                    r.getCopyList().size(),
                    r.getNumAvailableCopies()
            );
            results.put(resourceView, r);
            processNode(resourceView);
            resourceViews.add(resourceView);
        }
        if (sortGroup.getSelectedToggle() != null) {
            sortGroup.getSelectedToggle().setSelected(false);
        }
        showResources(resourceViews);
    }

    /**
     * Perform a search using the query in the search text field
     * and the toggle filters
     * @return an array of resources that match the query.
     */
    private ArrayList<Resource> doSearch() {
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
        if (availableToggle.isSelected()) {
            ArrayList<Resource> available = new ArrayList<>();
            for (Resource r : found) {
                if (r.getNumAvailableCopies() > 0) {
                    available.add(r);
                }
            }
            found = available;
        }
        return found;
    }

    /**
     * Make a node clickable that opens a resource inspector when clicked.
     * @param node the node to make clickable
     */
    private void processNode(final Node node) {
        node.setOnMouseClicked(event -> {
            try {
                SoundEffects.createPageOpen.play();
                Stage popup = ResourceInspectController.launchResourceInspector(
                        results.get(node), user, false
                );
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
     * Refresh and update the page, along with any information that may have changed.
     */
    private void refresh() {
        try {
            handleSearch(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Display a collection of processed nodes as a set of search results in the
     * display area.
     * @param resourceViews a list of nodes to display
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
     * JavaFX method, called when the back button is pressed, closes
     * the window and opens the user dashboard.
     * @param actionEvent the action event generated by the button.
     * @throws IOException if there is an error loading the dashboard
     * this will be thrown.
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
     * Create a label for displaying prompting a user to enter a search query.
     * @return the label
     */
    private HBox makeQueryLabel(){
        return makeLabel("Enter a search term to find resources.");
    }

    /**
     * Create a label indicating that a search returned no results for
     * displaying.
     * @param searchTerm the term
     * @return the label
     */
    private HBox makeNoResultsFoundLabel(final String searchTerm) {
        return makeLabel("No results found for " + searchTerm + ".");
    }

    /**
     * Create a displayable label with the given text.
     * @param label the test for the label.
     * @return the displayable label.
     */
    private HBox makeLabel(final String label) {
        HBox labelArea = new HBox(new Label(label));
        labelArea.setAlignment(Pos.CENTER);
        labelArea.setPadding(new Insets(20));
        return labelArea;
    }

    /**
     * Sort search results using the given comparator to compare two nodes.
     * @param comparator the comparison for two nodes.
     * @param ascending if true the list will be in ascending order, false
     *                  will be in descending.
     * @return the sorted results.
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
     * Set the user viewing this search page, sets user-based
     * context.
     * @param user the user viewing the page, not null.
     */
    public void setUser(final User user) {
        this.user = user;
    }

}
