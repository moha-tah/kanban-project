package client;
import client.interfaces.ComCallsDataClient;

public class ClientContext {
    private static ComCallsDataClient dataInterface;

    public static void init(ComCallsDataClient data) {
        dataInterface = data;
    }

    public static ComCallsDataClient getData() {
        return dataInterface;
    }
}