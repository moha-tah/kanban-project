package server;

import server.interfaces.CommCallsDataServer;

public final class ServerContext {

    private static CommCallsDataServer serverData;

    public static void setDataInterface(CommCallsDataServer data) {
        serverData = data;
    }

    public static CommCallsDataServer getData() {
        return serverData;
    }
}