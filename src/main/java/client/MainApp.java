package client;

import client.ihmMain.MainCore;
import client.comm.CommCoreClient;
import client.data.DataClientProvider;
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
    private kanbanCorps kanbanCorps;

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
        kanbanCorps = new kanbanCorps();

        // Main -> Data
        core.setDataPort(data.getToMainImpl());

        // Main -> Comm
        core.setCommPort(comm.getIhmMainCallsComm());
        core.setKanbanPort(kanbanCorps.getMAINService());

        // Data -> Main
        data.setMainInterface(core.getDATService());

        // Data -> Comm
        data.setCommInterface(comm.getDataCallsComm());
        data.setKanbanInterface(kanbanCorps.getDATService());

        // comm -> data
        comm.setDataInterface(comm.getDataInterface());

        // Comm -> Main
        comm.setMainInterface(comm.getMainInterface());
        comm.setKanbanInterface(kanbanCorps.getCOMMService());

        kanbanCorps.setCommPort(kanbanCorps.getCommPort());
        kanbanCorps.setDataPort(kanbanCorps.getDataPort());
        kanbanCorps.setMainPort(kanbanCorps.getMainPort());

        core.launchMainWindow(primaryStage);

        // Connect the client after UI launched
        comm.connect();

        // Ensure we stop network resources when the UI is closed
        primaryStage.setOnCloseRequest(event -> {
            try {
                if (comm != null) {
                    try {
                        comm.disconnect();
                    } catch (Exception ex) {
                        System.err.println("Erreur lors de la déconnexion du client: " + ex.getMessage());
                    }
                }
            } finally {
                // Ensure JavaFX exits and the JVM terminates
                Platform.exit();
                System.exit(0);
            }
        });

        // JVM shutdown hook as a safety net for non-UI shutdowns
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                if (comm != null) comm.disconnect();
            } catch (Exception ignored) {}
        }));

    }

    public static void main(String[] args) { launch(args); }
}