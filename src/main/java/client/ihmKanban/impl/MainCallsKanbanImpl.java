package client.ihmKanban.impl;
import client.interfaces.MainCallsKanban;
import client.ihmKanban.kanbanCorps;
import client.ihmMain.controllers.HomeViewController;
import client.ihmMain.controllers.ProfileController;
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
    public void openCreateForm(Kanban kanban, HomeViewController homeController, ProfileController profileController) { 
        corps.displayKanban(kanban, homeController, profileController); 
        kanbanCorps.LOGGER.info("[MainCallsKanban] openCreateForm called");

    }
    
    
}
