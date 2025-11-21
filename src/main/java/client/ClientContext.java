package client;
import client.data.CommCallsDataClientImplementation;
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

    public static void setData(CommCallsDataClientImplementation data) {
        dataInterface = data;
    }


    public static ComCallsDataClient getData() {
        return dataInterface;
    }

    public CommClientCallsKanban getKanbanComm() {
        return kanbanInterface;
    }
    
    public CommClientCallsMain getMainComm() {
        return mainInterface;
    }
}