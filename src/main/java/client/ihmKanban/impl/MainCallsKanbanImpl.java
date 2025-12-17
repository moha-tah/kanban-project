package client.ihmKanban.impl;
import client.ihmKanban.kanbanCorps;
import client.ihmMain.controllers.HomeViewController;
import client.ihmMain.controllers.ProfileController;
import client.interfaces.MainCallsKanban;
import common.dataClasses.Kanban;

/**
 * Implémentation des callbacks de la couche Main vers la couche Kanban (IHM Kanban).
 * 
 * Cette classe sert d'adaptateur entre la couche principale (IHM Main) et l'interface
 * utilisateur Kanban. Elle permet à la couche Main d'ouvrir des vues de kanban,
 * d'afficher des listes de snapshots, et de créer de nouveaux kanbans.
 * 
 * @author Équipe Kanban
 * @version 1.0
 * @since 1.0
 * @see MainCallsKanban
 * @see kanbanCorps
 */
public class MainCallsKanbanImpl implements MainCallsKanban {
    
    /**
     * Cœur de l'application Kanban.
     */
    private final kanbanCorps corps;  

    /**
     * Constructeur de l'implémentation.
     * 
     * @param corps Le cœur de l'application Kanban (ne doit pas être null)
     */
    public MainCallsKanbanImpl(kanbanCorps corps) {
        this.corps = corps;
    }

    /**
     * Affiche la liste des snapshots de kanban.
     * 
     * Cette méthode est appelée par la couche Main pour afficher l'historique
     * des versions sauvegardées d'un kanban. L'implémentation est à compléter.
     * 
     * @implNote Cette méthode n'est pas encore implémentée.
     */
    @Override
    public void displaySnapshotList(){
        // A IMPLEMENTER 
        kanbanCorps.LOGGER.info("[MainCallsKanban] displaySnapshotList called");

    }

    /**
     * Ouvre le formulaire de création d'un kanban.
     * 
     * Cette méthode est appelée par la couche Main lorsqu'un nouveau kanban
     * doit être créé. Elle affiche le kanban dans l'interface utilisateur.
     * 
     * @param kanban Le kanban à créer et afficher (ne doit pas être null)
     * @param homeController Le contrôleur de la vue principale (ne doit pas être null)
     */
    @Override
    public void openCreateForm(Kanban kanban, HomeViewController homeController) { 
        corps.displayKanban(kanban, homeController); 
        kanbanCorps.LOGGER.info("[MainCallsKanban] openCreateForm called");

    }
    
    /**
     * Ouvre la vue d'un kanban depuis le profil utilisateur.
     * 
     * Cette méthode est appelée par la couche Main lorsqu'un kanban doit
     * être affiché depuis la vue de profil utilisateur.
     * 
     * @param kanban Le kanban à afficher (ne doit pas être null)
     * @param profileController Le contrôleur de la vue de profil (ne doit pas être null)
     */
    @Override
    public void openKanbanViewFromProfile(Kanban kanban, ProfileController profileController){
        corps.displayKanbanFromProfile(kanban, profileController);
        kanbanCorps.LOGGER.info("[MainCallsKanban] openKanbanViewFromProfile called");
    }

}
