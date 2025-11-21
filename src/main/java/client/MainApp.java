package client;

import client.ihmKanban.kanbanCorps;
import client.ihmMain.MainCore;
import client.comm.CommCoreClient;
import client.data.DataClientProvider;
import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {
    // Singleton pour accès global contrôlé => passer sonarqube check
    private static MainApp INSTANCE;

    private MainCore          core;
    private CommCoreClient    comm;
    private DataClientProvider data;
    private CommCoreServer    commServer;
    private kanbanCorps       kanbanCore;

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
        core       = new MainCore();
        data       = new DataClientProvider();
        comm       = new CommCoreClient(DEFAULT_HOST, DEFAULT_PORT); // valeurs par défaut
        kanbanCore = new kanbanCorps();

        // -------- Câblage Main -> autres couches --------
        core.setDataPort(data.getToMainImpl());
        core.setCommPort(comm.getIhmMainCallsComm());
        core.setKanbanPort(kanbanCore.getMAINService());

        // -------- Câblage Data -> autres couches --------
        data.setMainInterface(core.getDATService());
        data.setCommInterface(comm.getDataCallsComm());
        data.setKanbanInterface(kanbanCore.getDATService());

        // -------- Câblage Comm -> autres couches --------
        // Comm -> Data
        comm.setDataInterface(data.getToCommImpl());
        // Comm -> Main
        comm.setMainInterface(core.getCOMMService());
        // Comm -> Kanban
        comm.setKanbanInterface(kanbanCore.getCOMMService());

        // -------- Contexte global pour les messages réseau --------
        ClientContext.setData(data.getToCommImpl());

        // -------- Lancement de l'IHM --------
        core.launchMainWindow(primaryStage);

        // Connexion réseau après lancement de la fenêtre
        comm.connect();
    }

    public static void main(String[] args) {
        launch(args);
    }
}