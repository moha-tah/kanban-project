package client;

import client.ihmKanban.kanbanCorps;
import client.ihmMain.MainCore;
import client.comm.CommCoreClient;
import client.data.DataClientProvider;
import client.ihmKanban.kanbanCorps;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import server.comm.CommCoreServer;

public class MainApp extends Application {
    // Singleton pour accès global contrôlé => passer sonarqube check
    private static MainApp INSTANCE;

    private MainCore        core;
    private CommCoreClient  comm;
    private DataClientProvider data;
    private CommCoreServer commServer;
    private kanbanCorps kanbanCore;

    public MainApp() {
        INSTANCE = this;
    }

    private static final String DEFAULT_HOST = "127.0.0.1";
    private static final int    DEFAULT_PORT = 8080;

    public static MainCore getCore() {
        return INSTANCE != null ? INSTANCE.core : null;
    }

    public static CommCoreClient getComm() {
        return INSTANCE != null ? INSTANCE.comm : null;
    }

    public static DataClientProvider getDataCore() {
        return INSTANCE != null ? INSTANCE.data : null;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        core = new MainCore();
        data = new DataClientProvider();
        comm = new CommCoreClient(DEFAULT_HOST, DEFAULT_PORT); // gérer côté cient en dynamique avec valeur par défault
        kanbanCore = new kanbanCorps();

        // Main -> Data
        core.setDataPort(data.getToMainImpl());

        // Main -> Comm
        core.setCommPort(comm.getIhmMainCallsComm());
        core.setKanbanPort(kanbanCore.getMAINService());

        // Data -> Main
        data.setMainInterface(core.getDATService());

        // Data -> Comm
        data.setCommInterface(comm.getDataCallsComm());
        data.setKanbanInterface(kanbanCore.getDATService());

        // comm -> data
        comm.setDataInterface(comm.getDataInterface());

        // Comm -> Main
        comm.setMainInterface(comm.getMainInterface());
        comm.setKanbanInterface(kanbanCore.getCOMMService());

        kanbanCore.setCommPort(kanbanCore.getCommPort());
        kanbanCore.setDataPort(kanbanCore.getDataPort());
        kanbanCore.setMainPort(kanbanCore.getMainPort());

        core.launchMainWindow(primaryStage);

        // Connect the client after UI launched
        comm.connect();

        // Ensure we stop network resources when the UI is closed

    }

    public static void main(String[] args) { launch(args); }
}