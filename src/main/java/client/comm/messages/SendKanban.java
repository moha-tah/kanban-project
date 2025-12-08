package client.comm.messages;

import java.util.Optional;

import common.dataClasses.Kanban;

public class SendKanban extends Message {

    private final Kanban kanban; // Remplacer "Object" par votre classe métier "Kanban"

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
