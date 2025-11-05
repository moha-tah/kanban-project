import interfaces.KanbanCallsData;
import interfaces.KanbanCallsMain;
import interfaces.KanbanCallsComm;
import interfaces.CommCallsKanban;
import interfaces.DataCallsKanban;
import interfaces.MainCallsKanban;


public class kanbanCorps {
    private CommCallsKanbanImpl requestFromComm;
    private DataCallsKanbanImpl requestFromData;
    private MainCallsKanbanImpl requestFromMain;
    private ManageDisplay myManageDisplay;
    private KanbanCallsData requestToData;
    private KanbanCallsMain requestToMain;
    private KanbanCallsComm requestToComm;

    void initialize() {
        requestFromComm = new CommCallsKanbanImpl(this);
        requestFromData = new DataCallsKanbanImpl(this);
        requestFromMain = new MainCallsKanbanImpl(this);
        myManageDisplay = new ManageDisplay(this);

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
