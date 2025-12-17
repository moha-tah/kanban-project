package client.ihmMain.controllers;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Scene;

/**
 * Contrôleur de la zone centrale de la page d'accueil.
 * 
 * Ce contrôleur gère l'affichage central de la page d'accueil, incluant
 * les conteneurs de kanbans (créés, participés, disponibles), la barre
 * de recherche, le bouton de création, et le panneau de notifications.
 * 
 * @author Équipe Kanban
 * @version 1.0
 * @since 1.0
 * @see HomeViewController
 */
public class HomeKanbanCentralController {

    /**
     * Logger pour les messages de log de cette classe.
     */
    private static final Logger LOGGER = Logger.getLogger(HomeViewController.class.getName());

    /**
     * Zone de défilement pour afficher un kanban en détail.
     */
    @FXML private ScrollPane kanbanArea;
    
    /**
     * Champ de recherche pour filtrer les kanbans.
     */
    @FXML private TextField searchField;
    
    /**
     * Bouton pour créer un nouveau kanban.
     */
    @FXML private Button createKanbanButton;

    /**
     * Conteneur horizontal pour les kanbans créés par l'utilisateur.
     */
    @FXML private HBox createdKanbansContainer;
    
    /**
     * Conteneur horizontal pour les kanbans auxquels l'utilisateur participe.
     */
    @FXML private HBox participateKanbansContainer;
    
    /**
     * Conteneur horizontal pour les kanbans disponibles (publics).
     */
    @FXML private HBox availableKanbansContainer;

    /**
     * Panneau de notifications.
     */
    @FXML private Pane notifPanel;
    
    /**
     * Conteneur vertical pour les notifications.
     */
    @FXML private VBox notifContainer;

    /**
     * Indique si le panneau de notifications est visible.
     */
    private boolean notifVisible = false;

    /**
     * Initialise le contrôleur après le chargement du FXML.
     * 
     * Cette méthode masque le panneau de notifications et le rend
     * non interactif au démarrage.
     */
    @FXML
    private void initialize() {
        // Initialisation simple
        notifPanel.setVisible(false);
        notifPanel.setMouseTransparent(true);
        LOGGER.info("HomeViewController initialized.");
    }

    /**
     * Gère le clic sur le bouton de création de kanban.
     * 
     * Cette méthode ouvre une nouvelle fenêtre avec le formulaire
     * de création de kanban.
     */
    @FXML
    private void handleCreateKanban() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/createKanban.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Créer un Kanban");
            stage.setScene(new Scene(root, 900, 600));
            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Impossible de charger createKanban.fxml", e);
        }
    }

    /**
     * Bascule la visibilité du panneau de notifications.
     * 
     * Cette méthode affiche ou masque le panneau de notifications
     * et ajuste son interactivité.
     */
    public void toggleNotif() {
        notifVisible = !notifVisible;
        notifPanel.setVisible(notifVisible);
        notifPanel.setMouseTransparent(!notifVisible);
        if (notifVisible) notifPanel.toFront();
    }

    /**
     * Ajoute une notification au conteneur de notifications.
     * 
     * Cette méthode crée une nouvelle notification avec le message fourni
     * et l'ajoute au conteneur. Si le panneau n'est pas visible, il est affiché.
     * 
     * @param message Le message de la notification (ne doit pas être null)
     */
    public void addNotification(String message) {
        VBox notifBox = new VBox();
        notifBox.getChildren().add(new javafx.scene.control.Label(message));
        notifContainer.getChildren().add(notifBox);
        if (!notifVisible) toggleNotif();
    }

    /**
     * Affiche un kanban dans la zone de défilement.
     * 
     * @param kanbanNode Le nœud représentant le kanban à afficher (ne doit pas être null)
     */
    public void displayKanban(Node kanbanNode) {
        kanbanArea.setContent(kanbanNode);
    }

    /**
     * Affiche une liste de kanbans dans la zone de défilement.
     * 
     * @param listNode Le nœud représentant la liste de kanbans à afficher (ne doit pas être null)
     */
    public void showKanbanList(Node listNode) {
        kanbanArea.setContent(listNode);
    }

    /**
     * Récupère le conteneur des kanbans créés.
     * 
     * @return Le conteneur horizontal pour les kanbans créés
     */
    public HBox getCreatedKanbansContainer() {
        return createdKanbansContainer;
    }

    /**
     * Récupère le conteneur des kanbans auxquels l'utilisateur participe.
     * 
     * @return Le conteneur horizontal pour les kanbans participés
     */
    public HBox getParticipateKanbansContainer() {
        return participateKanbansContainer;
    }

    /**
     * Récupère le conteneur des kanbans disponibles.
     * 
     * @return Le conteneur horizontal pour les kanbans disponibles
     */
    public HBox getAvailableKanbansContainer() {
        return availableKanbansContainer;
    }
}
