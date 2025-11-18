package client.comm.messages;

import java.util.Optional;
import java.util.UUID;

/**
 * Demande d'autorisation: un utilisateur demande l'accès (modification) à un Kanban.
 */
public class RequestPermission extends Message {
    private static final long serialVersionUID = 1L;

    private final UUID requesterId;
    private final UUID kanbanId;

    public RequestPermission(UUID requesterId, UUID kanbanId) {
        this.requesterId = requesterId;
        this.kanbanId = kanbanId;
    }

    public UUID requesterId() { return requesterId; }
    public UUID kanbanId() { return kanbanId; }

    @Override
    public Optional<Message> handle() {
        // Executed on the SERVER side when received
        System.out.println("[SERVER] RequestPermission received: user=" + requesterId + ", kanban=" + kanbanId);
        try {
            if (server.ServerContext.getData() != null) {
                // Minimal hook: could record pending request or notify owner later
                // For now, no server state change beyond logging.
            }
        } catch (Throwable t) {
            java.util.logging.Logger.getLogger(RequestPermission.class.getName())
                    .log(java.util.logging.Level.SEVERE, "RequestPermission: erreur lors du traitement de la requête.", t);
        }
        return Optional.empty();
    }
}
