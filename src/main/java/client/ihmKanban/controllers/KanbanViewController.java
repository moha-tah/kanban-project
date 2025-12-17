package client.ihmKanban.controllers;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import client.MainApp;
import client.ihmKanban.kanbanCorps;
import client.ihmMain.MainCore;
import client.ihmMain.controllers.UsersController;
import common.dataClasses.Column;
import common.dataClasses.CreateTask;
import common.dataClasses.LightKanban;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

/**
 * Contrôleur principal de la vue Kanban.
 * 
 * Ce contrôleur gère la vue principale du kanban qui inclut deux sous-vues :
 * - La liste des utilisateurs (via UsersController)
 * - L'affichage du kanban (via DisplayKanbanController)
 * 
 * @author Équipe Kanban
 * @version 1.0
 * @since 1.0
 * @see Initializable
 * @see DisplayKanbanController
 * @see UsersController
 */
public class KanbanViewController implements Initializable {

    /**
     * Logger pour les messages de log de cette classe.
     */
    private static final Logger LOGGER = Logger.getLogger(KanbanViewController.class.getName());

    /**
     * Contrôleur de la vue des utilisateurs injecté via fx:include.
     * 
     * Ce contrôleur est injecté automatiquement par JavaFX grâce à
     * l'élément fx:include avec fx:id="usersInclude" dans le FXML.
     */
    @FXML
    private UsersController usersIncludeController;

    /**
     * Contrôleur de l'affichage du kanban injecté via fx:include.
     * 
     * Ce contrôleur est injecté automatiquement par JavaFX grâce à
     * l'élément fx:include avec fx:id="displayKanbanInclude" dans le FXML.
     */
    @FXML
    private DisplayKanbanController displayKanbanIncludeController;

    /**
     * Cœur de l'application principale.
     */
    private MainCore core;
    
    /**
     * Cœur de l'application Kanban.
     */
    private kanbanCorps corps;

    /**
     * Initialise le contrôleur après le chargement du FXML.
     * 
     * Cette méthode est appelée automatiquement par JavaFX après le chargement
     * du fichier FXML. Elle initialise le contrôleur des utilisateurs.
     * 
     * @param url L'URL du fichier FXML (non utilisé)
     * @param resourceBundle Le ResourceBundle (non utilisé)
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        core = MainApp.getCore();

        if (core != null && usersIncludeController != null) {
            usersIncludeController.setCore(core);
            usersIncludeController.refreshUsers();
        }
    }

    /**
     * Initialise le tableau kanban avec les données fournies.
     * 
     * Cette méthode transmet les données du kanban au contrôleur d'affichage
     * pour qu'il puisse rendre le kanban avec ses colonnes et tâches.
     * 
     * @param kanban La version légère du kanban à afficher (ne doit pas être null)
     * @param columns La liste des colonnes du kanban (ne doit pas être null)
     * @param taskCreations La liste des tâches à créer (ne doit pas être null)
     * @param manageDisplay Le gestionnaire d'affichage pour les opérations de rafraîchissement
     */
    public void initBoard(LightKanban kanban,
                          List<Column> columns,
                          List<CreateTask> taskCreations, ManageDisplay manageDisplay) {

        if (displayKanbanIncludeController != null) {
            DisplayKanbanController.setCore(corps);
            displayKanbanIncludeController.initBoard(kanban, columns, taskCreations,manageDisplay );
            
            
        }
    }

    /**
     * Définit le cœur de l'application Kanban.
     * 
     * @param Kcorps Le cœur de l'application Kanban (ne doit pas être null)
     */
    public void setCore(kanbanCorps Kcorps) {
        this.corps = Kcorps;
        LOGGER.info("KanbanViewControlleur: le Corps a été rajouté .");
    }
}