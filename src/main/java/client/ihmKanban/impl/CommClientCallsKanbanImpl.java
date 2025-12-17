package client.ihmKanban.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import client.ihmKanban.kanbanCorps;
import client.ihmMain.controllers.HomeViewController;
import client.interfaces.CommClientCallsKanban;
import common.dataClasses.Kanban;
import common.dataClasses.LightKanban;
import common.dataClasses.Modification;
import javafx.application.Platform;

/**
 * Implémentation des callbacks de la couche Communication vers la couche Kanban (IHM Kanban).
 * 
 * Cette classe sert d'adaptateur entre la couche de communication et l'interface
 * utilisateur Kanban. Elle reçoit les messages du serveur (notifications de modifications,
 * demandes d'affichage) et les transmet au cœur de l'application Kanban.
 * 
 * Les opérations d'affichage sont exécutées sur le thread JavaFX pour garantir
 * la sécurité des opérations sur l'interface utilisateur.
 * 
 * @author Équipe Kanban
 * @version 1.0
 * @since 1.0
 * @see CommClientCallsKanban
 * @see kanbanCorps
 * @see Platform#runLater(Runnable)
 */
public class CommClientCallsKanbanImpl implements CommClientCallsKanban {
    
    /**
     * Logger pour les messages de log de cette classe.
     */
    private static final Logger LOGGER = Logger.getLogger(CommClientCallsKanbanImpl.class.getName());
    
    /**
     * Cœur de l'application Kanban.
     * 
     * Similaire à MainCore pour la couche main, ce champ contient la référence
     * au cœur de l'application Kanban qui gère la logique métier.
     */
    private final kanbanCorps corps;

    /**
     * Constructeur de l'implémentation.
     * 
     * @param corps Le cœur de l'application Kanban (ne doit pas être null)
     */
    public CommClientCallsKanbanImpl(kanbanCorps corps) {
        this.corps = corps;
    }

    /**
     * Livre une notification de modification d'un kanban.
     * 
     * Cette méthode est appelée par la couche Communication lorsqu'une modification
     * a été effectuée sur un kanban (ajout/suppression de colonne ou tâche, etc.).
     * L'implémentation actuelle se contente de logger la notification.
     * 
     * @param idKanban L'identifiant léger du kanban modifié (ne doit pas être null)
     * @param modification La modification effectuée (ne doit pas être null)
     * @implNote Cette méthode devrait déclencher une mise à jour de l'affichage.
     */
    @Override
    public void deliverNotification(LightKanban idKanban, Modification modification) {
        LOGGER.log(Level.INFO, "[Comm->kanban] notification reçue : kanban={0} modification={1}", new Object[]{idKanban, modification});

    }
    
    /**
     * Affiche un kanban dans l'interface utilisateur.
     * 
     * Cette méthode est appelée par la couche Communication lorsqu'un kanban
     * doit être affiché. L'affichage est exécuté sur le thread JavaFX car
     * cette méthode peut être appelée depuis le thread de réception des messages
     * (MsgReceiver), qui n'est pas le thread JavaFX.
     * 
     * @param kanban Le kanban à afficher (ne doit pas être null)
     * @param homeController Le contrôleur de la vue principale (ne doit pas être null)
     * @see Platform#runLater(Runnable)
     */
    @Override
    public void displayKanban(Kanban kanban, HomeViewController homeController) {
        LOGGER.log(Level.INFO, "[Comm->Kanban] displayKanban called for: {0}", kanban.getTitle());
        // Exécuter sur le thread JavaFX car appelé depuis MsgReceiver-thread
        Platform.runLater(() -> {
            corps.displayKanban(kanban, homeController);
        });
    }
}