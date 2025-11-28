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


    // Ajoutez le paramètre boolean isMyKanban
    public void setKanbanData(Kanban kanban, String color, boolean isMyKanban) {
        this.kanban = kanban;
        this.color = color;

        this.title = kanban.getTitle();
        // Gestion safe si columns ou creator sont null
        this.columns = (kanban.getTaskColumn() != null) ? kanban.getTaskColumn().size() : 0;
        this.visibility = (kanban.getVisibility() != null) ? kanban.getVisibility() : "Private";

        User c = kanban.getCreator();
        String cName = (c != null) ? c.getFirstName() + " " + c.getLastName() : "Unknown";

        titleLabel.setText(title);
        creatorLabel.setText("Creator: " + cName);
        columnsLabel.setText("Columns: " + columns);
        visibilityLabel.setText("Visibility: " + visibility);

        cardRoot.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 10;");

        // LOGIQUE DES BOUTONS
        boolean isPrivate = visibility.equalsIgnoreCase("private");

        if (isMyKanban) {
            // CAS 1 : C'est à moi -> J'ai toujours accès, même si c'est privé
            requestButton.setVisible(false);
            viewButton.setVisible(true);
            deleteButton.setVisible(true);
        } else {
            // CAS 2 : C'est pas à moi
            deleteButton.setVisible(false); // Je ne peux pas supprimer celui des autres

            if (isPrivate) {
                // Privé -> Demander accès
                requestButton.setVisible(true);
                viewButton.setVisible(false);
            } else {
                // Public -> Voir
                requestButton.setVisible(false);
                viewButton.setVisible(true);
            }
        }
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
        

        //core.getKanbanPort().openCreateForm(kanban);  //A changer  
        HomeViewController.getInstance().displayKanban(this.kanban); //cela la mis à la place 
    }


    @FXML
    private void requestPermission() {
        LOGGER.info("[REQUEST ACCESS] Kanban privé: " + title);

        if (core == null) {
            LOGGER.severe("MainCore is null in KanbanCardController!");
            return;
        }
        if (kanban == null) {
            LOGGER.severe("Kanban is null in KanbanCardController!");
            return;
        }

        core.requestAccessToKanban(kanban);
    }
  

    @FXML
    private void handleDelete() {
        LOGGER.warning("[DELETE] Kanban: " + title);
    }
    

}
