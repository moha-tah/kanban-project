package server;
import server.interfaces.CommCallsDataServer;

public class ServerContext {
    private static CommCallsDataServer serverData;

    public static void init(CommCallsDataServer data) {
        serverData = data;
    }

    public static CommCallsDataServer getData() {
        return serverData;
    }
}