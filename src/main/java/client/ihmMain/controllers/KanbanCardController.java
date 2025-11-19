package client.ihmMain.controllers;

import java.util.UUID;

import client.ihmMain.MainCore;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class KanbanCardController {

    @FXML private AnchorPane cardRoot;
    @FXML private Label titleLabel;
    @FXML private Label creatorLabel;
    @FXML private Label columnsLabel;
    @FXML private Label statusLabel;
    @FXML private Button viewButton;
    @FXML private Button deleteButton;

    // Données internes du Kanban (transmises par le controller parent)
    private String title;
    private String creator;
    private int columns;
    private String status;
    private String color;
    private MainCore mainCore;
    private UUID kanbanId;

    public void setMainCore(MainCore core) { this.mainCore = core; }
    public void setKanbanId(UUID id) { this.kanbanId = id; }


    @FXML
    private void initialize() {
        // rien de spécial ici pour le moment
    }

    /** Initialise la carte avec les données d’un Kanban */
    public void setKanbanData(String title, String creator, int columns, String status, String color) {
        this.title = title;
        this.creator = creator;
        this.columns = columns;
        this.status = status;
        this.color = color;

        titleLabel.setText(title);
        creatorLabel.setText("Creator: " + creator);
        columnsLabel.setText("Columns: " + columns);
        statusLabel.setText("Status: " + status);

        cardRoot.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 10; -fx-padding: 10;");
    }

    @FXML
    private void handleView() {
        if (mainCore == null) return;
        if (kanbanId == null) return;

        System.out.println("[VIEW] Request Kanban: " + kanbanId);

        mainCore.viewKanban(kanbanId);  
    }


    // === 🗑 Bouton DELETE ===
    @FXML
    private void handleDelete() {
        System.out.println("🗑 [DELETE] Kanban: " + title);
        // TODO: futur backend → envoyer une requête de suppression au serveur
        // Exemple futur :
        // backendService.deleteKanban(this.kanbanId);
    }

    // Optionnel pour futur backend
    public String getTitle() { return title; }
    public String getCreator() { return creator; }
    public int getColumns() { return columns; }
    public String getStatus() { return status; }
}
