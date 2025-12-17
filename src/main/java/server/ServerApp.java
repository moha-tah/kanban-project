package server;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import server.comm.CommCoreServer;
import server.data.ComCallsDataServImplementation;
import server.data.DataServProvider;
import server.data.ServerModel;

/**
 * Standalone server application - runs independently of clients.
 * Launch this first, then launch client instances that connect to it.
 */
public class ServerApp extends Application {
    private static final Logger logger = Logger.getLogger(ServerApp.class.getName());
    private static CommCoreServer server;
    private static DataServProvider dataProvider;
    private static ServerApp instance;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        instance = this;
        int port = promptForPort();

        // 1. Chargement de l'interface graphique
        ServerViewController controller;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/server.fxml"));
            Parent root = loader.load();
            controller = loader.getController();
            controller.setPort(port);
            Scene scene = new Scene(root, 600, 400);
            primaryStage.setTitle("Kanban Server Admin");
            primaryStage.setScene(scene);
            primaryStage.setOnCloseRequest(e -> stopServer());
            primaryStage.show();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to load Server GUI", e);
            System.exit(1);
            return;
        }

        // 2. Lancement du serveur dans un Thread séparé
        final int selectedPort = port;
        final ServerViewController uiController = controller;
        new Thread(() -> {
            try {
                // Initialize server data layer
                dataProvider = new DataServProvider();

                ServerModel model = new ServerModel();
                dataProvider.setModel(model);

                ComCallsDataServImplementation commImpl = ComCallsDataServImplementation.newComCallsDataServImplementation();
                commImpl.setDataServProvider(dataProvider);
                dataProvider.setDataCallsComServ(commImpl);

                ServerContext.setProvider(dataProvider);

                // Start server, with fallback if port is taken
                int effectivePort = selectedPort;
                try {
                    server = new CommCoreServer(effectivePort);
                    server.setDataInterface(commImpl);
                    server.start();
                } catch (IOException ex) {
                    if (ex instanceof BindException) {
                        logger.warning("Selected port is in use. Choosing a random available port.");
                        effectivePort = findAvailablePort();
                        server = new CommCoreServer(effectivePort);
                        server.setDataInterface(commImpl);
                        server.start();
                    } else {
                        throw ex;
                    }
                }

                logger.info("========================================");
                logger.info("  Kanban Server Started");
                int actualPort = server.getLocalPort();
                logger.log(Level.INFO, "  Port: {0}", actualPort);
                Platform.runLater(() -> uiController.setPort(actualPort));
                logger.info("  Press Stop Button to stop");
                logger.info("========================================");

            } catch (IOException e) {
                logger.log(Level.SEVERE, "Fatal error starting server", e);
                System.exit(1);
            }
        }).start();
    }

    private int promptForPort() {
        int defaultPort = 8080;

        // If provided via arguments, prefer that
        if (!getParameters().getRaw().isEmpty()) {
            try {
                return Integer.parseInt(getParameters().getRaw().get(0));
            } catch (NumberFormatException e) {
                logger.warning("Invalid port argument, falling back to dialog");
            }
        }

        TextInputDialog dialog = new TextInputDialog(String.valueOf(defaultPort));
        dialog.setTitle("Select Server Port");
        dialog.setHeaderText("Enter the port to run the Kanban Server");
        dialog.setContentText("Port:");

        return dialog.showAndWait().map(value -> {
            try {
                int p = Integer.parseInt(value.trim());
                if (p <= 0 || p > 65535) {
                    throw new NumberFormatException("Port out of range");
                }
                return p;
            } catch (NumberFormatException ex) {
                logger.warning("Invalid port entered, using default 8080");
                return defaultPort;
            }
        }).orElse(defaultPort);
    }

    private static int findAvailablePort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            socket.setReuseAddress(true);
            return socket.getLocalPort();
        } catch (IOException e) {
            // Fallback to default if cannot determine
            return 8080;
        }
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