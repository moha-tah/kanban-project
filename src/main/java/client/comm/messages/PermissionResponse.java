package client.comm.messages;

import java.util.Optional;
import java.util.UUID;

/**
 * Réponse du propriétaire à une demande d'autorisation.
 */
public class PermissionResponse extends Message {
    private static final long serialVersionUID = 1L;

    private final UUID requesterId;
    private final UUID kanbanId;
    private final boolean accepted;

    public PermissionResponse(UUID requesterId, UUID kanbanId, boolean accepted) {
        this.requesterId = requesterId;
        this.kanbanId = kanbanId;
        this.accepted = accepted;
    }

    public UUID requesterId() { return requesterId; }
    public UUID kanbanId() { return kanbanId; }
    public boolean accepted() { return accepted; }

    @Override
    public Optional<Message> handle() {
        // Executed on SERVER when owner responds
        System.out.println("[SERVER] PermissionResponse: user=" + requesterId + ", kanban=" + kanbanId + ", accepted=" + accepted);
        try {
            var data = server.ServerContext.getData();
            if (data != null && accepted) {
                data.addAuthorizedUser(kanbanId, requesterId);
            }
        } catch (Throwable t) {
            java.util.logging.Logger.getLogger(PermissionResponse.class.getName())
                    .log(java.util.logging.Level.SEVERE, "PermissionResponse: erreur lors du traitement de la réponse.", t);
        }
        // In a full implementation we would forward a NotifyDecision to the requester client here.
        return Optional.empty();
    }
}
