package client.comm.messages;

import java.util.Optional;
import java.util.UUID;

/**
 * Client -> Server: request distant user's profile by UUIDs.
 */
public class DistProfileRequest extends Message {
    private static final long serialVersionUID = 1L;

    private final UUID requesterId;
    private final UUID requestedUserId;

    public DistProfileRequest(UUID requesterId, UUID requestedUserId) {
        this.requesterId = requesterId;
        this.requestedUserId = requestedUserId;
    }

    public UUID requesterId() { return requesterId; }
    public UUID requestedUserId() { return requestedUserId; }

    @Override
    public Optional<Message> handle() {
        // Executed on SERVER side
        try {
            var data = server.ServerContext.getData();
            if (data == null) {
                java.util.logging.Logger.getLogger(DistProfileRequest.class.getName())
                        .severe("Server data interface is null");
                return Optional.empty();
            }
            // Find the user in connected users or any known list
            var users = data.getUsersList();
            common.dataClasses.LightUser found = null;
            if (users != null) {
                for (common.dataClasses.LightUser u : users) {
                    if (u.getId().equals(requestedUserId)) { found = u; break; }
                }
            }
            // Build a minimal full User if found (server currently retains LightUser info)
            common.dataClasses.User full = null;
            if (found != null) {
                full = new common.dataClasses.User(found.getId(), found.getUsername(), null, null, null);
            }
            // Build answer (may be null if not found)
            return Optional.of(new ForwardProfileAnswer(requesterId, full));
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(DistProfileRequest.class.getName())
                    .log(java.util.logging.Level.SEVERE, "Error handling DistProfileRequest", e);
            return Optional.empty();
        }
    }
}
