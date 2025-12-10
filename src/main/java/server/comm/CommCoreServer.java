package server.comm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

import common.dataClasses.LightUser;
import server.data.ComCallsDataServImplementation;
import server.data.ServerModel;
import server.interfaces.CommCallsDataServer;

public class CommCoreServer {

    private final int port;
    private ServerSocket serverSocket;
    private boolean isRunning;
    private Thread serverThread;
    private static CommCoreServer instance;
    private CommCallsDataServer dataServer;

    // Liste thread-safe des clients connectés pour diffuser les mises à jour
    private final List<SrvMsgSender> connectedClients = new CopyOnWriteArrayList<>();

    // Map pour associer chaque connexion client à l'utilisateur connecté
    private final Map<SrvMsgSender, common.dataClasses.LightUser> clientToUserMap = new ConcurrentHashMap<>();

    public CommCoreServer(int port) {
        this.port = port;
        instance = this;
    }

    public static void triggerBroadcast() { // acces statique pour déclencher le broadcast
        if (instance != null) {
            System.out.println("SERVER: Broadcast manuel déclenché.");
            instance.broadcastUsersAndKanbansUpdate();
        }
    }

    public static boolean sendToUser(UUID targetUserId, Object message) {
        if (instance == null)
            return false;

        // On cherche le socket associé à cet utilisateur
        for (Map.Entry<SrvMsgSender, common.dataClasses.LightUser> entry : instance.clientToUserMap.entrySet()) {
            if (entry.getValue().getId().equals(targetUserId)) {
                try {
                    System.out.println("SERVER: Routage message vers " + entry.getValue().getUsername());
                    entry.getKey().send(message);
                    return true;
                } catch (IOException e) {
                    java.util.logging.Logger.getLogger(CommCoreServer.class.getName())
                            .log(java.util.logging.Level.SEVERE, "SERVER: Erreur lors de l'envoi de la réponse.", e);
                }
            }
        }
        System.out.println("SERVER: Utilisateur cible " + targetUserId + " non trouvé ou déconnecté.");
        return false;
    }

    /**
     * Configure l'interface Data globale du serveur via ServerContext.
     */
    public void setDataInterface(CommCallsDataServer dataInterface) {
        this.dataServer = dataInterface;
        server.ServerContext.setDataInterface(dataInterface);
    }

