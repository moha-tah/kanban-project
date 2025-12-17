package client.comm.messages;

import java.util.Optional;

import common.dataClasses.Kanban;

/**
 * Message envoyé par le serveur pour transmettre un kanban complet à un client.
 * 
 * Ce message est la réponse à une demande {@link RequestKanban} et contient
 * le kanban complet avec toutes ses colonnes et tâches.
 * 
 * @author Équipe Kanban
 * @version 1.0
 * @since 1.0
 * @see Message
 * @see RequestKanban
 */
public class SendKanban extends Message {

    /**
     * Le kanban complet à transmettre au client.
     */
    private final Kanban kanban;

    /**
     * Constructeur du message d'envoi de kanban.
     * 
     * @param kanban Le kanban complet à transmettre (ne doit pas être null)
     */
    public SendKanban(Kanban kanban) {
        this.kanban = kanban;
    }


    @Override
    public Optional<Message> handle() {
        // Le message va chercher l'interface tout seul via la classe statique
        // Note : Il faut gérer le cas où on est sur le serveur (try/catch ou vérification)
        try {
            System.out.println("[SendKanban] Received Kanban from server: " + kanban.getTitle() + " (ID: " + kanban.getId() + ")");
            
            // COMM appelle directement displayKanban sur IHM Kanban
            //this.getClientContext().getKanbanComm().displayKanban(this.kanban); a décommenter par la suite 
            
            System.out.println("[SendKanban] Kanban displayed via IHM Kanban");
        } catch (Throwable t) {
            // On ignore si on n'est pas sur le client
            System.err.println("[SendKanban] Error or not on client: " + t.getMessage());
        }
        return Optional.empty();
    }
}
