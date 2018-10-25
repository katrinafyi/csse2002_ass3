package game.view;

import csse2002.block.world.WorldMapFormatException;
import csse2002.block.world.WorldMapInconsistentException;
import game.controller.BlockWorldController;
import game.controller.MessageController;
import game.model.ReadOnlyBlockWorldModel;
import game.model.events.BaseBlockWorldEvent;
import game.model.events.WorldMapLoadedEvent;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * Class for the game's menu bar. Contains a file menu with load and save
 * options.
 */
public class GameMenuBar extends MenuBar {
    /** Parent stage. Used for focusing the file dialogs. */
    private final Stage mainStage;
    /** Game controller to interact with. */
    private final BlockWorldController controller;
    /** Message controller for handling messages. */
    private final MessageController messenger;
    /** Last file which was picked. Used as initial file in file chooser. */
    private File currentFile;

    /** Menu item for save. */
    private final MenuItem saveMap;
    /** Menu item for save as. */
    private final MenuItem saveMapAs;

    /**
     * Constructs a new menu bar for the game.
     * @param mainStage Parent stage.
     * @param model Game model.
     * @param controller Game controller to interact with.
     * @param messenger Message controller
     */
    public GameMenuBar(Stage mainStage, ReadOnlyBlockWorldModel model,
                       BlockWorldController controller,
                       MessageController messenger) {
        this.mainStage = mainStage;
        this.controller = controller;
        this.messenger = messenger;

        model.addListener(WorldMapLoadedEvent.class, this::enableSaveButtons);

        Menu fileMenu = new Menu("File");

        MenuItem openMap = new MenuItem("Open…");
        openMap.setOnAction(this::openMapAction);
        openMap.setAccelerator(
                new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        saveMap = new MenuItem("Save");
        saveMap.setDisable(true);
        saveMap.setOnAction(this::saveMapAction);
        saveMap.setAccelerator(
                new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));

        saveMapAs = new MenuItem("Save As…");
        saveMapAs.setDisable(true);
        saveMapAs.setOnAction(this::saveMapAsAction);

        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(this::exitAction);

        fileMenu.getItems().addAll(openMap, saveMap, saveMapAs,
                new SeparatorMenuItem(), exit);
        this.getMenus().add(fileMenu);
    }

    /**
     * Enables the save and save as buttons.
     * @param event Event.
     */
    private void enableSaveButtons(BaseBlockWorldEvent event) {
        saveMap.setDisable(false);
        saveMapAs.setDisable(false);
    }

    /**
     * Creates and returns a new file chooser starting at the previous file,
     * or the current working directory if no previous file has been chosen.
     * @param title Title of the file dialog.
     * @return File dialog.
     */
    private FileChooser newFileChooser(String title) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(title);
        if (currentFile != null) {
            chooser.setInitialDirectory(currentFile.getParentFile());
            chooser.setInitialFileName(currentFile.getName());
        } else {
            // Start in the current working directory.
            chooser.setInitialDirectory(Paths.get("").toAbsolutePath().toFile());
        }
        return chooser;
    }

    /**
     * Handles a click on the open button. Displays a file chooser dialog
     * then attempts to load that file as a world map.
     * @param event Event.
     */
    private void openMapAction(ActionEvent event) {
        File newFile = newFileChooser("Open").showOpenDialog(mainStage);
        if (newFile == null) {
            return; // Dialog closed without choosing a file.
        }
        // Don't overwrite current file unless it was selected.
        currentFile = newFile;
        try {
            controller.loadWorldMapFile(currentFile.getAbsolutePath());
            messenger.handleInfoMessage("World map loaded!");
        } catch (WorldMapInconsistentException e) {
            messenger.handleErrorMessage(
                    "Error loading map: World map inconsistent.");
        } catch (WorldMapFormatException e) {
            String details = e.getMessage();
            if (details == null) {
                details = "Invalid format.";
            }
            messenger.handleErrorMessage("Error loading map: " + details);
        } catch (FileNotFoundException e) {
            messenger.handleErrorMessage("Error loading map: File not found.");
        }
    }

    /**
     * Handler for the save button. Saves the currently open map to the file
     * it was loaded from.
     * @param event Event.
     */
    private void saveMapAction(ActionEvent event) {
        saveCurrentMap();
    }

    /**
     * Handler for the save as button. Saves the currently open map to a file
     * of the user's choice.
     * @param event Event.
     */
    private void saveMapAsAction(ActionEvent event) {
        File newFile = newFileChooser("Save As").showSaveDialog(mainStage);
        if (newFile != null) {
            currentFile = newFile;
            saveCurrentMap();
        }
    }

    /**
     * Saves the current map to the currently selected file, displaying
     * an appropriate message on success or failure.
     */
    private void saveCurrentMap() {
        try {
            controller.saveWorldMapFile(currentFile.getAbsolutePath());
            messenger.handleInfoMessage("World map saved!");
        } catch (IOException e) {
            messenger.handleErrorMessage("Error saving map: "+e);
        }
    }

    /**
     * Handler for the exit button. Closes the application's main stage.
     * @param event Event.
     */
    private void exitAction(ActionEvent event) {
        Alert exitAlert = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to exit? Unsaved changes will be lost.",
                ButtonType.OK, ButtonType.CANCEL);
        Optional<ButtonType> result = exitAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            mainStage.close();
        }
    }
}
