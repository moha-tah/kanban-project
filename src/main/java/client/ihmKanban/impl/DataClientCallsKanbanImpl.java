package client.ihmKanban.impl;

import client.interfaces.DataClientCallsKanban;
import common.dataClasses.Kanban;
import client.ihmKanban.kanbanCorps;
import common.dataClasses.LightKanban;

/** Impl des callbacks de la couche Communication vers la couche Kanban (IHM Kanban). */
public class DataClientCallsKanbanImpl implements DataClientCallsKanban {
    
    private final kanbanCorps corps;  

    public DataClientCallsKanbanImpl(kanbanCorps corps) {
        this.corps = corps;
    }

    @Override
    public void displayKanban(LightKanban kanban) {
        // A IMPLEMENTER 
        System.out.println("[Data->kanban] Display kanban : "
                + "kanban=" + kanban);

    }
}


