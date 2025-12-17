package client.ihmKanban.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import client.MainApp;
import client.ihmKanban.kanbanCorps;
import client.ihmMain.controllers.HomeViewController;
import common.dataClasses.Column;
import common.dataClasses.CreateTask;
import common.dataClasses.Kanban;
import common.dataClasses.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

/**
 * Gestionnaire d'affichage des kanbans.
 * 
 * Cette classe est responsable de la construction et de l'affichage des vues
 * de kanban. Elle charge les fichiers FXML, initialise les contrôleurs,
 * et gère le rafraîchissement de l'affichage.
 * 
 * @author Équipe Kanban
 * @version 1.0
 * @since 1.0
 * @see KanbanViewController
 * @see HomeViewController
 */
public class ManageDisplay {

    /**
     * Logger pour les messages de log de cette classe.
     */
    private static final Logger LOGGER = Logger.getLogger(ManageDisplay.class.getName());
    
    /**
     * Cœur de l'application Kanban.
     */
    private final kanbanCorps corps; 

    /**
     * Constructeur du gestionnaire d'affichage.
     * 
     * @param corps Le cœur de l'application Kanban (ne doit pas être null)
     */
    public ManageDisplay(kanbanCorps corps) {
        this.corps = corps;

    }

    /**
     * Référence au contrôleur de la vue principale qui appartient à IHM Main.
     */
    private HomeViewController homeViewController;

    /**
     * Définit le contrôleur de la vue principale.
     * 
     * @param homeViewController Le contrôleur de la vue principale (peut être null)
     */
    public void setHomeViewController(HomeViewController homeViewController) {
        this.homeViewController = homeViewController;
    }

    /**
     * Récupère le contrôleur de la vue principale.
     * 
     * @return Le contrôleur de la vue principale, ou null si non défini
     */
    public HomeViewController getHomeViewController() {
        return homeViewController;
    }

    /**
     * Rafraîchit l'affichage d'un kanban.
     * 
     * Cette méthode reconstruit et réaffiche la vue du kanban avec les données
     * mises à jour.
     * 
     * @param kanban Le kanban à rafraîchir (ne doit pas être null)
     */
    public void refreshKanban(Kanban kanban) {

        this.openKanbanScreen(kanban, homeViewController);
        LOGGER.info("[Kanban] Refresh kanban : ");
    }

    /**
     * Construit la vue d'un kanban depuis le fichier FXML.
     * 
     * Cette méthode charge le fichier FXML, initialise le contrôleur avec
     * les colonnes et tâches du kanban, et retourne le Parent JavaFX.
     * 
     * @param kanban Le kanban à afficher (ne doit pas être null)
     * @return Le Parent JavaFX représentant la vue du kanban
     * @throws IOException si le chargement du FXML échoue
     * @throws IllegalStateException si le fichier FXML est introuvable
     */
    private Parent buildKanbanView(Kanban kanban) throws IOException {
    URL fxmlUrl = MainApp.class.getResource("/kanbanView.fxml");
    LOGGER.info("DEBUG FXML kanbanView");

    if (fxmlUrl == null) {
        throw new IllegalStateException("kanbanView.fxml introuvable dans le classpath !");
    }

    FXMLLoader loader = new FXMLLoader(fxmlUrl);
    Parent kanbanView = loader.load();

    // Colonnes
    List<Column> cols = kanban.getAllColumns();

    // Tasks & CreateTask
    List<CreateTask> taskCreations = new ArrayList<>();
    for (Column col : cols) {
        for (Task t : kanban.getTasksFromColumn(col)) {
            taskCreations.add(new CreateTask(t, col.getId()));
        }
    }

    // Controller du kanban
    KanbanViewController controller = loader.getController();
    controller.setCore(corps);
    controller.initBoard(kanban, cols, taskCreations, this);

    return kanbanView;
}

    /**
     * Ouvre l'écran d'affichage d'un kanban dans la vue principale.
     * 
     * Cette méthode construit la vue du kanban et l'affiche dans la zone
     * dédiée du contrôleur de la vue principale.
     * 
     * @param kanban Le kanban à afficher (ne doit pas être null)
     * @param homeController Le contrôleur de la vue principale (ne doit pas être null)
     */
    public void openKanbanScreen(Kanban kanban, HomeViewController homeController) {
    try {
        setHomeViewController(homeController);
        Parent kanbanView = buildKanbanView(kanban);
        homeController.getKanbanArea().setContent(kanbanView);

    } catch (IOException | IllegalStateException e) {
        LOGGER.log(Level.INFO, 
            "Erreur lors de l'ouverture de l'écran Kanban : {0}", e.getMessage());
    }
}

    /**
     * Ouvre l'écran d'affichage d'un kanban depuis le profil utilisateur.
     * 
     * Cette méthode construit la vue du kanban mais ne l'affiche pas
     * automatiquement (code commenté). Elle est prévue pour une utilisation
     * future depuis la vue de profil.
     * 
     * @param kanban Le kanban à afficher (ne doit pas être null)
     * @param profileController Le contrôleur de la vue de profil (non utilisé actuellement)
     */
    public void openKanbanScreenFromProfile(
        Kanban kanban, 
        client.ihmMain.controllers.ProfileController profileController) {
    
    try {
        setHomeViewController(null);
        Parent kanbanView = buildKanbanView(kanban);
        //profileController.getKanbanArea().setContent(kanbanView);

    } catch (IOException | IllegalStateException e) {
        LOGGER.log(Level.INFO, 
            "Erreur lors de l'ouverture de l'écran Kanban : {0}", e.getMessage());
    }
}
}