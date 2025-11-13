package client;

import client.ihmMain.MainCore;
import client.interfaces.MainCallsDataClient;
import client.comm.CommCoreClient;
import client.data.DataClientProvider;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {


    private static MainCore        CORE;
    private static CommCoreClient  COMM;
    private static DataClientProvider DATA;

    public static MainCore getCore()       { return CORE; }
    public static CommCoreClient getComm() { return COMM; }
    public static DataClientProvider getDataCore() { return DATA; }


    @Override
    public void start(Stage primaryStage) throws Exception {
        CORE = new MainCore();
        COMM = new CommCoreClient("127.0.0.1", 8080);
        DATA = new DataClientProvider();

        // Main -> Data (port sortant de Main vers Data)
        MainCallsDataClient dataPort = DATA;
        CORE.setDataPort(dataPort);

        // Main -> Comm (port sortant de Main vers Comm)
        CORE.setCommPort(COMM.getIhmMainCallsComm());

        // Data -> Main (callbacks Data vers Main)
        DATA.setMainInterface(CORE.getDATService());

        // Data -> Comm (callbacks Data vers Comm)
        DATA.setCommInterface(COMM.getDataCallsComm());

        CORE.launchMainWindow(primaryStage);
    }

    public static void main(String[] args) { launch(args); }
}