import client.src.interfaces.*; 
import client.src.ihmKanban.CommCallsKanbanImpl; 
import client.src.ihmKanban.DataCallsKanbanImpl;
import client.src.ihmKanban.MainCallsKanbanImpl;
import client.src.ihmKanban.ManageDisplay;


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
