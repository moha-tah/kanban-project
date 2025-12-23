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


/** Impl des callbacks de la couche Communication vers la couche Kanban (IHM Kanban). */
public class CommClientCallsKanbanImpl implements CommClientCallsKanban {
    
    private static final Logger LOGGER = Logger.getLogger(CommClientCallsKanbanImpl.class.getName());
    private final kanbanCorps corps;   // ← Comme MainCore pour la couche main

    public CommClientCallsKanbanImpl(kanbanCorps corps) {
        this.corps = corps;
    }

    @Override
    public void deliverNotification(LightKanban idKanban, Modification modification) {
        LOGGER.log(Level.INFO, "[Comm->kanban] notification reçue : kanban={0} modification={1}", new Object[]{idKanban, modification});
        
        // Vérifier si la notification concerne le kanban actuellement affiché
        Kanban currentKanban = corps.getCurrentKanban();
        if (currentKanban != null && currentKanban.getId().equals(idKanban.getId())) {
            LOGGER.log(Level.INFO, "[Comm->kanban] Modification du kanban affiché, application locale de la modification");
            try {
                // Appliquer la modification au kanban courant localement
                //Kanban updatedKanban = modification.execute(currentKanban);
                LOGGER.log(Level.INFO, "[Comm->kanban] Modification appliquée avec succès, rafraîchissement de l'affichage");
                // Rafraîchir l'affichage sur le thread JavaFX
                Platform.runLater(() -> {
                    corps.updateKanban(currentKanban);
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "[Comm->kanban] Erreur lors de l'application de la modification", e);
            }
        } else {
            LOGGER.log(Level.INFO, "[Comm->kanban] Notification pour un kanban non affiché (kanban courant: {0})", 
                    currentKanban != null ? currentKanban.getId() : "null");
        }
    }
    
    @Override
    public void displayKanban(Kanban kanban, HomeViewController homeController) {
        LOGGER.log(Level.INFO, "[Comm->Kanban] displayKanban called for: {0}", kanban.getTitle());
        // Exécuter sur le thread JavaFX car appelé depuis MsgReceiver-thread
        Platform.runLater(() -> {
            corps.displayKanban(kanban, homeController);
        });
    }
}