package client.comm.messages;

import java.util.Optional;
import java.util.UUID;

import common.dataClasses.User;

/**
 * Server -> AppOwner Client: ask to fetch full profile for requested user UUID,
 * then reply back to server using DistProfileAnswer.
 */
public class FetchDistantProfile extends Message {
    private static final long serialVersionUID = 1L;

    private final UUID requesterId;
    private final UUID requestedUserId;

    public FetchDistantProfile(UUID requesterId, UUID requestedUserId) {
        this.requesterId = requesterId;
        this.requestedUserId = requestedUserId;
    }

    public UUID requesterId() { return requesterId; }
    public UUID requestedUserId() { return requestedUserId; }

    @Override
    public Optional<Message> handle() {
        // Executed on CLIENT (AppOwner) side: build full User and respond to server
        try {
            var dataCli = this.getClientContext() != null ? this.getClientContext().getData() : null;
            User full = null;
            if (dataCli != null) {
                // Let the local data layer assemble the full profile of the local user
                full = dataCli.getDistantProfile();
            }
            // Respond to server with DistProfileAnswer including the full user
            return Optional.of(new DistProfileAnswer(requesterId, full));
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(FetchDistantProfile.class.getName())
                .log(java.util.logging.Level.SEVERE, "Error handling FetchDistantProfile", e);
            return Optional.empty();
        }
    }
}
