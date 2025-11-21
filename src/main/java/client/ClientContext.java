package client;
import client.data.CommCallsDataClientImplementation;
import client.interfaces.ComCallsDataClient;

public class ClientContext {
    private static ComCallsDataClient dataInterface;

    public static void setData(CommCallsDataClientImplementation data) {
        dataInterface = data;
    }

    public static ComCallsDataClient getData() {
        return dataInterface;
    }
}