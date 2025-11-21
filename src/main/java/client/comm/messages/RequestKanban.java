package client.comm.messages;
import java.util.UUID;

import common.dataClasses.Kanban;

import java.util.Optional;

public class RequestKanban extends Message {

    private final UUID lightKanbanId;
    private final common.dataClasses.LightUser lightUser; 

    public RequestKanban(UUID kanbanId, common.dataClasses.LightUser user) {
        this.lightKanbanId = kanbanId;
        this.lightUser = user;
    }

    @Override
    public Optional<Message> handle() {
        try {
            // 1. Récupération de l'interface via le Contexte
            var dataServer = server.ServerContext.getData();

            if (dataServer != null) {
                // 2. Appel de la méthode EXACTE de ton interface
                Kanban fullKanban = dataServer.requestKanban(this.lightUser, this.lightKanbanId);

                // 3. Si on a un résultat, on renvoie le message de réponse
                if (fullKanban != null) {
                    return Optional.of(new SendKanban(fullKanban));
                }
            }
        } catch (NoClassDefFoundError | Exception e) {
            // Ignore l'erreur si on est coté client (ServerContext n'existe pas)
            // Ou log l'erreur si c'est un vrai problème serveur
        }

        return Optional.empty();
    }

}
