package client;
import client.interfaces.ComCallsDataClient;
import client.interfaces.CommClientCallsKanban;
import client.interfaces.CommClientCallsMain;


public class ClientContext {
    private static ComCallsDataClient dataInterface;
    private static CommClientCallsKanban kanbanInterface;
    private static CommClientCallsMain mainInterface;

    public static void init(ComCallsDataClient data, CommClientCallsKanban commKanban,
            CommClientCallsMain commMain) {
        dataInterface = data;
        kanbanInterface = commKanban;
        mainInterface = commMain;
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