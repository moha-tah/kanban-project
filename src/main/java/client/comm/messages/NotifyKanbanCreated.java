package client.comm.messages;

import java.util.Optional;
import common.dataClasses.LightKanban;

/**
 * Message envoyé par le serveur pour notifier un client de la création
 * d'un nouveau kanban.
 * 
 * Ce message est traité côté client qui ajoute le kanban à sa liste locale.
 * 
 * @author Équipe Kanban
 * @version 1.0
 * @since 1.0
 * @see Message
 * @see SendNewKanban
 */
public class NotifyKanbanCreated extends Message {
    private static final long serialVersionUID = 1L;

    /**
     * La version légère du kanban créé (ID + Titre).
     */
    private final LightKanban lightKanban;

    /**
     * Constructeur du message de notification de création de kanban.
     * 
     * @param lightKanban La version légère du kanban créé (ne doit pas être null)
     */
    public NotifyKanbanCreated(LightKanban lightKanban) {
        this.lightKanban = lightKanban;
    }

    /**
     * Traite le message côté client.
     * 
     * S'exécute sur le CLIENT et appelle DataClient.addToListKanban()
     * pour ajouter le kanban à la liste locale.
     */
    @Override
    public Optional<Message> handle() {
        try {
            // Accès au contexte client via l'objet générique stocké dans ClientContext
            var dataClient = this.getClientContext().getData();

            if (dataClient != null) {
                System.out.println("CLIENT: Notification de création reçue pour " + lightKanban.getTitle());

                // Correspond à la flèche : Comm Client -> Data Client : addToListKanban
                dataClient.addToListKanban(this.lightKanban);
            }
        } catch (Throwable t) {
            // Ignoré sur le serveur
        }
        return Optional.empty();
    }
}