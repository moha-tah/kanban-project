package client.comm.messages;
import java.util.UUID;

import common.dataClasses.Kanban;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;

import java.util.Optional;

public class RequestKanban extends Message {

    private final LightKanban lightKanbanId; // ou LightKanban.id
    private final LightUser lightUserId; // ou LightKanban.id

    public RequestKanban(LightKanban kanbanId, LightUser userId) {
        this.lightKanbanId = kanbanId;
        this.lightUserId = userId;
    }
    
    @Override
    public Optional<Message> handle() {
        try {
            // 1. Récupération de l'interface via le Contexte
            // (On utilise le chemin complet ou l'import server.ServerContext)
            var dataServer = server.ServerContext.getData();

            if (dataServer != null) {

                // 2. Appel de la méthode EXACTE de ton interface
                Kanban fullKanban = dataServer.requestKanban(lightUserId, lightKanbanId.getId());

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
