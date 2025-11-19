package server;
import server.interfaces.CommCallsDataServer;

public class ServerContext {
    private static CommCallsDataServer serverData;
    // Generic application context accessible from messages or other static places

    public static void init(CommCallsDataServer data) {
        serverData = data;
    }

    public CommCallsDataServer getData() {
        return serverData;
    }

}