package client.ihmKanban.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import client.ihmKanban.kanbanCorps;
import client.ihmMain.controllers.HomeViewController;
import client.ihmMain.controllers.ProfileController;
import client.ihmMain.controllers.ProfileDistantController;
import common.dataClasses.Kanban;
import client.interfaces.DataClientCallsKanban;


/** Impl des callbacks de la couche Communication vers la couche Kanban (IHM Kanban). */
public class DataClientCallsKanbanImpl implements DataClientCallsKanban {
    
    private static final Logger LOGGER = Logger.getLogger(DataClientCallsKanbanImpl.class.getName());
    private final kanbanCorps corps;  

    public DataClientCallsKanbanImpl(kanbanCorps corps) {
        this.corps = corps;
    }

    @Override
    public void displayKanban(Kanban kanban, HomeViewController homeController, ProfileController profileController , ProfileDistantController profileDistantController) {
        corps.displayKanban(kanban, homeController, profileController, profileDistantController); 
        corps.LOGGER.info("[Data->kanban] Display kanban : "
                + "kanban=" + kanban);

    }

}


