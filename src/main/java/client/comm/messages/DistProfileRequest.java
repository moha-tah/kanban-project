package client.comm.messages;

import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Client -> Server: request distant user's profile by UUIDs.
 */
public class DistProfileRequest extends Message {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(DistProfileRequest.class.getName());

    private final UUID requesterId;
    private final UUID requestedUserId;

    public DistProfileRequest(UUID requesterId, UUID requestedUserId) {
        this.requesterId = requesterId;
        this.requestedUserId = requestedUserId;
    }

    public UUID requesterId() {
        return requesterId;
    }

    public UUID requestedUserId() {
        return requestedUserId;
    }

    @Override
    public Optional<Message> handle() {
        // Executed on SERVER side
        try {
            // New behavior: do NOT read or store full profiles on server.
            // Forward the request to the concerned user so their client can reply.
            LOGGER.info(String.format("[SERVER] Forwarding FetchDistantProfile to target=%s for requester=%s",
                    requestedUserId, requesterId));
            boolean sent = server.comm.CommCoreServer.sendToUser(
                    requestedUserId,
                    new FetchDistantProfile(requesterId, requestedUserId));

            if (!sent) {
                LOGGER.warning(() -> "[SERVER] Target user not connected; cannot fetch distant profile: "
                        + requestedUserId);
            }
            // No immediate response from server; the target client will send DistProfileAnswer
            // which the server will relay to the requester.
            return Optional.empty();
        } catch (Exception e) {
            LOGGER.log(java.util.logging.Level.SEVERE, "Error handling DistProfileRequest", e);
            return Optional.empty();
        }
    }
    
}
