package client.ihmKanban.impl;

import client.interfaces.CommClientCallsKanban;
import common.dataClasses.Kanban;
import common.dataClasses.LightKanban;
import common.dataClasses.Modification;
import client.ihmKanban.kanbanCorps;
import javafx.application.Platform;

/** Impl des callbacks de la couche Communication vers la couche Kanban (IHM Kanban). */
public class CommClientCallsKanbanImpl implements CommClientCallsKanban {
    
    private final kanbanCorps corps;   // ← Comme MainCore pour la couche main

    public CommClientCallsKanbanImpl(kanbanCorps corps) {
        this.corps = corps;
    }

    @Override
    public void deliverNotification(LightKanban idKanban, Modification modification) {
        corps.LOGGER.info("[Comm->kanban] notification reçue : "
                + "kanban=" + idKanban
                + " modification=" + modification);

    }
    
    @Override
    public void displayKanban(Kanban kanban) {
        corps.LOGGER.info("[Comm->Kanban] displayKanban called for: " + kanban.getTitle());
        // Exécuter sur le thread JavaFX car appelé depuis MsgReceiver-thread
        Platform.runLater(() -> {
            corps.displayKanban(kanban);
        });
    }
}