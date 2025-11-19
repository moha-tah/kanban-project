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

        // Start server first, potentially on a fallback port
        commServer = new CommCoreServer(8080);
        commServer.start();
        int actualPort = commServer.getLocalPort();

        // Create client using the actual bound port and connect
        comm = new CommCoreClient("127.0.0.1", actualPort);

        // Main -> Data
        core.setDataPort(data.getToMainImpl());

        // Main -> Comm
        core.setCommPort(comm.getIhmMainCallsComm());

        // Data -> Main
        data.setMainInterface(core.getDATService());

        // Data -> Comm
        data.setCommInterface(comm.getDataCallsComm());

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
                if (commServer != null) {
                    try {
                        commServer.stop();
                    } catch (Exception ex) {
                        System.err.println("Erreur lors de l'arrêt du serveur: " + ex.getMessage());
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
            try {
                if (commServer != null) commServer.stop();
            } catch (Exception ignored) {}
        }));

    }

    public static void main(String[] args) { launch(args); }
}