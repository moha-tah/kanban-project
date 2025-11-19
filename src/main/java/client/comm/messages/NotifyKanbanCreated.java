package client.comm.messages;

import java.util.Optional;
import common.dataClasses.LightKanban;

public class NotifyKanbanCreated extends Message {
    private static final long serialVersionUID = 1L;

    private LightKanban lightKanban; // Le résultat (ID + Titre)

    public NotifyKanbanCreated(LightKanban lightKanban) {
        this.lightKanban = lightKanban;
    }

    /**
     * S'exécute sur le CLIENT (Fig 20).
     * Appelle DataClient.addToListKanban()
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