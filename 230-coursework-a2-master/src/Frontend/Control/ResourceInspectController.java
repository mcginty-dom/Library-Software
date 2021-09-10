package Frontend.Control;

import Backend.Databases.UserDatabase;
import Backend.Library;
import Backend.Resources.*;
import Backend.Sounds.SoundEffects;
import Backend.Tools.TrailerGrabber;
import Backend.Users.Librarian;
import Backend.Users.User;
import Frontend.Tools.StringSanitiser;
import Frontend.UIManager;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * Controller for the resource inspector popup
 * Linked to Resource_Inspect_Page.fxml
 * Allows a user to:
 * <ul>
 *     <li>See the details of a resource</li>
 *     <li>Read and write reviews</li>
 *     <li>Watch a trailer for is DVD</li>
 *     <li>Request a resource they're not already requesting</li>
 * </ul>
 * As well as allowing a librarian to:
 * <ul>
 *     <li>Issue copies</li>
 *     <li>Return copies</li>
 *     <li>Edit resource info</li>
 * </ul>
 * <br>
 * Should never need to be created manually, use the launchResourceInspector
 * method to open the page and get the controller
 * @author matt
 */
public class ResourceInspectController {

    /**
     * A stanitiser, will map all empty strings to null.
     */
    private static final StringSanitiser NOT_EMPTY = s -> {
        if (s.equals("")) {
            return null;
        }
        return s;
    };

