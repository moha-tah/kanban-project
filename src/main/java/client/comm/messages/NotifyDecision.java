package client.comm.messages;

import java.util.Optional;
import java.util.UUID;

/**
 * Notification envoyée au demandeur pour l'informer de la décision.
 * Dans cette version, c'est un simple log côté client ; pas de routage multi-clients.
 */
public class NotifyDecision extends Message {
    private static final long serialVersionUID = 1L;

    private final UUID requesterId;
    private final UUID kanbanId;
    private final boolean accepted;

    public NotifyDecision(UUID requesterId, UUID kanbanId, boolean accepted) {
        this.requesterId = requesterId;
        this.kanbanId = kanbanId;
        this.accepted = accepted;
    }

    @Override
    public Optional<Message> handle() {
        // Executed on the side that receives the notification (here: typically client)
        System.out.println("[CLIENT] NotifyDecision: user=" + requesterId + ", kanban=" + kanbanId + ", accepted=" + accepted);
        return Optional.empty();
    }
}
