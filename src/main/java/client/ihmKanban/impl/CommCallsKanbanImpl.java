package client.ihmKanban.impl;

import client.ihmKanban.kanbanCorps;
import client.interfaces.CommClientCallsKanban;

public class CommCallsKanbanImpl implements CommClientCallsKanban{
    private kanbanCorps myCorps;

    public CommCallsKanbanImpl(kanbanCorps myCorp) {
        myCorps = myCorp; 

    }
}
