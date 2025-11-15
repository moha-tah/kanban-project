package client.ihmKanban.impl;

import client.ihmKanban.kanbanCorps;
import client.interfaces.CommClientCallsMain;
import client.interfaces.MainCallsKanban;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;

public class MainCallsKanbanImpl implements MainCallsKanban {
    private kanbanCorps myCorps;

    public MainCallsKanbanImpl(kanbanCorps myCorp) {
        myCorps = myCorp; 

    }

    @Override
    public void displayKanban( LightKanban lightKanban) {
        myCorps.
    }

}
