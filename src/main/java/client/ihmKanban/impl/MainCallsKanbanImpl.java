package client.ihmKanban.impl;
import client.interfaces.MainCallsKanban;
import client.ihmKanban.kanbanCorps;
import client.interfaces.CommClientCallsMain;
import client.interfaces.MainCallsKanban;
import common.dataClasses.Kanban;
import common.dataClasses.LightUser;
import javafx.scene.control.ScrollPane;

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
    public void openCreateForm(Kanban kanban, ScrollPane kanbanArea) { 
        corps.displayKanban(kanban, kanbanArea); 
        kanbanCorps.LOGGER.info("[MainCallsKanban] openCreateForm called");

    }

}
