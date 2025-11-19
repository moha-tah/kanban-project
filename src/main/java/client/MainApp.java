package client;

import client.ihmMain.MainCore;
import client.interfaces.MainCallsDataClient;
import client.comm.CommCoreClient;
import client.data.DataClientProvider;
import server.comm.CommCoreServer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class MainApp extends Application {
    // Singleton pour accès global contrôlé => passer sonarqube check
    private static MainApp INSTANCE;

    private MainCore        core;
    private CommCoreClient  comm;
    private DataClientProvider data;
    private  CommCoreServer commServer;

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

        // Main -> Data
        core.setDataPort(data.getToMainImpl());

        // Main -> Comm
        core.setCommPort(comm.getIhmMainCallsComm());

        // Data -> Main
        data.setMainInterface(core.getDATService());

        // Data -> Comm
        data.setCommInterface(comm.getDataCallsComm());

        // comm -> data
        comm.setDataInterface(comm.getDataInterface());

        // Comm -> Main
        comm.setMainInterface(comm.getMainInterface());

        core.launchMainWindow(primaryStage);

        // Connect the client after UI launched
        comm.connect();

    }

    public static void main(String[] args) { launch(args); }
}