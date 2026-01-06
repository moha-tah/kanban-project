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
import java.util.logging.Logger;

import common.dataClasses.LightUser;
import server.data.ComCallsDataServImplementation;
import server.data.ServerModel;
import server.interfaces.CommCallsDataServer;

public class CommCoreServer {

    private static final Logger LOGGER = Logger.getLogger(CommCoreServer.class.getName());
    private final int port;
    private ServerSocket serverSocket;
    private boolean isRunning;
    private Thread serverThread;
    private static CommCoreServer instance;
    private CommCallsDataServer dataServer;

    private final List<SrvMsgSender> connectedClients = new CopyOnWriteArrayList<>();
    private final Map<SrvMsgSender, common.dataClasses.LightUser> clientToUserMap = new ConcurrentHashMap<>();

    public CommCoreServer(int port) {
        this.port = port;
        instance = this;
    }

    public static void triggerBroadcast() {
        if (instance != null) {
            LOGGER.info("SERVER: Broadcast manuel déclenché.");
            instance.broadcastUsersAndKanbansUpdate();
        }
    }

    public static boolean sendToUser(UUID targetUserId, Object message) {
        if (instance == null)
            return false;

        for (Map.Entry<SrvMsgSender, common.dataClasses.LightUser> entry : instance.clientToUserMap.entrySet()) {
            if (entry.getValue().getId().equals(targetUserId)) {
                try {
                    LOGGER.log(Level.INFO, "SERVER: Routage message vers {0}", entry.getValue().getUsername());
                    entry.getKey().send(message);
                    return true;
                } catch (IOException e) {
                    java.util.logging.Logger.getLogger(CommCoreServer.class.getName())
                            .log(java.util.logging.Level.SEVERE, "SERVER: Erreur lors de l'envoi de la réponse.", e);
                }
            }
        }
        LOGGER.log(Level.WARNING, "SERVER: Utilisateur cible {0} non trouv\u00e9 ou d\u00e9connect\u00e9.", targetUserId);
        return false;
    }

    public void setDataInterface(CommCallsDataServer dataInterface) {
        this.dataServer = dataInterface;
        server.ServerContext.setDataInterface(dataInterface);
    }

    public void start() throws IOException {
        try {
            serverSocket = new ServerSocket(port);
        } catch (BindException be) {
            serverSocket = new ServerSocket(0);
            System.err.println("SERVER: Port " + port + " occupé, bascule sur le port " + serverSocket.getLocalPort());
        }
        isRunning = true;
        LOGGER.log(Level.INFO, "SERVER: D\u00e9marr\u00e9 sur le port {0}", serverSocket.getLocalPort());

        serverThread = new Thread(() -> {
            while (isRunning) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    LOGGER.log(Level.INFO, "SERVER: Nouvelle connexion TCP : {0}", clientSocket.getInetAddress());
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
                                connectedClients.remove(finalMsgSender);
                                clientToUserMap.remove(finalMsgSender);
                            }
                        });

                        if (receivedMsg instanceof client.comm.messages.ConnectionRequest connReq) {
                            if (connReq.getUser() != null) {
                                clientToUserMap.put(finalMsgSender, connReq.getUser());
                                LOGGER.log(Level.INFO, "SERVER: Utilisateur authentifi\u00e9 : {0} (ID: {1})", new Object[]{connReq.getUser().getUsername(), connReq.getUser().getId()});
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
                connectedClients.remove(finalMsgSender);
                java.util.logging.Logger logger = java.util.logging.Logger.getLogger(CommCoreServer.class.getName());

                LightUser userId = clientToUserMap.remove(finalMsgSender);
                if (userId != null) {
                    try {
                        if (dataServer != null) {
                            ServerModel model = ((ComCallsDataServImplementation) dataServer).getDataServProvider()
                                    .getModel();
                            model.removeConnectedUser(userId.getId());
                            logger.log(java.util.logging.Level.INFO,
                                    "SERVER: Utilisateur {0} retiré de la liste des connectés.", userId);

                            broadcastUsersAndKanbansUpdate();
                        }
                    } catch (Exception e) {
                        logger.log(java.util.logging.Level.WARNING, "SERVER: Erreur lors du retrait de l'utilisateur.",
                                e);
                    }
                }

                logger.log(java.util.logging.Level.INFO, "SERVER: Client déconnecté, retiré de la liste.");
            });

            msgReceiver.start();

        } catch (IOException e) {
            java.util.logging.Logger.getLogger(CommCoreServer.class.getName())
                    .log(java.util.logging.Level.SEVERE, "SERVER: Erreur de connexion avec un client.", e);
        }
    }

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
                    common.dataClasses.LightUser currentUser = clientToUserMap.get(clientSender);
                    java.util.List<common.dataClasses.LightKanban> visibleKanbans;

                    if (currentUser != null) {
                        visibleKanbans = dataServer.getVisibleKanbansForUser(currentUser);
                    } else {
                        visibleKanbans = new java.util.ArrayList<>();
                    }

                    client.comm.messages.UpdateUsersAndKanbansListResponse updateMsg = new client.comm.messages.UpdateUsersAndKanbansListResponse(
                            new java.util.ArrayList<>(users),
                            new java.util.ArrayList<>(visibleKanbans));

                    clientSender.send(updateMsg);

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
            isRunning = false;
            try {
                if (serverSocket != null && !serverSocket.isClosed()) {
                    serverSocket.close();
                }
                for (SrvMsgSender client : connectedClients) {
                    try {
                        client.close();
                    } catch (Exception e) { /* Ignorer */ }
                }
                connectedClients.clear();
                LOGGER.info("SERVER: Serveur arrêté proprement.");
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "SERVER: Erreur lors de l'arrêt.", e);
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
                        java.util.logging.Logger.getLogger(SrvMsgReceiver.class.getName())
                                .log(java.util.logging.Level.FINE, "SrvMsgReceiver: Client déconnecté (EOF).");
                        break;
                    } catch (IOException e) {
                        break;
                    }
                    try {
                        handler.accept(msg);
                    } catch (Throwable t) {
                        java.util.logging.Logger.getLogger(SrvMsgReceiver.class.getName())
                                .log(java.util.logging.Level.SEVERE, "SrvMsgReceiver: Exception in handler.", t);
                    }
                }
            } catch (ClassNotFoundException e) {
                java.util.logging.Logger.getLogger(SrvMsgReceiver.class.getName())
                        .log(java.util.logging.Level.SEVERE, "SrvMsgReceiver: Class not found.", e);
            } finally {
                running.set(false);
                if (onDisconnect != null) {
                    try {
                        onDisconnect.run();
                    } catch (Exception e) {}
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