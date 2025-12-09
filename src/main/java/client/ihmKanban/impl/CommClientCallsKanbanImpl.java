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