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

            client.ClientContext.getData().send(this.kanban);
        } catch (Throwable t) {
            // On ignore si on n'est pas sur le client
        }
        return Optional.empty();
    }
}
