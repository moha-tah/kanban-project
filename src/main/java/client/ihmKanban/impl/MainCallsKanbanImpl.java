package client.ihmKanban.impl;
import client.interfaces.MainCallsKanban;
import client.ihmKanban.kanbanCorps;
import client.interfaces.CommClientCallsMain;
import client.interfaces.MainCallsKanban;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;

public class MainCallsKanbanImpl implements MainCallsKanban {
    
    private final kanbanCorps corps;  

    public MainCallsKanbanImpl(kanbanCorps corps) {
        this.corps = corps;
    }


    @Override
    public void displaySnapshotList(){
        // A IMPLEMENTER 
        System.out.println("[MainCallsKanban] displaySnapshotList called");
    }

    @Override
    public void openCreateForm(LightKanban lightKanban){ 
        // A IMPLEMENTER 
        System.out.println("[MainCallsKanban] openCreateForm called");
    }

}
