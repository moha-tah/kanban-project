package client.ihmKanban.impl;

import client.ihmKanban.kanbanCorps;
import client.interfaces.DataClientCallsKanban;

public class DataCallsKanbanImpl implements DataClientCallsKanban{
    private kanbanCorps myCorps; 

    public DataCallsKanbanImpl(kanbanCorps myCorp) {
        myCorps = myCorp; 

    }
}
