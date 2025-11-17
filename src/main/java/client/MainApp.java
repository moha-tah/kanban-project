package client;

import client.ihmMain.MainCore;
import client.interfaces.MainCallsDataClient;
import client.comm.CommCoreClient;
import client.data.DataClientProvider;
import client.ihmKanban.kanbanCorps;
import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {
    // Singleton pour accès global contrôlé => passer sonarqube check
    private static MainApp INSTANCE;

    private MainCore        core;
    private CommCoreClient  comm;
    private DataClientProvider data;
    private kanbanCorps kanban;


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

    public static kanbanCorps getKanbanCorps() {
        return INSTANCE != null ? INSTANCE.kanban : null;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        core = new MainCore();
        comm = new CommCoreClient("127.0.0.1", 8080);
        data = new DataClientProvider();
        kanban = new kanbanCorps(); 

        // Main -> Data
        core.setDataPort(data.getToMainImpl());

        // Main -> Comm
        core.setCommPort(comm.getIhmMainCallsComm());

        // Data -> Main
        data.setMainInterface(core.getDATService());

        // Data -> Comm
        data.setCommInterface(comm.getDataCallsComm());

        // Kanban -> Data
        kanban.setDataPort(data.getToKabanImpl());

        //Kanban -> Main
        kanban.setMainPort(core.getKANBANService());

        //Kanban -> Comm 
        kanban.setCommPort(comm.getIhmKanbanCallsComm());

        core.launchMainWindow(primaryStage);
    }

    public static void main(String[] args) { launch(args); }
}