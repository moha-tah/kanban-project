package client;

import client.ihmKanban.kanbanCorps;
import client.ihmMain.MainCore;

import java.util.logging.Logger;

import client.comm.CommCoreClient;
import client.data.DataClientProvider;
import javafx.application.Application;
import javafx.stage.Stage;


public class MainApp extends Application {
    // Singleton pour accès global contrôlé => passer sonarqube check
    private static MainApp INSTANCE;
    public static final Logger LOGGER = Logger.getLogger("MainApp");

    private MainCore          core;
    private CommCoreClient    comm;
    private DataClientProvider data;
    private kanbanCorps       kanban;

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

    public static kanbanCorps getKanbanCore() {
        return INSTANCE != null ? INSTANCE.kanban : null;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        core       = new MainCore();
        data       = new DataClientProvider();
        comm       = new CommCoreClient(); // valeurs par défaut
        kanban = new kanbanCorps();

        // -------- Câblage Main -> autres couches --------
        core.setDataPort(data.getToMainImpl());
        core.setCommPort(comm.getIhmMainCallsComm());
        core.setKanbanPort(kanban.getMAINService());

        kanban.setMainPort(core.getKANBANService());
        kanban.setCommPort(comm.getIhmKanbanCallsComm());
        kanban.setDataPort(data.getToKabanImpl());

        // -------- Câblage Data -> autres couches --------
        data.setMainInterface(core.getDATService());
        data.setCommInterface(comm.getDataCallsComm());
        data.setKanbanInterface(kanban.getDATService());

        // -------- Câblage Comm -> autres couches --------
        // Comm -> Data
        comm.setDataInterface(data.getToCommImpl());
        // Comm -> Main
        comm.setIhmMainInterface(core.getCOMMService());
        // Comm -> Kanban
        comm.setIhmKanbanInterface(kanban.getCOMMService());

        // -------- Lancement de l'IHM --------
        core.launchMainWindow(primaryStage);

        // -------- Connexion réseau (optionnelle pour tests IHM) --------
        try {
            comm.connect();
        } catch (Exception e) {
            LOGGER.info("[MainApp] Impossible de se connecter au serveur (mode test UI/offline).");
            e.printStackTrace();
            // On ne relance PAS l'exception, pour laisser l'IHM tourner
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