    /**
     * A stanitiser, will map all not integer strings to null.
     */
    private static final StringSanitiser MUST_BE_INT = s -> {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return null;
        }
        return s;
    };

    @FXML private TitledPane infoTitle;
    @FXML private TitledPane copiesTitle;
    
    @FXML private ImageView resourceThumbnail;
    @FXML private ImageView editThumbnailIcon;
    @FXML private Label idLabel;
    @FXML private Label titleLabel;
    @FXML private ChoiceBox minimumLoanDurationChooser;
    @FXML private GridPane infoDisplayPane;
    @FXML private Button watchTrailerButton;

    @FXML private VBox subTitleSection;
    @FXML private ListView subTitleList;
    @FXML private Button addSubtitleLanguageButton;
    @FXML private Button removeSubtitleLanguageButton;

    @FXML private Label noReviewLabel;
    @FXML private ImageView star0;
    @FXML private ImageView star1;
    @FXML private ImageView star2;
    @FXML private ImageView star3;
    @FXML private ImageView star4;

    @FXML private Label copiesInLibraryLabel;
    @FXML private Label copiesAvailableLabel;
    @FXML private Label queueSizeLabel;
    @FXML private Label expectedAvailableDateLabel;

    @FXML private GridPane copiesGrid;
    private int copiesGridSize = 0;

    @FXML private Button requestResourceButton;

    @FXML private ToggleButton enableEditingButton;

    @FXML private Button addCopyButton;

    private ObservableList<Node> infoPaneDefaultState;

    private Resource resource;
    private User user;
    private Stage stage;

    /**
     * JavaFX method. Is called at the creation of the page,
     * should never need to be called manually.
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

        // Title
        makeEditableLabel(
                titleLabel, enableEditingButton.selectedProperty(), NOT_EMPTY
        );

        // Subtitles table
        subTitleList.setEditable(false);
        subTitleList.setCellFactory(TextFieldListCell.forListView());
        subTitleList.setOnEditCommit(event -> {
            ListView.EditEvent editEvent = (ListView.EditEvent) event;
            if (!editEvent.getNewValue().equals("")) {
                subTitleList.getItems().set(
                        ((ListView.EditEvent)event).getIndex(),
                        ((ListView.EditEvent)event).getNewValue()
                );
            }

        });
        enableEditingButton.selectedProperty()
                .addListener((observable, oldValue, newValue) -> {
            addSubtitleLanguageButton.setVisible(newValue);
            removeSubtitleLanguageButton.setVisible(newValue);
            subTitleList.setEditable(newValue);
        });

        // Choice box
        minimumLoanDurationChooser.setItems(FXCollections.observableArrayList(
                Resource.ONE_DAY,
                Resource.ONE_WEEK,
                Resource.TWO_WEEKS,
                Resource.FOUR_WEEKS)
        );
        enableEditingButton.selectedProperty()
                .addListener((observable, oldValue, newValue) -> {
            minimumLoanDurationChooser.setDisable(!newValue);
        });

        // Imageview
        enableEditingButton.selectedProperty()
                .addListener((observable, oldValue, newValue) -> {
            editThumbnailIcon.setVisible(newValue);
        });

        editThumbnailIcon.setOnMouseClicked(event -> {
            if (enableEditingButton.isSelected()) {
                String path = loadImage(stage);
                if (path != null) {
                    Image newImage = new Image("file:" + path);
                    resourceThumbnail.setImage(newImage);
                    try {
                        saveImage(newImage, resource.getThumbnail(), "png");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        enableEditingButton.selectedProperty()
                .addListener((observable, oldValue, newValue) -> {
            addCopyButton.setVisible(newValue);
        });

        infoPaneDefaultState = FXCollections
                .observableArrayList(infoDisplayPane.getChildren());

    }

    /**
     * JavaFX method, called when the add copy button is clicked,
     * handles adding a copy to the given resource.
     * @param actionEvent the event generated by the button
     */
    @FXML private void handleAddCopy(final ActionEvent actionEvent) {
        addCopy(resource.createAndAddCopy());
        refresh();
    }

    /**
     * JavaFX method, called when the read review button is clicked,
     * handles loading the review reader page.
     * @param actionEvent the event generated by the button
     * @throws IOException if there is an error loading the JavaFX context
     * this is thrown
     */
    @FXML private void handleReadReviews(
            final ActionEvent actionEvent) throws IOException {
        SoundEffects.resetButton.play();
        ReadReviewsController.launchReadReviews(resource);
    }

    /**
     * JavaFX method, called when the write review button is clicked
     * handles adding loading the review writer page.
     * @param actionEvent the event generated by the button
     * @throws IOException if there is an error loading the JavaFX context
     * this is thrown
     */
    @FXML private void handleWriteReview(
            final ActionEvent actionEvent) throws IOException {
        SoundEffects.imageSelect.play();
        WriteReviewController controller = WriteReviewController
                .launchWriteReview(resource, user);
        controller.getStage().setOnHidden(event -> {
            takeReview(controller.getReview());
        });
    }

    /**
     * Add a review to a resource. This performs no verifications, and assumes the
     * review element is not null.
     * @param element the review to add.
     */
    private void takeReview(final Element element) {
        if (element != null) {
            resource.getReview().addReview(element);
            refresh();
        }
    }

    /**
     * JavaFX method, called when the request resource button is clicked,
     * handles adding the user to the resources request queue.
     * @param actionEvent actionEvent the event generated by the button
     */
    @FXML private void handleRequestResource(final ActionEvent actionEvent) {
        SoundEffects.newPage.play();
        Library.requestResource(user, resource);
        requestResourceButton.setDisable(resource.isRequestedBy(user));
        refresh();
    }

    /**
     * JavaFX method, called when the exit button is clicked,
     * handles closing the page.
     * @param actionEvent actionEvent the event generated by the button
     */
    @FXML private void handleExitButton(final ActionEvent actionEvent) {
        SoundEffects.errorMessage1.play();
        stage.fireEvent(
                new WindowEvent(
                        stage,
                        WindowEvent.WINDOW_CLOSE_REQUEST
                )
        );
    }

    /**
     * JavaFX method, called when the enable editing toggle is clicked,
     * handles adding and removing edit permissions.
     * @param actionEvent actionEvent the event generated by the button
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
     * Change the focus from the info titled pane to the copies titled pane.
     * If true will focus on copies,
     * If false will remove the focus from copies
     * @param focusOnCopies should the copies pane be the most visible in the
     *                      accordion.
     */
    private void focusOnCopies(final boolean focusOnCopies) {
        Platform.runLater(() -> copiesTitle.setExpanded(focusOnCopies));
    }

    /**
     * Sets the resource and all resource context based content in this page.
     * Assumes the resource is not null.
     * Populates all relevant fields
     * @param resource the resource this inspector is referencing.
     */
    private void setResource(final Resource resource) {
        this.resource = resource;
        requestResourceButton.setDisable(resource.isRequestedBy(user));
        infoDisplayPane.getChildren()
                .removeIf(node -> !infoPaneDefaultState.contains(node));

        titleLabel.setText(resource.getTitle());
        titleLabel.textProperty()
                .addListener((observable, oldValue, newValue) -> {
            resource.setTitle(newValue);
        });
        resourceThumbnail.setImage(
                new Image("file:" + resource.getThumbnail())
        );
        idLabel.setText("ID #" + resource.getID());
        setRating(resource.getReview().getRating());
        minimumLoanDurationChooser.setValue(resource.getMinLoanDuration());
        minimumLoanDurationChooser.valueProperty()
                .addListener((observable, oldValue, newValue) -> {
            resource.setMinLoanDuration((String) newValue);
        });

        updateCopyInfo();

        copiesGrid.getChildren().clear();
        copiesGridSize = 0;
        for (int i = 0; i < resource.getCopyList().size(); i++) {
            addCopy(resource.getCopyList().get(i));
        }

        if (resource instanceof Book) {
            setBook((Book) resource);
        } else if (resource instanceof DVD) {
            setDVD((DVD) resource);
        } else if (resource instanceof Laptop) {
            setLaptop((Laptop) resource);
        }
    }

    /**
     * Sets all additional fields relating to books in the inspector,
     * and removes any fields unrelated to them.
     * @param book the book to inspect.
     */
    private void setBook(final Book book) {
        subTitleSection.setManaged(false);
        subTitleSection.setVisible(false);
        watchTrailerButton.setManaged(false);
        watchTrailerButton.setVisible(false);

        int row = 0;
        int column = 0;

        infoDisplayPane.add(new Label("Author:"), column++, row);
        Label authorLabel = makeEditableLabel(
                book.getAuthor(),
                enableEditingButton.selectedProperty(),
                NOT_EMPTY
        );
        authorLabel.textProperty()
                .addListener((observable, oldValue, newValue) -> {
            book.setAuthor(newValue);
        });
        infoDisplayPane.add(authorLabel, column++, row);

        infoDisplayPane.add(new Label("Publisher:"), column++, row);
        Label publisherLabel = makeEditableLabel(
                book.getPublisher(),
                enableEditingButton.selectedProperty(),
                NOT_EMPTY
        );
        publisherLabel.textProperty()
                .addListener((observable, oldValue, newValue) -> {
            book.setPublisher(newValue);
        });
        infoDisplayPane.add(publisherLabel, column++, row++);

        column = 0;

        infoDisplayPane.add(new Label("Language:"), column++, row);
        Label languageLabel = makeEditableLabel(
                book.getLanguage(),
                enableEditingButton.selectedProperty(),
                s -> s
        );
        languageLabel.textProperty()
                .addListener((observable, oldValue, newValue) -> {
            book.setLanguage(newValue);
        });
        infoDisplayPane.add(languageLabel, column++, row);

        infoDisplayPane.add(new Label("Genre:"), column++, row);
        Label genreLabel = makeEditableLabel(
                book.getGenre(), enableEditingButton.selectedProperty(), s -> s
        );
        genreLabel.textProperty()
                .addListener((observable, oldValue, newValue) -> {
            book.setGenre(newValue);
        });
        infoDisplayPane.add(genreLabel, column++, row++);

        column = 0;

        infoDisplayPane.add(new Label("ISBN:"), column++, row);
        Label isbnLabel = makeEditableLabel(
                book.getISBN(),
                enableEditingButton.selectedProperty(),
                s -> s
        );
        isbnLabel.textProperty().addListener(
                (observable, oldValue, newValue) -> {
            book.setISBN(newValue);
        });
        infoDisplayPane.add(isbnLabel, column, row);
    }

    /**
     * Sets all additional fields relating to dvds in the inspector,
     * and removes any fields unrelated to them.
     * @param dvd the dvd to inspect.
     */
    public void setDVD(final DVD dvd) {
        subTitleSection.setManaged(true);
        subTitleSection.setVisible(true);
        watchTrailerButton.setManaged(true);
        watchTrailerButton.setVisible(true);

        int row = 0;
        int column = 0;

        infoDisplayPane.add(new Label("Director:"), column++, row);
        Label directorLabel = makeEditableLabel(
                dvd.getDirector(),
                enableEditingButton.selectedProperty(),
                NOT_EMPTY
        );
        directorLabel.textProperty()
                .addListener((observable, oldValue, newValue) -> {
            dvd.setDirector(newValue);
        });
        infoDisplayPane.add(directorLabel, column++, row);

        infoDisplayPane.add(new Label("Runtime:"), column++, row);
        Label runTimeLabel = makeEditableLabel(
                Integer.toString(dvd.getRuntime()),
                enableEditingButton.selectedProperty(),
                MUST_BE_INT
        );
        runTimeLabel.textProperty()
                .addListener((observable, oldValue, newValue) -> {
            dvd.setRuntime(Integer.parseInt(newValue));
        });
        infoDisplayPane.add(runTimeLabel, column++, row);

        column = 0;
        row++;

        infoDisplayPane.add(new Label("Language:"), column++, row);
        Label languageLabel = makeEditableLabel(
                dvd.getLanguage(),
                enableEditingButton.selectedProperty(),
                s -> s
        );
        languageLabel.textProperty()
                .addListener((observable, oldValue, newValue) -> {
            dvd.setLanguage(newValue);
        });
        infoDisplayPane.add(languageLabel, column++, row);

        createSubtitleArea(dvd);
        initTrailersButton(dvd);
    }

    /**
     * Populates the subtitle area for a given dvd.
     * @param dvd the dvd to read.
     */
    private void createSubtitleArea(final DVD dvd) {
        subTitleList.setItems(
                FXCollections.observableArrayList(
                        Arrays.asList(dvd.getSubtitles())
                )
        );

        addSubtitleLanguageButton.setOnAction(event -> {
            subTitleList.getItems().add(
                    subTitleList.getItems().size(), "New Language"
            );
            subTitleList.edit(subTitleList.getItems().size() - 1);
        });

        removeSubtitleLanguageButton.setOnAction(event -> {
            int selectedIndex = subTitleList
                    .getSelectionModel().getSelectedIndex();
            if (selectedIndex < 0) {
                return;
            }
            subTitleList.getItems().remove(selectedIndex, selectedIndex + 1);
        });
        subTitleList.getItems().addListener((ListChangeListener) c -> {
            String[] newSubtitles = new String[c.getList().size()];
            for (int i = 0; i < c.getList().size(); i++) {
                newSubtitles[i] = c.getList().get(i).toString();
            }
            dvd.setSubtitles(newSubtitles);
        });
    }

    /**
     * Find a trailer and link it to the play trailer button,
     * the button will be deactivated if no trailer could be found.
     * @param dvd the dvd to find a trailer for.
     */
    private void initTrailersButton(final DVD dvd) {
        final int trailerWidth = 640;
        final int trailerHeight = 480;

        String trailerEmbed = TrailerGrabber.getEmbeddedTrailer(dvd.getTitle());
        if (trailerEmbed == null) {
            watchTrailerButton.setDisable(true);
        } else {
            watchTrailerButton.setOnAction(event -> {
                try {
                    TrailerPopupController.launchTrailerPopup(
                            dvd.getTitle(),
                            trailerEmbed,
                            trailerWidth,
                            trailerHeight
                    );
                } catch (IOException e) {
                    watchTrailerButton.setDisable(true);
                }
            });
        }
    }

    /**
     * Sets all additional fields relating to laptops in the inspector,
     * and removes any fields unrelated to them.
     * @param laptop the laptop to inspect.
     */
    public void setLaptop(Laptop laptop) {

        subTitleSection.setManaged(false);
        subTitleSection.setVisible(false);
        watchTrailerButton.setManaged(false);
        watchTrailerButton.setVisible(false);

        int row = 0;
        int column = 0;

        infoDisplayPane.add(new Label("Manufacturer:"), column++, row);
        Label manufacturerLabel = makeEditableLabel(
                laptop.getManufacturer(),
                enableEditingButton.selectedProperty(),
                NOT_EMPTY
        );
        manufacturerLabel.textProperty()
                .addListener((observable, oldValue, newValue) -> {
            laptop.setManufacturer(newValue);
        });
        infoDisplayPane.add(manufacturerLabel, column++, row);

        infoDisplayPane.add(new Label("Model:"), column++, row);
        Label modelLabel = makeEditableLabel(
                laptop.getModel(),
                enableEditingButton.selectedProperty(),
                NOT_EMPTY
        );
        modelLabel.textProperty()
                .addListener((observable, oldValue, newValue) -> {
            laptop.setModel(newValue);
        });
        infoDisplayPane.add(modelLabel, column++, row);

        column = 0;
        row++;

        infoDisplayPane.add(new Label("OS:"), column++, row);
        Label osLabel = makeEditableLabel(
                laptop.getOS(),
                enableEditingButton.selectedProperty(),
                NOT_EMPTY
        );
        osLabel.textProperty()
                .addListener((observable, oldValue, newValue) -> {
            laptop.setOS(newValue);
        });
        infoDisplayPane.add(osLabel, column++, row);
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
        makeEditableLabel(l, editableProperty, sanitiser);
        return l;
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
    private void makeEditableLabel(
            final Label label,
            final BooleanProperty editableProperty,
            final StringSanitiser sanitiser) {

        Label editMe = new Label("Edit...");
        Font editMeFont = editMe.getFont();
        editMe.setFont(Font.font(
                editMeFont.getName(),
                FontPosture.ITALIC,
                editMeFont.getSize()
        ));
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
    }

    /**
     * Populate the rating area with the rating for this resource.
     * @param rating the average rating of the resource
     */
    private void setRating(final float rating) {
        ImageView[] stars = new ImageView[]{star0, star1, star2, star3, star4};

        if (rating < 0) {
            noReviewLabel.setVisible(true);
            noReviewLabel.setManaged(true);
            for (ImageView star: stars) {
                star.setVisible(false);
                star.setManaged(false);
            }
        } else {
            noReviewLabel.setVisible(false);
            noReviewLabel.setManaged(false);

            float roundRating = Math.round(rating * 2) / 2f;
            for (ImageView star: stars) {
                star.setVisible(true);
                star.setManaged(true);

                if (roundRating - 1 >= 0) {
                    star.setImage(new Image(
                            "file:" + UIManager.FULL_STAR_ICON
                    ));
                } else if (roundRating - (1 / 2f) >= 0) {
                    star.setImage(new Image(
                            "file:" + UIManager.HALF_STAR_ICON
                    ));
                } else {
                    star.setImage(new Image(
                            "file:" + UIManager.EMPTY_STAR_ICON
                    ));
                }
                roundRating--;
            }
        }
    }

    /**
     * Add the information for a copy of this resource to the
     * copy view area.
     * @param c a copy of the inspected resource, not null.
     */
    private void addCopy(final Copy c) {
        final int identBoxSpacing = 10;
        final int infoBoxSpacing = 5;
        final int overdueBoxSpacing = 5;
        final int imageSize = 20;

        HBox identBox = new HBox();
        identBox.setSpacing(identBoxSpacing);
        identBox.setAlignment(Pos.CENTER_LEFT);
        ImageView trashBin = new ImageView(new Image(
                "file:" + UIManager.TRASH_BIN_ICON
        ));
        trashBin.setFitWidth(imageSize);
        trashBin.setFitHeight(imageSize);
        Button deleteButton = new Button("", trashBin);
        deleteButton.setDisable(!c.isAvailable() || c.isReserved());
        deleteButton.setVisible(enableEditingButton.isSelected());

        enableEditingButton.selectedProperty()
                .addListener((observable, oldValue, newValue) -> {
            deleteButton.setVisible(newValue);
        });

        Label copyID = new Label("Copy #" + c.getID());
        deleteButton.setOnAction(event -> handleRequestDeleteCopy(c));
        deleteButton.setDisable(!c.isAvailable());
        identBox.getChildren().addAll(deleteButton, copyID);
        copiesGrid.add(identBox, 0, copiesGridSize);

        HBox infoBox = new HBox();
        infoBox.setAlignment(Pos.CENTER_LEFT);
        infoBox.setSpacing(infoBoxSpacing);
        CheckBox availableCheckbox = new CheckBox("Available");
        availableCheckbox.setSelected(c.isAvailable());
        availableCheckbox.setDisable(true);
        CheckBox reservedCheckbox = new CheckBox("Reserved");
        reservedCheckbox.setSelected(c.isReserved());
        reservedCheckbox.setDisable(true);
        Button viewCopyHistory = new Button("View Copy History");
        viewCopyHistory.setOnAction(event -> {
            viewCopyHistory(c);
        });


        Button issueReturnButton = createIssueReturnButton(c);

        if (!(user instanceof Librarian)) {
            issueReturnButton.setVisible(false);
            issueReturnButton.setManaged(false);
            viewCopyHistory.setVisible(false);
            viewCopyHistory.setManaged(false);
        }


        HBox overdueBox = new HBox();
        overdueBox.setSpacing(overdueBoxSpacing);
        overdueBox.setAlignment(Pos.CENTER_LEFT);
        ImageView exclamationMark = new ImageView(
                new Image("file:" + UIManager.EXCLAMATION_MARK)
        );
        exclamationMark.setFitHeight(imageSize);
        exclamationMark.setFitWidth(imageSize);
        Label overdueLabel = new Label("Overdue");
        overdueBox.getChildren().addAll(exclamationMark, overdueLabel);
        overdueBox.setVisible(c.isOverdue());

        infoBox.getChildren().addAll(
                availableCheckbox,
                reservedCheckbox,
                viewCopyHistory,
                issueReturnButton,
                overdueBox
        );
        copiesGrid.add(infoBox, 1, copiesGridSize++);
    }


    /**
     * Launch the popup to inspect this copy's borrow history.
     * @param copy the copy to inspect the history of, not null
     */
    private void viewCopyHistory(final Copy copy) {
        try {
            SoundEffects.cancel.play();
            CopyHistoryController copyHistoryController = CopyHistoryController.launchViewHistory(user, copy);
            copyHistoryController.getStage().setOnHidden(e -> refresh());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create a button to handle either the issuing or returning of a copy
     * to/from the library (based on the specific status of the copy
     * provided).
     * @param copy the copy to read
     * @return a button to handle the copy.
     */
    private Button createIssueReturnButton(final Copy copy) {
        Button issueReturnButton;
        if (copy.isAvailable() || copy.isReserved()) {
            issueReturnButton = new Button("Issue Copy");
            issueReturnButton.setOnMouseClicked(event -> {
                handleIssueCopy(event, issueReturnButton, copy);
            });
        } else {
            issueReturnButton = new Button("Return Copy");
            issueReturnButton.setOnAction(event -> {
                handleReturnCopy(copy);
            });
            issueReturnButton.setDisable(
                    copy.getCurrentTransaction()
                            .getUSERNAME().equals(user.getUsername())
            );
        }
        return issueReturnButton;
    }

    /**
     * Handle the process of returning a copy of a resource to a library.
     * Will prompt user where appropriate to confirm.
     * @param copy the copy to return to the library, not null.
     */
    private void handleReturnCopy(final Copy copy) {
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
                            user),
                    ButtonType.OK, payNow);
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
            Alert alert = new Alert(
                    Alert.AlertType.INFORMATION,
                    "Resource was returned successfully.",
                    ButtonType.OK
            );
            alert.showAndWait();
        }
        refresh();
    }

    /**
     * Handle the process of issuing a resource to a user, initiated
     * by clicking an issue copy button.
     * @param event the Mouse Event that instigates this process
     * @param issueReturnButton the button that was clicked
     * @param copy the copy to issue.
     */
    private void handleIssueCopy(
            final MouseEvent event,
            final Button issueReturnButton,
            final Copy copy) {

        if (!issueReturnButton.isDisabled()) {
            try {
                QuickUserSearchController controller = QuickUserSearchController.launchQuickSearch(
                        copy.getReservedFor(), user, (int) event.getScreenX(), (int) event.getScreenY()
                );
                Stage popup = controller.getStage();
                popup.setOnHidden(e -> {
                    if (controller.getSelectedUser() != null) {
                        Library.issueResource(
                                controller.getSelectedUser(), copy
                        );
                        refresh();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Handles the process of deleting a copy of a resource from the library.
     * @param copy the copy to be deleted, not null.
     */
    private void handleRequestDeleteCopy(final Copy copy) {
        Alert alert = new Alert(
                Alert.AlertType.WARNING,
                "Are you sure you want to delete this copy? "
                        + "This process cannot be undone!",
                ButtonType.YES,
                ButtonType.CANCEL);
        alert.showAndWait();

        if (alert.getResult() != ButtonType.CANCEL) {
            resource.removeCopy(copy);

            updateCopyInfo();

            copiesGrid.getChildren().clear();
            copiesGridSize = 0;
            for (int i = 0; i < resource.getCopyList().size(); i++) {
                addCopy(resource.getCopyList().get(i));
            }
        }
    }

    /**
     * Refresh the information in the copy view area of the inspector,
     * updating to any changes that may have taken place.
     */
    private void updateCopyInfo() {
        copiesInLibraryLabel.setText(
                Integer.toString(resource.getCopyList().size())
        );
        copiesAvailableLabel.setText(
                Integer.toString(resource.getNumAvailableCopies())
        );
        queueSizeLabel.setText(
                Integer.toString(resource.getRequestQueue().size())
        );
        String expectedAvailableDate = resource.getExpectedAvailableDate();
        if (expectedAvailableDate != null) {
            expectedAvailableDateLabel.setText(expectedAvailableDate);
        } else {
            expectedAvailableDateLabel.setText("Never");
        }
    }

    /**
     * Refresh all information in the inspector.
     */
    private void refresh() {
        setResource(resource);
    }

    /**
     * Set the user viewing this inspector. Required for user-specific
     * context items.
     * @param user the user viewing this inspector.
     */
    private void setUser(final User user) {
        this.user = user;
        setPermissions(user);
    }

    /**
     * Sets the availability of controls based on the
     * access rights of the given user.
     * @param user the user to analyse.
     */
    public void setPermissions(final User user) {
        boolean librarian = user instanceof Librarian;
        enableEditingButton.setVisible(librarian);
        enableEditingButton.setManaged(librarian);
    }

    /**
     * Set the window that this inspector exists in.
     * @param stage the window this inspector exists in.
     */
    private void setStage(final Stage stage) {
        this.stage = stage;
    }

    /**
     * Handle loading an image from a file on the user's external
     * file system and getting a path to the file.
     * @param stage the window to launch a filechooser from.
     * @return a path to an image, or null if no image was selected.
     */
    private String loadImage(final Stage stage) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Image files", "*.png", "*.jpg", "*.jpeg", "*.gif");
        fileChooser.getExtensionFilters().add(filter);

        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            return file.getPath();
        }
        return null;
    }

    /**
     * Handle saving a JavaFX Image to file at a given path in a given format.
     * @param image The image to save
     * @param thumbnailPath the path to save the thumbnail at.
     * @param format the format, a string which will resemble a file extension.
     * @throws IOException if there is an error saving the file this will
     * be thrown.
     */
    private void saveImage(
            final Image image,
            final String thumbnailPath,
            final String format) throws IOException {
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        File outFile = new File(thumbnailPath);
        ImageIO.write(bufferedImage, format, outFile);
    }

    /**
     * Launch the resource inspector as a popup, inspecting
     * the given resource with the supplied observer.
     * @param resource the resource to inspect.
     * @param user the user observing the inspector.
     * @param focusOnCopies if this is true the copies tab will be visible on popup,
     *                      otherwise the info tab will be shown.
     * @return the stage that the popup takes place in.
     * @throws IOException is thrown if there was an error loading the
     * FXML for this page.
     */
    public static Stage launchResourceInspector(
            final Resource resource,
            final User user,
            final boolean focusOnCopies) throws IOException {

        FXMLLoader loader = new FXMLLoader(
                DashboardController.class.getResource(
                        "/fxml/Resource_Inspect_Page.fxml"
                )
        );
        loader.load();
        Parent root = loader.getRoot();

        ResourceInspectController controller = loader.getController();
        controller.setUser(user);
        controller.setResource(resource);

        Scene newScene = new Scene(root);
        Stage stage = new Stage();
        stage.setTitle(resource.getTitle());
        stage.setScene(newScene);
        stage.getIcons().add(new Image("file:" + resource.getThumbnail()));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();

        controller.setStage(stage);
        controller.focusOnCopies(focusOnCopies);

        return stage;
    }


}
