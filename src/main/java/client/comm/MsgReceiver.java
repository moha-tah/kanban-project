package client.comm;

import client.comm.messages.Message;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Récepteur de messages qui lit continuellement des objets depuis un ObjectInputStream
 * et les transmet au gestionnaire fourni. S'exécute dans son propre thread.
 * 
 * Cette classe gère la réception asynchrone de messages depuis le serveur.
 * Elle démarre un thread démon qui lit les messages et les transmet au gestionnaire.
 * En cas de déconnexion, elle peut exécuter un callback de notification.
 * 
 * @author Équipe Kanban
 * @version 1.0
 * @since 1.0
 * @see Message
 * @see MsgSender
 */
public class MsgReceiver implements Runnable, AutoCloseable {
    /**
     * Flux d'entrée pour lire les objets depuis le serveur.
     */
    private final ObjectInputStream in;
    
    /**
     * Gestionnaire appelé pour chaque message reçu.
     */
    private final Consumer<Message> handler;
    
    /**
     * Indicateur atomique pour contrôler l'exécution de la boucle de réception.
     */
    private final AtomicBoolean running = new AtomicBoolean(false);
    
    /**
     * Callback exécuté lors de la déconnexion du serveur.
     */
    private final Runnable onDisconnect;
    
    /**
     * Thread sur lequel s'exécute la boucle de réception.
     */
    private Thread worker;
    
    /**
     * Logger pour les messages de log de cette classe.
     */
    private static final Logger LOGGER = Logger.getLogger(MsgReceiver.class.getName());

    /**
     * Constructeur du récepteur de messages.
     * 
     * @param in Le flux d'entrée pour lire les objets (ne doit pas être null)
     * @param handler Le gestionnaire appelé pour chaque message reçu (ne doit pas être null)
     * @param onDisconnect Callback exécuté lors de la déconnexion (peut être null)
     */
    public MsgReceiver(ObjectInputStream in, Consumer<Message> handler, Runnable onDisconnect) {
        this.in = Objects.requireNonNull(in);
        this.handler = Objects.requireNonNull(handler);
        this.onDisconnect = onDisconnect;
    }

    /**
     * Démarre la boucle de réception sur un thread dédié.
     * 
     * Le thread créé est un thread démon qui s'arrêtera automatiquement
     * lorsque l'application principale se termine.
     */
    public void start() {
        if (running.compareAndSet(false, true)) {
            worker = new Thread(this, "MsgReceiver-thread");
            worker.setDaemon(true);
            worker.start();
        }
    }

    /**
     * Arrête la boucle de réception et attend la fin du thread.
     * 
     * Cette méthode attend jusqu'à 2 secondes pour que le thread se termine
     * proprement. Si le thread ne se termine pas dans ce délai, l'attente
     * est interrompue.
     */
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
                Object obj = in.readObject();
                if (obj instanceof Message msg) {
                    try {
                        handler.accept(msg);
                    } catch (Throwable t) {
                        LOGGER.log(Level.SEVERE, "MsgReceiver: Exception in handler.", t);
                    }
                }
            }
        } catch (java.io.EOFException | java.net.SocketException e) {
            LOGGER.info("Connexion au serveur interrompue.");
            if (onDisconnect != null) {
                onDisconnect.run();
            }
        } catch (IOException | ClassNotFoundException e) {
            if (running.get()) {
                LOGGER.log(Level.INFO, "MsgReceiver: I/O error or stream closed.", e);
            }
        } finally {
            running.set(false);
        }
    }

    @Override
    public void close() throws IOException {
        try (in) {
            stop();
        }
    }
}