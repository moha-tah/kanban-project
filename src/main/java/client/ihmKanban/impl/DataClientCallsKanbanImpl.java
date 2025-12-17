package client.ihmKanban.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import client.ihmKanban.kanbanCorps;
import client.ihmMain.controllers.HomeViewController;
import client.interfaces.DataClientCallsKanban;
import common.dataClasses.Kanban;


/** Impl des callbacks de la couche Communication vers la couche Kanban (IHM Kanban). */
public class DataClientCallsKanbanImpl implements DataClientCallsKanban {
    
    private static final Logger LOGGER = Logger.getLogger(DataClientCallsKanbanImpl.class.getName());
    private final kanbanCorps corps;  

    public DataClientCallsKanbanImpl(kanbanCorps corps) {
        this.corps = corps;
    }

    @Override
    public void displayKanban(Kanban kanban, HomeViewController homeController ) {
        corps.displayKanban(kanban, homeController); 
        LOGGER.log(Level.INFO, "[Data->kanban] Display kanban : kanban={0}", kanban);

    }

    @Override
    public void updateKanban(Kanban kanban) {
        corps.updateKanban(kanban);
        System.out.println("[Data->kanban] Update kanban : kanban=" + kanban.getTitle());
        kanbanCorps.LOGGER.info("[Data->kanban] Display kanban : ");

    }

}


