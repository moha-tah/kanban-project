package client.ihmKanban.impl;

import java.util.logging.Level;

import client.interfaces.DataClientCallsKanban;
import common.dataClasses.Kanban;
import client.ihmKanban.kanbanCorps;
import client.ihmMain.controllers.HomeViewController;


/** Impl des callbacks de la couche Communication vers la couche Kanban (IHM Kanban). */
public class DataClientCallsKanbanImpl implements DataClientCallsKanban {
    
    private final kanbanCorps corps;  

    public DataClientCallsKanbanImpl(kanbanCorps corps) {
        this.corps = corps;
    }

    @Override
    public void displayKanban(Kanban kanban, HomeViewController homeController ) {
        corps.displayKanban(kanban, homeController); 
        corps.LOGGER.info("[Data->kanban] Display kanban : "
                + "kanban=" + kanban);

    }

    @Override
    public void updateKanban(Kanban kanban) {
        corps.updateKanban(kanban);

        corps.LOGGER.info("[Data->kanban] Display kanban : " + "kanban=" + kanban);

    }

}


