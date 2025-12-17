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

/**
 * Contrôleur d'une carte de kanban.
 * 
 * Ce contrôleur gère l'affichage d'une carte représentant un kanban dans
 * la liste des kanbans. Il affiche les informations du kanban (titre,
 * créateur, nombre de colonnes, visibilité) et gère les actions possibles
 * selon les droits de l'utilisateur (voir, demander accès, supprimer).
 * 
 * @author Équipe Kanban
 * @version 1.0
 * @since 1.0
 * @see HomeViewController
 * @see MainCore
 */
public class KanbanCardController {

    /**
     * Cœur de l'application principale.
     */
    private MainCore core;
    
    /**
     * Le vrai objet Kanban backend (objet complet).
     */
    private Kanban kanban;

    /**
     * Logger pour les messages de log de cette classe.
     */
    private static final Logger LOGGER = Logger.getLogger(KanbanCardController.class.getName());

    /**
     * Panneau racine de la carte.
     */
    @FXML private AnchorPane cardRoot;
    
    /**
     * Label affichant le titre du kanban.
     */
    @FXML private Label titleLabel;
    
    /**
     * Label affichant le créateur du kanban.
     */
    @FXML private Label creatorLabel;
    
    /**
     * Label affichant le nombre de colonnes.
     */
    @FXML private Label columnsLabel;
    
    /**
     * Label affichant la visibilité du kanban.
     */
    @FXML private Label visibilityLabel;
    
    /**
     * Bouton pour voir le kanban.
     */
    @FXML private Button viewButton;
    
    /**
     * Bouton pour demander l'accès au kanban.
     */
    @FXML private Button requestButton;
    
    /**
     * Bouton pour supprimer le kanban.
     */
    @FXML private Button deleteButton;
    
    /**
     * Titre du kanban.
     */
    private String title;
    
    /**
     * Créateur du kanban.
     */
    private User creator;
    
    /**
     * Nombre de colonnes du kanban.
     */
    private int columns;
    
    /**
     * Visibilité du kanban (Private/Public).
     */
    private String visibility;
    
    /**
     * Couleur de fond de la carte.
     */
    private String color;
    
    /**
     * Indique si une demande d'accès est en attente.
     */
    private boolean isPending = false;
    
    /**
     * Style CSS pour les boutons principaux.
     */
    private static final String BTN_PRIMARY = "-fx-background-color: #4A90E2; -fx-text-fill: white; -fx-background-radius: 6;";
    
    /**
     * Style CSS pour les boutons d'avertissement.
     */
    private static final String BTN_WARNING = "-fx-background-color: #FF7043; -fx-text-fill: white; -fx-background-radius: 6;";
    
    /**
     * Style CSS pour les boutons en attente.
     */
    private static final String BTN_PENDING = "-fx-background-color: #F5C16C; -fx-text-fill: #333; -fx-background-radius: 6;";
    
    /**
     * Style CSS pour le tag de visibilité.
     */
    private static final String TAG_VISIBILITY =
    "-fx-background-color: #ECECEC; -fx-text-fill: #333; -fx-padding: 3 8; -fx-background-radius: 8;";

    /**
     * Définit le cœur de l'application principale.
     * 
     * @param core Le cœur de l'application (ne doit pas être null)
     */
    public void setMainCore(MainCore core) {
    this.core = core;
}


    /**
     * Définit les données du kanban à afficher dans la carte.
     * 
     * Cette méthode configure tous les labels et boutons de la carte
     * selon les droits de l'utilisateur (propriétaire, participant, ou externe).
     * 
     * @param kanban Le kanban à afficher (ne doit pas être null)
     * @param color La couleur de fond de la carte (ne doit pas être null)
     * @param isMyKanban true si l'utilisateur est le propriétaire du kanban
     * @param isParticipating true si l'utilisateur participe au kanban
     */
    public void setKanbanData(Kanban kanban, String color, boolean isMyKanban, boolean isParticipating)
 {
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



    /**
     * Gère le clic sur le bouton "Voir" pour afficher le kanban.
     * 
     * Cette méthode affiche le kanban dans la zone centrale de la page d'accueil.
     */
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


    /**
     * Gère la demande d'accès à un kanban privé.
     * 
     * Cette méthode envoie une demande de permission au créateur du kanban
     * et met à jour l'état du bouton pour indiquer que la demande est en attente.
     */
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

    /**
     * Met à jour l'état du bouton pour indiquer qu'une demande est en attente.
     */
    private void setPendingState() {
        isPending = true;
        requestButton.setText("Pending...");
        requestButton.setDisable(true);
        requestButton.setStyle("-fx-background-color: #FFD580; -fx-text-fill: #333;"); // orange clair
    }

    /**
     * Met à jour le statut de la permission après réponse du créateur.
     * 
     * Cette méthode est appelée lorsque le créateur du kanban répond à la demande
     * d'accès. Si accepté, le bouton de demande disparaît et le bouton "Voir" apparaît.
     * Si refusé, le bouton de demande revient à son état initial.
     * 
     * @param accepted true si l'accès a été accordé, false sinon
     */
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


  

    /**
     * Gère la suppression d'un kanban.
     * 
     * Cette méthode est appelée lorsque le propriétaire clique sur le bouton
     * de suppression. L'implémentation actuelle se contente de logger l'action.
     * 
     * @implNote Cette méthode devrait déclencher la suppression effective du kanban.
     */
    @FXML
    private void handleDelete() {
        LOGGER.log(Level.WARNING, "[DELETE] Kanban: {0}", title);
    }
    

}