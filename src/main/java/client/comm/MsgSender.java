package client.comm;

import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.Objects;

/**
 * Gestionnaire responsable de l'envoi sécurisé de messages via un ObjectOutputStream.
 * 
 * Cette classe centralise la synchronisation, le flush et la gestion d'erreurs
 * de base pour l'envoi de messages. Toutes les opérations d'envoi sont thread-safe.
 * 
 * @author Équipe Kanban
 * @version 1.0
 * @since 1.0
 * @see MsgReceiver
 * @see Message
 */
public class MsgSender implements AutoCloseable {
    /**
     * Flux de sortie pour envoyer les objets au serveur.
     */
    private final ObjectOutputStream out;

    /**
     * Constructeur du gestionnaire d'envoi de messages.
     * 
     * @param out Le flux de sortie pour envoyer les objets (ne doit pas être null)
     */
    public MsgSender(ObjectOutputStream out) {
        this.out = Objects.requireNonNull(out);
    }

    /**
     * Envoie un message sérialisable de manière synchrone et thread-safe.
     * 
     * Cette méthode écrit l'objet dans le flux, puis effectue un flush
     * pour s'assurer que les données sont bien envoyées.
     * 
     * @param message Le message à envoyer (doit être sérialisable)
     * @throws IOException si l'envoi échoue ou si le flux sous-jacent échoue
     */
    public synchronized void send(Object message) throws IOException {
        out.writeObject(message);
        out.flush();
    }

    /**
     * Ferme le flux de sortie.
     * 
     * @throws IOException si la fermeture du flux échoue
     */
    @Override
    public void close() throws IOException {
        out.close();
    }
}
