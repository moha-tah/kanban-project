package client.ihmMain.controllers;

import java.util.logging.Level;
import java.util.logging.Logger;

import client.ihmMain.MainCore;
import common.dataClasses.Kanban;
import common.dataClasses.User;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class KanbanCardController {

    private MainCore core;
    private Kanban kanban;          //LE VRAI OBJET KANBAN BACKEND

    private static final Logger LOGGER = Logger.getLogger(KanbanCardController.class.getName());

    @FXML private AnchorPane cardRoot;
    @FXML private Label titleLabel;
    @FXML private Label creatorLabel;
    @FXML private Label columnsLabel;
    @FXML private Label visibilityLabel;
    @FXML private Button viewButton;
    @FXML private Button requestButton;
    @FXML private Button deleteButton;

    private String title;
    private User creator;
    private int columns;
    private String visibility;
    private String color;

    public void setMainCore(MainCore core) {
    this.core = core;
}


    public void setKanbanData(Kanban kanban, String color) {
    this.kanban = kanban;
    this.color = color;

    this.title = kanban.getTitle();
    this.columns = kanban.getTaskColumn().size();
    this.visibility = kanban.getVisibility();
    this.creator = kanban.getCreator();

    titleLabel.setText(title);
    creatorLabel.setText("Creator: " + creator.getFirstName() + " " + creator.getLastName());
    columnsLabel.setText("Columns: " + columns);
    visibilityLabel.setText("Visibility: " + visibility);

    cardRoot.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 10;");

    boolean isPrivate = visibility.equalsIgnoreCase("private");
    if (isMyKanban()) {
        requestButton.setVisible(false);
        requestButton.setManaged(false);
        viewButton.setVisible(true);
    } else {
        requestButton.setVisible(isPrivate);
        requestButton.setManaged(isPrivate);
        viewButton.setVisible(!isPrivate);
        viewButton.setManaged(!isPrivate);
    }
}

    private boolean isMyKanban() {
        if (core == null || core.getMe() == null || kanban == null) {
            LOGGER.log(Level.WARNING, "Cannot determine if Kanban is mine: core or current user or kanban is null");
            return false;
        }
        return kanban.getCreator().getUsername().equals(core.getMe().getUsername());
    }

    @FXML
    private void handleView() {
        LOGGER.info("[VIEW] Kanban: " + title);

        if (core == null) {
            LOGGER.severe(" MainCore is null in KanbanCardController!");
            return;
        }

        if (core.getKanbanPort() == null) {
            LOGGER.severe(" MainCallsKanban interface is null!");
            return;
        }

        core.getKanbanPort().openCreateForm(kanban);   
    }


    @FXML
    private void requestPermission() {
        LOGGER.info("[REQUEST ACCESS] Kanban privé: " + title);
    }

    @FXML
    private void handleDelete() {
        LOGGER.warning("[DELETE] Kanban: " + title);
    }
    

}
