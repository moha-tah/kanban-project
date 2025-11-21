package server;
import server.interfaces.CommCallsDataServer;

public class ServerContext {
    private static CommCallsDataServer serverData;
    // Generic application context accessible from messages or other static places

    public void setDataInterface(CommCallsDataServer serverData) {
        ServerContext.serverData = serverData;
    }

    public static CommCallsDataServer getData() {
        return serverData;
    }

}