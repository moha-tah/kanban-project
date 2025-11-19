package client.comm.messages;

import java.util.Optional;
import common.dataClasses.Kanban;
import common.dataClasses.LightKanban;

public class SendNewKanban extends Message {
    private static final long serialVersionUID = 1L;

    private final Kanban newKanban; // Le kanban complet créé par l'utilisateur

    public SendNewKanban(Kanban newKanban) {
        this.newKanban = newKanban;
    }

    @Override
    public Optional<Message> handle() {
        try {
            // Accès au contexte serveur
            var dataServer = this.getServerContext().getData();
            
            if (dataServer != null) {
                System.out.println("SERVEUR: Demande de création de Kanban reçue.");

                // Appel de saveKanban(Kanban) -> retourne LightKanban (avec l'UUID généré)
                // Correspond à la flèche : CommServer -> DataServer : saveKanban
                LightKanban createdLightKanban = dataServer.saveKanban(this.newKanban);

                if (createdLightKanban != null) {
                    System.out.println("SERVEUR: Kanban créé avec succès (ID: " + createdLightKanban.getId() + ")");
                    
                    // Correspond à la flèche retour : notifyKanbanCreated
                    return Optional.of(new NotifyKanbanCreated(createdLightKanban));
                }
            }
        } catch (Throwable t) {
            // Ignoré sur le client
        }
        return Optional.empty();
    }
}