    /*
     * Démarre le serveur dans un thread séparé.
     */
    public void start() throws IOException {
        try {
            serverSocket = new ServerSocket(port);
        } catch (BindException be) {
            serverSocket = new ServerSocket(0);
            System.err.println("SERVER: Port " + port + " occupé, bascule sur le port " + serverSocket.getLocalPort());
        }
        isRunning = true;
        System.out.println("SERVER: Démarré sur le port " + serverSocket.getLocalPort());

        serverThread = new Thread(() -> {
            while (isRunning) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("SERVER: Nouvelle connexion TCP : " + clientSocket.getInetAddress());
                    new Thread(() -> handleClientConnection(clientSocket)).start();
                } catch (IOException e) {
                    if (isRunning) {
                        System.err.println("SERVER: Erreur lors de l'acceptation d'un client : " + e.getMessage());
                    }
                }
            }
        });

        serverThread.start();
    }

    public int getLocalPort() {
        return (serverSocket != null) ? serverSocket.getLocalPort() : port;
    }

    public void stop() {
        isRunning = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            if (serverThread != null) {
                serverThread.join(5000);
            }
        } catch (IOException e) {
            java.util.logging.Logger.getLogger(CommCoreServer.class.getName())
                    .log(java.util.logging.Level.SEVERE, "SERVER: Erreur lors de l'arrêt du serveur.", e);
        } catch (InterruptedException e) {
            java.util.logging.Logger.getLogger(CommCoreServer.class.getName())
                    .log(java.util.logging.Level.SEVERE, "SERVER: Erreur lors de l'arrêt du serveur.", e);
            Thread.currentThread().interrupt();
        }
    }

    @SuppressWarnings("resource")
    private void handleClientConnection(Socket socket) {
        SrvMsgSender msgSender;
        try {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            msgSender = new SrvMsgSender(out);
            final SrvMsgSender finalMsgSender = msgSender;

            connectedClients.add(msgSender);

            SrvMsgReceiver msgReceiver = new SrvMsgReceiver(in, obj -> {
                if (obj instanceof client.comm.messages.Message receivedMsg) {

                    try {
                        java.util.Optional<client.comm.messages.Message> response = receivedMsg.handle();

                        response.ifPresent(resp -> {
                            try {
                                finalMsgSender.send(resp);
                            } catch (IOException e) {
                                java.util.logging.Logger.getLogger(CommCoreServer.class.getName())
                                        .log(java.util.logging.Level.SEVERE,
                                                "SERVER: Erreur lors de l'envoi de la réponse.", e);
                                // Client déconnecté, le retirer
                                connectedClients.remove(finalMsgSender);
                                clientToUserMap.remove(finalMsgSender);
                            }
                        });

                        if (receivedMsg instanceof client.comm.messages.ConnectionRequest connReq) {
                            if (connReq.getUser() != null) {
                                clientToUserMap.put(finalMsgSender, connReq.getUser());
                                System.out.println("SERVER: Utilisateur authentifié : " + connReq.getUser().getUsername() + " (ID: " + connReq.getUser().getId() + ")");
                            }
                            broadcastUsersAndKanbansUpdate();
                        }

                    } catch (Exception e) {
                        String logMsg = "SERVER: Erreur lors du traitement du message "
                                + receivedMsg.getClass().getSimpleName();
                        java.util.logging.Logger.getLogger(CommCoreServer.class.getName())
                                .log(java.util.logging.Level.SEVERE, logMsg, e);
                    }
                } else {
                    java.util.logging.Logger.getLogger(CommCoreServer.class.getName())
                            .log(java.util.logging.Level.WARNING,
                                    "SERVER: Message inattendu reçu: {0}", obj);
                }
            }, () -> {
                // Callback appelé quand le receiver s'arrête (client déconnecté)
                connectedClients.remove(finalMsgSender);
                java.util.logging.Logger logger = java.util.logging.Logger.getLogger(CommCoreServer.class.getName());

                // Retirer l'utilisateur associé à cette connexion de la liste des utilisateurs
                // connectés
                LightUser userId = clientToUserMap.remove(finalMsgSender);
                if (userId != null) {
                    try {
                        if (dataServer != null) {
                            ServerModel model = ((ComCallsDataServImplementation) dataServer).getDataServProvider()
                                    .getModel();
                            model.removeConnectedUser(userId.getId());
                            logger.log(java.util.logging.Level.INFO,
                                    "SERVER: Utilisateur {0} retiré de la liste des connectés.", userId);

                            // Diffuser la mise à jour de la liste des utilisateurs
                            broadcastUsersAndKanbansUpdate();
                        }
                    } catch (Exception e) {
                        logger.log(java.util.logging.Level.WARNING, "SERVER: Erreur lors du retrait de l'utilisateur.",
                                e);
                    }
                }

                logger.log(java.util.logging.Level.INFO, "SERVER: Client déconnecté, retiré de la liste.");

                // Afficher le nombre d'utilisateurs restants
                try {
                    if (dataServer != null) {
                        var users = dataServer.getUsersList();
                        logger.log(Level.INFO, "SERVER: {0} utilisateur(s) connect\u00e9(s) restant(s).", users.size());
                    }
                } catch (Exception e) {
                    // Ignorer les erreurs lors de l'affichage
                }
            });

            msgReceiver.start();

        } catch (IOException e) {
            java.util.logging.Logger.getLogger(CommCoreServer.class.getName())
                    .log(java.util.logging.Level.SEVERE, "SERVER: Erreur de connexion avec un client.", e);
        }
    }

    /**
     * Envoie les listes mises à jour d'utilisateurs et de kanbans à tous les
     * clients connectés.
     */
    private void broadcastUsersAndKanbansUpdate() {
        try {
            if (dataServer == null) {
                java.util.logging.Logger.getLogger(CommCoreServer.class.getName())
                        .log(java.util.logging.Level.SEVERE,
                                "SERVER: Data interface is null in ServerContext, broadcast annulé.");
                return;
            }

            var users = dataServer.getUsersList();
            java.util.logging.Logger logger = java.util.logging.Logger.getLogger(CommCoreServer.class.getName());

            for (SrvMsgSender clientSender : connectedClients) {
                try {
                    // 1. Récupération de l'objet User (Map<SrvMsgSender, LightUser>)
                    common.dataClasses.LightUser currentUser = clientToUserMap.get(clientSender);

                    java.util.List<common.dataClasses.LightKanban> visibleKanbans;

                    if (currentUser != null) {
                        // --- CORRECTION ICI : On passe l'objet LightUser directement ---
                        visibleKanbans = dataServer.getVisibleKanbansForUser(currentUser);
                    } else {
                        visibleKanbans = new java.util.ArrayList<>();
                    }

                    // 3. Création du message
                    client.comm.messages.UpdateUsersAndKanbansListResponse updateMsg = new client.comm.messages.UpdateUsersAndKanbansListResponse(
                            new java.util.ArrayList<>(users),
                            new java.util.ArrayList<>(visibleKanbans));

                    clientSender.send(updateMsg);

                    // Log
                    String pseudo = (currentUser != null) ? currentUser.getUsername() : "Anonyme";
                    logger.log(Level.INFO, "SERVER: Broadcast vers {0} -> {1} kanbans envoyés.",
                            new Object[] { pseudo, visibleKanbans.size() });

                } catch (IOException e) {
                    connectedClients.remove(clientSender);
                    clientToUserMap.remove(clientSender);
                }
            }
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(CommCoreServer.class.getName())
                    .log(java.util.logging.Level.SEVERE, "SERVER: Erreur broadcast.", e);
        }
    }

    class SrvMsgSender implements AutoCloseable {
        private final ObjectOutputStream out;

        SrvMsgSender(ObjectOutputStream out) {
            this.out = out;
        }

        public synchronized void send(Object message) throws IOException {
            out.writeObject(message);
            out.flush();
        }

        @Override
        public void close() throws IOException {
            out.close();
        }
    }

    class SrvMsgReceiver implements Runnable, AutoCloseable {
        private final ObjectInputStream in;
        private final java.util.function.Consumer<Object> handler;
        private final Runnable onDisconnect;
        private final java.util.concurrent.atomic.AtomicBoolean running = new java.util.concurrent.atomic.AtomicBoolean(
                false);
        private Thread worker;

        SrvMsgReceiver(ObjectInputStream in, java.util.function.Consumer<Object> handler, Runnable onDisconnect) {
            this.in = in;
            this.handler = handler;
            this.onDisconnect = onDisconnect;
        }

        public void start() {
            if (running.compareAndSet(false, true)) {
                worker = new Thread(this, "SrvMsgReceiver-thread");
                worker.setDaemon(true);
                worker.start();
            }
        }

        public void stop() {
            running.set(false);
            if (worker != null && Thread.currentThread() != worker) {
                try {
                    worker.join(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        @Override
        public void run() {
            try {
                while (running.get()) {
                    Object msg;
                    try {
                        msg = in.readObject();
                    } catch (java.io.EOFException e) {
                        // EOFException est normale quand le client ferme la connexion proprement
                        java.util.logging.Logger.getLogger(SrvMsgReceiver.class.getName())
                                .log(java.util.logging.Level.FINE,
                                        "SrvMsgReceiver: Client déconnecté (EOF).");
                        break;
                    } catch (IOException e) {
                        // Traiter les SocketException (connection reset) comme une déconnexion normale
                        java.util.logging.Logger logger = java.util.logging.Logger
                                .getLogger(SrvMsgReceiver.class.getName());
                        if (e instanceof java.net.SocketException) {
                            // Connection reset / abort — log informatif sans stacktrace
                            logger.log(java.util.logging.Level.INFO,
                                    "SrvMsgReceiver: I/O error while reading message: {0}", e.getMessage());
                        } else {
                            // Autres erreurs I/O gardent la stacktrace pour le débogage
                            logger.log(java.util.logging.Level.WARNING,
                                    "SrvMsgReceiver: I/O error while reading message.", e);
                        }
                        break;
                    }
                    try {
                        handler.accept(msg);
                    } catch (Throwable t) {
                        java.util.logging.Logger.getLogger(SrvMsgReceiver.class.getName())
                                .log(java.util.logging.Level.SEVERE,
                                        "SrvMsgReceiver: Exception in handler.", t);
                    }
                }
            } catch (ClassNotFoundException e) {
                java.util.logging.Logger.getLogger(SrvMsgReceiver.class.getName())
                        .log(java.util.logging.Level.SEVERE,
                                "SrvMsgReceiver: Class not found while reading message.", e);
            } finally {
                running.set(false);
                // Notifier que le client est déconnecté
                if (onDisconnect != null) {
                    try {
                        onDisconnect.run();
                    } catch (Exception e) {
                        java.util.logging.Logger.getLogger(SrvMsgReceiver.class.getName())
                                .log(java.util.logging.Level.WARNING,
                                        "SrvMsgReceiver: Erreur dans onDisconnect callback.", e);
                    }
                }
            }
        }

        @Override
        public void close() throws IOException {
            try (in) {
                stop();
            }
        }
    }
}