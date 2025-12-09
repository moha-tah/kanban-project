package server;

import server.interfaces.CommCallsDataServer;
import server.data.DataServProvider;

public final class ServerContext {

    private static CommCallsDataServer serverData;
    // Also store the concrete provider to allow access to ServerModel/cache when needed
    private static DataServProvider dataProvider;

    public static void setDataInterface(CommCallsDataServer data) {
        serverData = data;
    }

    public static CommCallsDataServer getData() {
        return serverData;
    }

    public static void setProvider(DataServProvider provider) {
        dataProvider = provider;
    }

    public static DataServProvider getProvider() {
        return dataProvider;
    }
}