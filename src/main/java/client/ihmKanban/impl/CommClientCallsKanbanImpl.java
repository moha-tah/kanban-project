package client.ihmKanban.impl;

import client.interfaces.CommClientCallsKanban;
import common.dataClasses.LightKanban;
import common.dataClasses.Modification;
import client.ihmKanban.kanbanCorps;

/** Impl des callbacks de la couche Communication vers la couche Kanban (IHM Kanban). */
public class CommClientCallsKanbanImpl implements CommClientCallsKanban {
    
    private final kanbanCorps corps;   // ← Comme MainCore pour la couche main

    public CommClientCallsKanbanImpl(kanbanCorps corps) {
        this.corps = corps;
    }

    @Override
    public void deliverNotification(LightKanban idKanban, Modification modification) {
        /*System.out.println("[Comm->kanban] notification reçue : "
                + "kanban=" + idKanban
                + " modification=" + modification);*/

    }
}