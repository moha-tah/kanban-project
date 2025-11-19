package server;

import server.comm.CommCoreServer;
import server.data.DataServProvider;
import server.data.ComCallsDataServImplementation;
import server.ServerContext;

/**
 * Standalone server application - runs independently of clients.
 * Launch this first, then launch client instances that connect to it.
 */
public class ServerApp {
    private static CommCoreServer server;
    private static DataServProvider dataProvider;

    public static void main(String[] args) {
        int port = 8080;
        
        // Parse command line args for custom port
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number, using default 8080");
            }
        }

        try {
            // Initialize server data layer
            dataProvider = new DataServProvider();
            ComCallsDataServImplementation commImpl = ComCallsDataServImplementation.newComCallsDataServImplementation();
            commImpl.setDataServProvider(dataProvider);
            dataProvider.setDataCallsComServ(commImpl);
            ServerContext.init(commImpl);
            
            // Start server
            server = new CommCoreServer(port);
            server.start();
            
            System.out.println("========================================");
            System.out.println("  Kanban Server Started");
            System.out.println("  Port: " + server.getLocalPort());
            System.out.println("  Press Ctrl+C to stop");
            System.out.println("========================================");
            
            // Keep server running
            Thread.currentThread().join();
            
        } catch (Exception e) {
            System.err.println("Fatal error starting server: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        
        // Cleanup on shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nShutting down server...");
            if (server != null) {
                try {
                    server.stop();
                    System.out.println("Server stopped cleanly");
                } catch (Exception e) {
                    System.err.println("Error stopping server: " + e.getMessage());
                }
            }
        }));
    }
}
