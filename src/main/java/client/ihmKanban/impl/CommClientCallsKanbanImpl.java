package client.ihmKanban.impl;

import java.util.logging.Level;

import client.interfaces.CommClientCallsKanban;
import common.dataClasses.Kanban;
import common.dataClasses.LightKanban;
import common.dataClasses.Modification;
import client.ihmKanban.kanbanCorps;
import javafx.application.Platform;
import client.ihmMain.controllers.HomeViewController;
import client.ihmMain.controllers.ProfileController;


/** Impl des callbacks de la couche Communication vers la couche Kanban (IHM Kanban). */
public class CommClientCallsKanbanImpl implements CommClientCallsKanban {
    
    private final kanbanCorps corps;   // ← Comme MainCore pour la couche main

    public CommClientCallsKanbanImpl(kanbanCorps corps) {
        this.corps = corps;
    }

    @Override
    public void deliverNotification(LightKanban idKanban, Modification modification) {
        corps.LOGGER.log(Level.INFO, "[Comm->kanban] notification re\u00e7ue : kanban={0} modification={1}", new Object[]{idKanban, modification});

    }
    
    @Override
    public void displayKanban(Kanban kanban, HomeViewController homeController, ProfileController profileController) {
        corps.LOGGER.info("[Comm->Kanban] displayKanban called for: " + kanban.getTitle());
        // Exécuter sur le thread JavaFX car appelé depuis MsgReceiver-thread
        Platform.runLater(() -> {
            corps.displayKanban(kanban, homeController, profileController);
        });
    }
}