package client.comm.messages;

import java.util.Optional;
import java.util.UUID;

import common.dataClasses.LightUser;

/**
 * Server -> Client: forward distant user's profile to requester.
 */
public class ForwardProfileAnswer extends Message {
    private static final long serialVersionUID = 1L;

    private final UUID requesterId;
    private final LightUser requestedUser;

    public ForwardProfileAnswer(UUID requesterId, LightUser requestedUser) {
        this.requesterId = requesterId;
        this.requestedUser = requestedUser;
    }

    public UUID requesterId() { return requesterId; }
    public LightUser requestedUser() { return requestedUser; }

    @Override
    public Optional<Message> handle() {
        // Executed on CLIENT side
        try {
            var commToMain = this.getClientContext() != null ? this.getClientContext().getMainComm() : null;
            // Invoke callback; underlying UI layer should handle null user gracefully
            if (commToMain != null) {
                commToMain.displayDistantProfile(requestedUser);
            }
            // Return a Noop reply when user not found to avoid constant return warnings
            return requestedUser == null ? Optional.of(new NoopMessage()) : Optional.empty();
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(ForwardProfileAnswer.class.getName())
                    .log(java.util.logging.Level.SEVERE, "Error handling ForwardProfileAnswer", e);
            return Optional.empty();
        }
    }
}
