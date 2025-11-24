package server;

import server.interfaces.CommCallsDataServer;

public final class ServerContext {

    private static CommCallsDataServer serverData;

    private ServerContext() {
    }

    public static void setDataInterface(CommCallsDataServer data) {
        serverData = data;
    }

    public static CommCallsDataServer getData() {
        return serverData;
    }
}