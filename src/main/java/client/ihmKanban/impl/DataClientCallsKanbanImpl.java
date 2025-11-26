package client.ihmKanban.impl;

import java.util.logging.Level;

import client.interfaces.DataClientCallsKanban;
import common.dataClasses.Kanban;
import client.ihmKanban.kanbanCorps;

/** Impl des callbacks de la couche Communication vers la couche Kanban (IHM Kanban). */
public class DataClientCallsKanbanImpl implements DataClientCallsKanban {
    
    private final kanbanCorps corps;  

    public DataClientCallsKanbanImpl(kanbanCorps corps) {
        this.corps = corps;
    }

    @Override
    public void displayKanban(Kanban kanban) {
        corps.displayKanban(kanban); 
        corps.LOGGER.log(Level.INFO, "[Data->kanban] Display kanban : kanban={0}", kanban);

    }
}


