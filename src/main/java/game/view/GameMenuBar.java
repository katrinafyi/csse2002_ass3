package game.view;

import csse2002.block.world.WorldMapFormatException;
import csse2002.block.world.WorldMapInconsistentException;
import game.controller.BlockWorldController;
import game.controller.MessageController;
import game.model.BlockWorldModel;
import game.model.events.BaseBlockWorldEvent;
import game.model.events.WorldMapLoadedEvent;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class GameMenuBar extends MenuBar {


    private final Stage mainStage;
    private final BlockWorldController controller;
    private final MessageController messageController;
    private File currentFile;

    private MenuItem saveMap;
    private MenuItem saveMapAs;

    public GameMenuBar(Stage mainStage, BlockWorldModel model,
                       BlockWorldController controller,
                       MessageController messageController) {
        this.mainStage = mainStage;
        this.controller = controller;
        this.messageController = messageController;

        model.addListener(WorldMapLoadedEvent.class, this::enableSaveButtons);

        Menu fileMenu = new Menu("File");

        MenuItem openMap = new MenuItem("Open…");
        openMap.setOnAction(this::openMapAction);
        saveMap = new MenuItem("Save");
        saveMap.setDisable(true);
        saveMap.setOnAction(this::saveMapAction);
        saveMapAs = new MenuItem("Save As…");
        saveMapAs.setDisable(true);
        saveMapAs.setOnAction(this::saveMapAsAction);

        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(this::exitAction);

        fileMenu.getItems().addAll(
                openMap, saveMap, saveMapAs, new SeparatorMenuItem(), exit
        );
        this.getMenus().add(fileMenu);
    }

    private void enableSaveButtons(BaseBlockWorldEvent event) {
        saveMap.setDisable(false);
        saveMapAs.setDisable(false);
    }

    private FileChooser newFileChooser(String title) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(title);
        if (currentFile != null) {
            chooser.setInitialDirectory(currentFile.getParentFile());
            chooser.setInitialFileName(currentFile.getName());
        }
        return chooser;
    }

    private void openMapAction(ActionEvent event) {
        currentFile = newFileChooser("Open").showOpenDialog(mainStage);
        try {
            controller.loadWorldMapFile(currentFile.getAbsolutePath());
            messageController.handleMessage("World map loaded!");
        } catch (WorldMapInconsistentException e) {
            showErrorAlert("Error loading map: World map inconsistent.");
        } catch (WorldMapFormatException e) {
            showErrorAlert("Error loading map: Invalid world map format.");
        } catch (FileNotFoundException e) {
            showErrorAlert("Error loading map: File not found.");
        }
    }

    private void saveMapAction(ActionEvent event) {
        saveCurrentMap();
    }

    private void saveMapAsAction(ActionEvent event) {
        File newFile = newFileChooser("Save As").showSaveDialog(mainStage);
        if (newFile != null) {
            currentFile = newFile;
            saveCurrentMap();
        }
    }

    private void saveCurrentMap() {
        try {
            controller.saveWorldMapFile(currentFile.getAbsolutePath());
            messageController.handleMessage("World map saved!");
        } catch (Exception e) {
            showErrorAlert("Error saving map: "+e);
        }
    }

    private void exitAction(ActionEvent event) {
        mainStage.close();
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.showAndWait();
    }

}