package server;

import server.comm.CommCoreServer;
import server.data.DataServProvider;
import server.data.ComCallsDataServImplementation;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Standalone server application - runs independently of clients.
 * Launch this first, then launch client instances that connect to it.
 */
public class ServerApp {
    private static final Logger logger = Logger.getLogger(ServerApp.class.getName());
    private static CommCoreServer server;
    private static DataServProvider dataProvider;

    public static void main(String[] args) {
        int port = 8080;
        
        // Parse command line args for custom port
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                logger.warning("Invalid port number, using default 8080");
            }
        }

        try {
            // Initialize server data layer
            dataProvider = new DataServProvider();
            ComCallsDataServImplementation commImpl = ComCallsDataServImplementation.newComCallsDataServImplementation();
            commImpl.setDataServProvider(dataProvider);
            dataProvider.setDataCallsComServ(commImpl);

            // Start server
            server = new CommCoreServer(port);
            server.setDataInterface(commImpl);
            server.start();
            
            logger.info("========================================");
            logger.info("  Kanban Server Started");
            logger.log(Level.INFO, "  Port: {0}", server.getLocalPort());
            logger.info("  Press Ctrl+C to stop");
            logger.info("========================================");
            
            // Keep server running
            Thread.currentThread().join();
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.log(Level.WARNING, "Server interrupted", e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Fatal error starting server", e);
            System.exit(1);
        }
        
        // Cleanup on shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("\nShutting down server...");
            if (server != null) {
                try {
                    server.stop();
                    logger.info("Server stopped cleanly");
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Error stopping server", e);
                }
            }
        }));
    }
}
