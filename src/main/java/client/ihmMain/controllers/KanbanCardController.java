package client.ihmMain.controllers;

import java.util.UUID;

import client.ihmMain.MainCore;
import client.interfaces.IhmMainCallsComm;
import common.dataClasses.Kanban;
import common.dataClasses.User;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class KanbanCardController {

    private MainCore core;

    private Kanban kanban;

    @FXML private AnchorPane cardRoot;
    @FXML private Label titleLabel;
    @FXML private Label creatorLabel;
    @FXML private Label columnsLabel;
    @FXML private Label visibilityLabel;
    @FXML private Button viewButton;
    @FXML private Button requestButton;
    @FXML private Button deleteButton;

    // private String title = kanban.getTitle();
    private String title ;
    // private User creator = kanban.getCreator();
    private User creator ;
    // private int columns = kanban.getAllColumns().size();
    private int columns ;
    // private String visibility = kanban.getVisibility();
    private String visibility ;
    private String color ; 
    

    public void setKanbanData(String title, User creator, int columns, String visibility, String color, boolean isAvailableSection) {
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

        // --- 🔥 Logique des boutons ---
        if (visibility.equalsIgnoreCase("private") && isAvailableSection) {
            requestButton.setVisible(true);
            viewButton.setVisible(false);
        } else {
            requestButton.setVisible(false);
            viewButton.setVisible(true);
        }
    }


    @FXML
    private void handleView() {
        System.out.println("[VIEW] Kanban: " + title);
    }

    // Demander autorisation
    @FXML
    private void requestPermission(/*UUID lightKanbanID*/) {
        System.out.println("[REQUEST ACCESS] Kanban privé: " + title);

        // IhmMainCallsComm comm = core.getCommPort();
        // comm.sendPermissionRequest(core.getMe().getId(), lightKanbanID);

        // === 🔄 Mettre le bouton en mode "Pending" ===
        requestButton.setText("Pending");
        requestButton.getStyleClass().add("btn-pending");
        requestButton.setDisable(true); // empêche de redemander
    }


    // Supprimer
    @FXML
    private void handleDelete() {
        System.out.println("[DELETE] Kanban: " + title);
    }
}
