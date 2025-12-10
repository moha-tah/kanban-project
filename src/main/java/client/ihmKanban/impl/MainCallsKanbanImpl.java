package client.ihmKanban.impl;
import client.ihmKanban.kanbanCorps;
import client.ihmMain.controllers.HomeViewController;
import client.ihmMain.controllers.ProfileController;
import client.ihmMain.controllers.ProfileDistantController;
import client.interfaces.MainCallsKanban;
import common.dataClasses.Kanban;


public class MainCallsKanbanImpl implements MainCallsKanban {
    
    private final kanbanCorps corps;  

    public MainCallsKanbanImpl(kanbanCorps corps) {
        this.corps = corps;
    }


    @Override
    public void displaySnapshotList(){
        // A IMPLEMENTER 
        kanbanCorps.LOGGER.info("[MainCallsKanban] displaySnapshotList called");

    }

    @Override
    public void openKanban(Kanban kanban, HomeViewController homeController, ProfileController profileController, ProfileDistantController profileDistantController) { 
        corps.displayKanban(kanban, homeController, profileController,profileDistantController); 
        kanbanCorps.LOGGER.info("[MainCallsKanban] openKanban called");

    }
    
    
}
