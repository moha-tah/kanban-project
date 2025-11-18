package server.comm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Optional;

// Fallback: common.messages.Message was not available, we declare a local package-level Message
// interface at the end of this file so this compilation unit can compile independently.
// On réutilise vos classes utilitaires existantes
import client.comm.MsgReceiver; 
import client.comm.MsgSender; 
// Note : Idéalement MsgSender/Receiver devraient être dans un package "common.comm" 
// s'ils sont identiques, sinon copiez-les dans server.comm.

public class CommCoreServer {

    private final int port;
    private ServerSocket serverSocket;
    private boolean isRunning;

    public CommCoreServer(int port) {
        this.port = port;
    }

    /**
     * Démarre le serveur et attend les connexions en boucle.
     * Cette méthode est bloquante, il vaut mieux l'appeler dans un Thread dédié coté Main serveur.
     */
    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        isRunning = true;
        System.out.println("SERVER: Démarré sur le port " + port);

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
    }

    public void stop() {
        isRunning = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
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
            MsgSender msgSender = new MsgSender(out);

            // Création du Receiver avec la logique de réaction (Callback)
            MsgReceiver msgReceiver = new MsgReceiver(in, (obj) -> {
                if (obj instanceof Message receivedMsg) {
                    
                    // ----------------------------------------------------
                    // C'est ICI que la méthode handle() du message est exécutée
                    // (Ex: MsgRequestKanban.handle() qui interroge la BDD)
                    // ----------------------------------------------------
                    try {
                        Optional<Message> response = receivedMsg.handle();

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

// Fallback package-local Message interface to replace missing common.messages.Message.
// This matches the minimal contract used in this file: a handle() method returning
// an Optional<Message> and possibly throwing exceptions.
interface Message {
    java.util.Optional<Message> handle() throws Exception;
}