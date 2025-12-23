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
    private boolean isPending = false;
    private static final String BTN_PRIMARY = "-fx-background-color: #4A90E2; -fx-text-fill: white; -fx-background-radius: 6;";
    private static final String BTN_WARNING = "-fx-background-color: #FF7043; -fx-text-fill: white; -fx-background-radius: 6;";
    private static final String BTN_PENDING = "-fx-background-color: #F5C16C; -fx-text-fill: #333; -fx-background-radius: 6;";
    private static final String TAG_VISIBILITY =
    "-fx-background-color: #ECECEC; -fx-text-fill: #333; -fx-padding: 3 8; -fx-background-radius: 8;";




    public void setMainCore(MainCore core) {
    this.core = core;
}


    // Ajoutez le paramètre boolean isMyKanban
    public void setKanbanData(Kanban kanban, String color, boolean isMyKanban, boolean isParticipating)
 {
        this.kanban = kanban;
        this.color = color;
            // Reset styles (important)
    requestButton.setStyle(BTN_PRIMARY);
    viewButton.setStyle(BTN_PRIMARY);
    deleteButton.setStyle(BTN_WARNING);


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
        visibilityLabel.setStyle(TAG_VISIBILITY);


        cardRoot.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 10;");

                // LOGIQUE DES BOUTONS
        boolean isPrivate = visibility.equalsIgnoreCase("private");

        if (isMyKanban) {
            // Le propriétaire : toujours accès total
            requestButton.setVisible(false);
            requestButton.setStyle(BTN_PRIMARY);

            viewButton.setVisible(true);
            viewButton.setStyle(BTN_PRIMARY);
            deleteButton.setVisible(true);
            deleteButton.setStyle(BTN_WARNING);

        }
        else if (isParticipating) {
            //  FIX : un utilisateur accepté doit pouvoir voir même si privé
            requestButton.setVisible(false);
            requestButton.setStyle(BTN_PRIMARY);

            viewButton.setVisible(true);
            viewButton.setStyle(BTN_PRIMARY);
            deleteButton.setVisible(false);
            deleteButton.setStyle(BTN_WARNING);

        }
        else {
            // Utilisateur externe
            deleteButton.setVisible(false);

            if (isPrivate) {
                requestButton.setVisible(true);
                viewButton.setVisible(false);
            } else {
                requestButton.setVisible(false);
                viewButton.setVisible(true);
            }
        }

    }



    @FXML
    private void handleView() {
        LOGGER.log(Level.INFO, "[VIEW] Kanban: {0}", title);

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
        LOGGER.log(Level.INFO, "[REQUEST ACCESS] Kanban priv\u00e9: {0}", title);

        if (core == null) {
            LOGGER.severe("MainCore is null in KanbanCardController!");
            return;
        }
        if (kanban == null) {
            LOGGER.severe("Kanban is null in KanbanCardController!");
            return;
        }

        core.requestAccessToKanban(kanban);
        setPendingState();

    }

    private void setPendingState() {
        isPending = true;
        requestButton.setText("Pending...");
        requestButton.setDisable(true);
        requestButton.setStyle("-fx-background-color: #FFD580; -fx-text-fill: #333;"); // orange clair
    }

    public void updatePermissionStatus(boolean accepted) {
        isPending = false;

        if (accepted) {
            // accès accordé
            requestButton.setVisible(false);
            viewButton.setVisible(true);
            deleteButton.setVisible(false);
        } else {
            // refusé → on remet à l’état initial
            requestButton.setVisible(true);
            requestButton.setDisable(false);
            requestButton.setText("Request Access");
            requestButton.setStyle(""); 
            viewButton.setVisible(false);
        }
    }   


  

    @FXML
    private void handleDelete() {
        LOGGER.log(Level.WARNING, "[DELETE] Kanban: {0}", title);
    }
    

}