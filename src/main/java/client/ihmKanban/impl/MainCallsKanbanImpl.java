package client.ihmKanban.impl;
import client.interfaces.MainCallsKanban;
import client.ihmKanban.kanbanCorps;
import client.ihmMain.controllers.HomeViewController;
import client.ihmMain.controllers.ProfileViewController;
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
    public void openCreateForm(Kanban kanban, HomeViewController homeController) { 
        corps.displayKanban(kanban, homeController); 
        kanbanCorps.LOGGER.info("[MainCallsKanban] openCreateForm called");

    }
    
    @Override
    public void openKanbanViewFromProfile(Kanban kanban, ProfileViewController profileController){
        corps.displayKanbanFromProfile(kanban, profileController);
        kanbanCorps.LOGGER.info("[MainCallsKanban] openKanbanViewFromProfile called");
    }

}
