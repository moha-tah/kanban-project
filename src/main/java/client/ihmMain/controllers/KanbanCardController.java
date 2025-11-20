package client.ihmMain.controllers;

import java.util.logging.Level;
import java.util.logging.Logger;

import client.ihmMain.MainCore;
import common.dataClasses.User;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class KanbanCardController {

    private MainCore core;
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

    public void setKanbanData(String title, User creator, int columns, String visibility, String color) {
        this.title = title;
        this.creator = creator;
        this.columns = columns;
        this.visibility = visibility;
        this.color = color;

        titleLabel.setText(title);
        creatorLabel.setText("Creator: " + creator.getFirstName() + " " + creator.getLastName());
        columnsLabel.setText("Columns: " + columns);
        visibilityLabel.setText("Visibility: " + visibility);

        cardRoot.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 10; -fx-padding: 10;");

        if (visibility.equalsIgnoreCase("private")) {
            viewButton.setVisible(false);
            requestButton.setVisible(true);
        } else {
            viewButton.setVisible(true);
            requestButton.setVisible(false);
        }

        LOGGER.info("🔧 Kanban data initialized: " + title);
    }

    @FXML
    private void handleView() {
        LOGGER.info("[VIEW] Kanban: " + title);
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
