package server.comm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

// Fallback: common.messages.Message was not available, we declare a local package-level Message
// interface at the end of this file so this compilation unit can compile independently.
// On réutilise vos classes utilitaires existantes
// Local lightweight sender/receiver to avoid cross-package dependency on client classes
// Note : Idéalement MsgSender/Receiver devraient être dans un package "common.comm" 
// s'ils sont identiques, sinon copiez-les dans server.comm.

public class CommCoreServer {

    private final int port;
    private ServerSocket serverSocket;
    private boolean isRunning;
    private Thread serverThread;

    public CommCoreServer(int port) {
        this.port = port;
    }

    /**
     * Démarre le serveur dans un thread séparé pour ne pas bloquer l'application.
     */
    public void start() throws IOException {
        try {
            serverSocket = new ServerSocket(port);
        } catch (BindException be) {
            // fallback to an ephemeral port if requested port is unavailable
            serverSocket = new ServerSocket(0);
            System.err.println("SERVER: Port " + port + " occupé, bascule sur le port " + serverSocket.getLocalPort());
        }
        isRunning = true;
        System.out.println("SERVER: Démarré sur le port " + serverSocket.getLocalPort());

        serverThread = new Thread(() -> {
            while (isRunning) {
                try {
                    // 1. Attente bloquante d'un client
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("SERVER: Nouveau client connecté : " + clientSocket.getInetAddress());

                    // 2. On délègue la gestion de ce client à un Thread séparé
                    // pour ne pas bloquer l'arrivée d'autres clients.
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
                serverThread.join(5000); // Wait up to 5 seconds for thread to finish
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Logique de gestion pour UN client spécifique.
     * Configure les flux, le Sender et le Receiver pour ce client.
     */
    @SuppressWarnings("resource")
    private void handleClientConnection(Socket socket) {
        try {
            // IMPORTANT : Toujours créer l'ObjectOutputStream AVANT l'ObjectInputStream
            // et faire un flush() sinon les deux côtés vont s'attendre mutuellement (deadlock).
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.flush(); 
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            // Création du Sender pour répondre à ce client spécifique
            SrvMsgSender msgSender = new SrvMsgSender(out);

            // Création du Receiver avec la logique de réaction (Callback)
            SrvMsgReceiver msgReceiver = new SrvMsgReceiver(in, (obj) -> {
                if (obj instanceof client.comm.messages.Message receivedMsg) {
                    
                    // ----------------------------------------------------
                    // C'est ICI que la méthode handle() du message est exécutée
                    // (Ex: MsgRequestKanban.handle() qui interroge la BDD)
                    // ----------------------------------------------------
                    try {
                        java.util.Optional<client.comm.messages.Message> response = receivedMsg.handle();

                        // Si handle() retourne une réponse (ex: MsgSendKanban), on l'envoie
                        if (response.isPresent()) {
                            try {
                                msgSender.send(response.get());
                            } catch (IOException e) {
                                System.err.println("SERVER: Erreur lors de l'envoi de la réponse.");
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("SERVER: Erreur lors du traitement du message " + receivedMsg.getClass().getSimpleName());
                        e.printStackTrace();
                    }
                }
            });

            // Démarrer l'écoute pour ce client
            msgReceiver.start();

            // Note : Ce thread se termine ici, mais le thread interne de MsgReceiver continue de tourner
            // tant que la connexion est active.
        } catch (IOException e) {
            System.err.println("SERVER: Erreur de connexion avec un client.");
            e.printStackTrace();
        }
    }
}

// Note: We now rely on client.comm.messages.Message for the contract

// --- Local helper classes to send/receive messages ---
class SrvMsgSender implements AutoCloseable {
    private final ObjectOutputStream out;
    SrvMsgSender(ObjectOutputStream out) { this.out = out; }
    public synchronized void send(Object message) throws java.io.IOException {
        out.writeObject(message);
        out.flush();
    }
    public void close() throws java.io.IOException { out.close(); }
}

class SrvMsgReceiver implements Runnable, AutoCloseable {
    private final ObjectInputStream in;
    private final java.util.function.Consumer<Object> handler;
    private final java.util.concurrent.atomic.AtomicBoolean running = new java.util.concurrent.atomic.AtomicBoolean(false);
    private Thread worker;

    SrvMsgReceiver(ObjectInputStream in, java.util.function.Consumer<Object> handler) {
        this.in = in; this.handler = handler;
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
            try { worker.join(2000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
    }
    public void run() {
        try {
            while (running.get()) {
                Object msg = in.readObject();
                try { handler.accept(msg); } catch (Throwable t) { t.printStackTrace(); }
            }
        } catch (java.io.IOException e) {
            // stream closed, exit
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            running.set(false);
        }
    }
    public void close() throws java.io.IOException { stop(); in.close(); }
}