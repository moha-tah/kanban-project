package client;
import client.interfaces.ComCallsDataClient;
import client.interfaces.CommClientCallsKanban;
import client.interfaces.CommClientCallsMain;


public class ClientContext {
    private static ComCallsDataClient dataInterface;
    private static CommClientCallsKanban kanbanInterface;
    private static CommClientCallsMain mainInterface;

    public void setDataInterface(ComCallsDataClient dataInterface) {
        ClientContext.dataInterface = dataInterface;
    }
    
    public void setKanbanInterface(CommClientCallsKanban kanbanInterface) {
        ClientContext.kanbanInterface = kanbanInterface;
    }

    public void setMainInterface(CommClientCallsMain mainInterface) {
        ClientContext.mainInterface = mainInterface;
    }


    public ComCallsDataClient getData() {
        return dataInterface;
    }

    public CommClientCallsKanban getKanbanComm() {
        return kanbanInterface;
    }
    
    public CommClientCallsMain getMainComm() {
        return mainInterface;
    }
}