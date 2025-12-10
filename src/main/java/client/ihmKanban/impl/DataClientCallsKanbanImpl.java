package client.ihmKanban.impl;

import java.util.logging.Level;

import client.interfaces.DataClientCallsKanban;
import common.dataClasses.Kanban;
import client.ihmKanban.kanbanCorps;
import client.ihmMain.controllers.HomeViewController;
import client.ihmMain.controllers.ProfileController;
import client.ihmMain.controllers.ProfileDistantController;


/** Impl des callbacks de la couche Communication vers la couche Kanban (IHM Kanban). */
public class DataClientCallsKanbanImpl implements DataClientCallsKanban {
    
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


