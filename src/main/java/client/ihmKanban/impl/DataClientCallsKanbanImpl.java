package client.ihmKanban.impl;

import client.ihmKanban.kanbanCorps;
import client.ihmMain.controllers.HomeViewController;
import client.interfaces.DataClientCallsKanban;
import common.dataClasses.Kanban;


/** Impl des callbacks de la couche Communication vers la couche Kanban (IHM Kanban). */
public class DataClientCallsKanbanImpl implements DataClientCallsKanban {
    
    private final kanbanCorps corps;  

    public DataClientCallsKanbanImpl(kanbanCorps corps) {
        this.corps = corps;
    }

    @Override
    public void displayKanban(Kanban kanban, HomeViewController homeController ) {
        corps.displayKanban(kanban, homeController); 
        kanbanCorps.LOGGER.info("[Data->kanban] Display kanban : ");

    }

    @Override
    public void updateKanban(Kanban kanban) {
        corps.updateKanban(kanban);

        kanbanCorps.LOGGER.info("[Data->kanban] Display kanban : ");

    }

}


