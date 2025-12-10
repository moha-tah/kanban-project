package server;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import server.comm.CommCoreServer;
import server.data.ComCallsDataServImplementation;
import server.data.DataServProvider;
import server.data.ServerModel; // <--- Import ajouté

/**
 * Standalone server application - runs independently of clients.
 * Launch this first, then launch client instances that connect to it.
 */
public class ServerApp extends Application {
    private static final Logger logger = Logger.getLogger(ServerApp.class.getName());
    private static CommCoreServer server; // <--- Cette variable masquait le package 'server'
    private static DataServProvider dataProvider;
    private static ServerApp instance;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        instance = this;

        // 1. Chargement de l'interface graphique
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/server.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 600, 400);
            primaryStage.setTitle("Kanban Server Admin");
            primaryStage.setScene(scene);
            primaryStage.setOnCloseRequest(e -> stopServer());
            primaryStage.show();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to load Server GUI", e);
            System.exit(1);
        }

        // 2. Lancement du serveur dans un Thread séparé
        new Thread(() -> {
            int port = 8080;

            if (!getParameters().getRaw().isEmpty()) {
                try {
                    port = Integer.parseInt(getParameters().getRaw().get(0));
                } catch (NumberFormatException e) {
                    logger.warning("Invalid port number, using default 8080");
                }
            }

            try {
                // Initialize server data layer
                dataProvider = new DataServProvider();

                // Correction : Utilisation directe de la classe importée (plus de préfixe server.)
                ServerModel model = new ServerModel();
                dataProvider.setModel(model);

                ComCallsDataServImplementation commImpl = ComCallsDataServImplementation.newComCallsDataServImplementation();
                commImpl.setDataServProvider(dataProvider);
                dataProvider.setDataCallsComServ(commImpl);

                // Correction : Utilisation directe de ServerContext (il est dans le même package)
                ServerContext.setProvider(dataProvider);

                // Start server
                server = new CommCoreServer(port);
                server.setDataInterface(commImpl);
                server.start();

                logger.info("========================================");
                logger.info("  Kanban Server Started");
                logger.log(Level.INFO, "  Port: {0}", server.getLocalPort());
                logger.info("  Press Stop Button to stop");
                logger.info("========================================");

            } catch (IOException e) {
                logger.log(Level.SEVERE, "Fatal error starting server", e);
                System.exit(1);
            }
        }).start();
    }

    public static void requestStop() {
        if (instance != null) {
            instance.stopServer();
        }
    }

    public void stopServer() {
        logger.info("Stopping server...");
        if (server != null) {
            server.stop();
        }
        Platform.exit();
        System.exit(0);
    }
}