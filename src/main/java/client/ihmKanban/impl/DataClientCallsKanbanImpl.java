package client.ihmKanban.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import client.ihmKanban.kanbanCorps;
import client.ihmMain.controllers.HomeViewController;
import client.interfaces.DataClientCallsKanban;
import common.dataClasses.Kanban;

/**
 * Implémentation des callbacks de la couche Data vers la couche Kanban (IHM Kanban).
 * 
 * Cette classe sert d'adaptateur entre la couche de données et l'interface
 * utilisateur Kanban. Elle reçoit les notifications de la couche Data et
 * les transmet au cœur de l'application Kanban pour mise à jour de l'affichage.
 * 
 * @author Équipe Kanban
 * @version 1.0
 * @since 1.0
 * @see DataClientCallsKanban
 * @see kanbanCorps
 */
public class DataClientCallsKanbanImpl implements DataClientCallsKanban {
    
    /**
     * Logger pour les messages de log de cette classe.
     */
    private static final Logger LOGGER = Logger.getLogger(DataClientCallsKanbanImpl.class.getName());
    
    /**
     * Cœur de l'application Kanban.
     */
    private final kanbanCorps corps;  

    /**
     * Constructeur de l'implémentation.
     * 
     * @param corps Le cœur de l'application Kanban (ne doit pas être null)
     */
    public DataClientCallsKanbanImpl(kanbanCorps corps) {
        this.corps = corps;
    }

    /**
     * Affiche un kanban dans l'interface utilisateur.
     * 
     * Cette méthode est appelée par la couche Data lorsqu'un kanban doit
     * être affiché. Elle délègue l'affichage au cœur de l'application Kanban.
     * 
     * @param kanban Le kanban à afficher (ne doit pas être null)
     * @param homeController Le contrôleur de la vue principale (ne doit pas être null)
     */
    @Override
    public void displayKanban(Kanban kanban, HomeViewController homeController ) {
        corps.displayKanban(kanban, homeController); 
        LOGGER.log(Level.INFO, "[Data->kanban] Display kanban : kanban={0}", kanban);

    }

    /**
     * Met à jour l'affichage d'un kanban existant.
     * 
     * Cette méthode est appelée par la couche Data lorsqu'un kanban a été
     * modifié et doit être rafraîchi dans l'interface utilisateur.
     * 
     * @param kanban Le kanban mis à jour (ne doit pas être null)
     */
    @Override
    public void updateKanban(Kanban kanban) {
        corps.updateKanban(kanban);

        kanbanCorps.LOGGER.info("[Data->kanban] Display kanban : ");

    }

}


