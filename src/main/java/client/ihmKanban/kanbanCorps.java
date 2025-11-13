package client.ihmKanban;

import client.ihmKanban.controllers.ManageDisplay;
import client.ihmKanban.impl.DataCallsKanbanImpl;
import client.interfaces.IhmKanbanCallsComm;
import client.interfaces.KanbanCallsDataClient;
import client.interfaces.KanbanCallsMain;
import client.ihmKanban.impl.CommCallsKanbanImpl;
import client.ihmKanban.impl.DataCallsKanbanImpl;
import client.ihmKanban.impl.MainCallsKanbanImpl;



public class kanbanCorps {
    private CommCallsKanbanImpl requestFromComm;
    private DataCallsKanbanImpl requestFromData;
    private MainCallsKanbanImpl requestFromMain;
    private ManageDisplay myManageDisplay;
    private KanbanCallsDataClient requestToData;
    private KanbanCallsMain requestToMain;
    private IhmKanbanCallsComm requestToComm;

    kanbanCorps() {
        /* 
        requestFromComm = new CommCallsKanbanImpl(this);
        requestFromData = new DataCallsKanbanImpl(this);
        requestFromMain = new MainCallsKanbanImpl(this);
        myManageDisplay = new ManageDisplay(this);*/

    }
    CommCallsKanbanImpl getRequestFromComm(){
        return requestFromComm;
    }

    DataCallsKanbanImpl getRequestFromData(){
        return requestFromData;
    }

    MainCallsKanbanImpl getRequestFromMain(){
        return requestFromMain;
    }
}
