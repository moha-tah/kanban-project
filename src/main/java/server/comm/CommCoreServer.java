package server.comm;

import server.ServerContext;
import server.interfaces.CommCallsDataServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class CommCoreServer {

    private final int port;
    private ServerSocket serverSocket;
    private boolean isRunning;
    private Thread serverThread;

    // Liste thread-safe des clients connectés pour diffuser les mises à jour
    private final List<SrvMsgSender> connectedClients = new CopyOnWriteArrayList<>();

    public CommCoreServer(int port) {
        this.port = port;
    }

    /**
     * Configure l'interface Data globale du serveur via ServerContext.
     */
    public void setDataInterface(CommCallsDataServer dataInterface) {
        ServerContext.setDataInterface(dataInterface);
    }

    /**
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
                    System.out.println("SERVER: Nouveau client connecté : " + clientSocket.getInetAddress());
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
        } catch (IOException | InterruptedException e) {
            java.util.logging.Logger.getLogger(CommCoreServer.class.getName())
                    .log(java.util.logging.Level.SEVERE, "SERVER: Erreur lors de l'arrêt du serveur.", e);
        }
    }

    @SuppressWarnings("resource")
    private void handleClientConnection(Socket socket) {
        SrvMsgSender msgSender = null;
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
                                        .log(java.util.logging.Level.SEVERE, "SERVER: Erreur lors de l'envoi de la réponse.", e);
                                // Client déconnecté, le retirer
                                connectedClients.remove(finalMsgSender);
                            }
                        });

                        if (receivedMsg instanceof client.comm.messages.ConnectionRequest) {
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
                logger.log(java.util.logging.Level.INFO, "SERVER: Client déconnecté, retiré de la liste.");
                
                // Afficher le nombre d'utilisateurs restants
                try {
                    CommCallsDataServer data = ServerContext.getData();
                    if (data != null) {
                        var users = data.getUsersList();
                        logger.info("SERVER: " + users.size() + " utilisateur(s) connecté(s) restant(s).");
                    }
                } catch (Exception e) {
                    // Ignorer les erreurs lors de l'affichage
                }
            });

            msgReceiver.start();

        } catch (IOException e) {
            java.util.logging.Logger.getLogger(CommCoreServer.class.getName())
                    .log(java.util.logging.Level.SEVERE, "SERVER: Erreur de connexion avec un client.", e);
            if (msgSender != null) {
                connectedClients.remove(msgSender);
            }
        }
    }

    /**
     * Envoie les listes mises à jour d'utilisateurs et de kanbans à tous les clients connectés.
     */
    private void broadcastUsersAndKanbansUpdate() {
        try {
            CommCallsDataServer data = ServerContext.getData();
            if (data == null) {
                java.util.logging.Logger.getLogger(CommCoreServer.class.getName())
                        .log(java.util.logging.Level.SEVERE,
                                "SERVER: Data interface is null in ServerContext, broadcast annulé.");
                return;
            }

            var users = data.getUsersList();
            var kanbans = data.getKanbansList();

            java.util.logging.Logger logger = java.util.logging.Logger.getLogger(CommCoreServer.class.getName());
            logger.info("SERVER: Creating broadcast message with " + users.size() + " users for " + connectedClients.size() + " clients");

            // Créer un nouveau message pour chaque client pour éviter les problèmes de référence partagée
            for (SrvMsgSender clientSender : connectedClients) {
                try {
                    // Créer une nouvelle instance du message pour chaque client
                    client.comm.messages.UpdateUsersAndKanbansListResponse updateMsg =
                            new client.comm.messages.UpdateUsersAndKanbansListResponse(
                                    new java.util.ArrayList<>(users), // Copie de la liste
                                    new java.util.ArrayList<>(kanbans)); // Copie de la liste
                    
                    logger.info("SERVER: Sending broadcast to client with " + updateMsg.getUsers().size() + " users");
                    clientSender.send(updateMsg);
                } catch (IOException e) {
                    logger.log(java.util.logging.Level.WARNING,
                            "SERVER: Échec de l'envoi de la mise à jour à un client.", e);
                    connectedClients.remove(clientSender);
                }
            }

            // Afficher la liste des utilisateurs connectés
            logger.log(java.util.logging.Level.INFO,
                    "SERVER: Broadcast de {0} utilisateurs et {1} kanbans à {2} clients.",
                    new Object[]{users.size(), kanbans.size(), connectedClients.size()});
            
            // Afficher les détails des utilisateurs connectés
            if (!users.isEmpty()) {
                StringBuilder userList = new StringBuilder("Utilisateurs connectés: ");
                for (int i = 0; i < users.size(); i++) {
                    common.dataClasses.LightUser user = users.get(i);
                    userList.append(user.getUsername());
                    if (i < users.size() - 1) {
                        userList.append(", ");
                    }
                }
                logger.info("SERVER: " + userList.toString());
            } else {
                logger.info("SERVER: Aucun utilisateur connecté.");
            }

        } catch (Exception e) {
            java.util.logging.Logger.getLogger(CommCoreServer.class.getName())
                    .log(java.util.logging.Level.SEVERE, "SERVER: Erreur lors du broadcast des listes.", e);
        }
    }
}

class SrvMsgSender implements AutoCloseable {
    private final ObjectOutputStream out;

    SrvMsgSender(ObjectOutputStream out) { this.out = out; }

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
    private final java.util.concurrent.atomic.AtomicBoolean running =
            new java.util.concurrent.atomic.AtomicBoolean(false);
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
                    // Autres erreurs I/O sont des vraies erreurs
                    java.util.logging.Logger.getLogger(SrvMsgReceiver.class.getName())
                            .log(java.util.logging.Level.WARNING,
                                    "SrvMsgReceiver: I/O error while reading message.", e);
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
                            .log(java.util.logging.Level.WARNING, "SrvMsgReceiver: Erreur dans onDisconnect callback.", e);
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