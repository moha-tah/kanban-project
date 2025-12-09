package client.comm.messages;

import java.util.Optional;
import java.util.UUID;

import common.dataClasses.LightUser;
import common.dataClasses.User;

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
            var serverCtx = this.getServerContext();
            if (serverCtx == null || serverCtx.getData() == null) {
                java.util.logging.Logger.getLogger(DistProfileRequest.class.getName())
                        .severe("Server context or data interface is null");
                return Optional.empty();
            }

            var data = serverCtx.getData();
            var users = data.getUsersList();
            LightUser found = null;
            if (users != null) {
                for (LightUser u : users) {
                    if (u.getId().equals(requestedUserId)) { found = u; break; }
                }
            }

            // Build a minimal User if found (data layer currently stores LightUser)
            User full = null;
            if (found != null) {
                full = new User(found.getId(), found.getUsername(), null, null, null);
            }

            // Reply (even null user is forwarded so client can handle "not found")
            return Optional.of(new ForwardProfileAnswer(requesterId, full));
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(DistProfileRequest.class.getName())
                    .log(java.util.logging.Level.SEVERE, "Error handling DistProfileRequest", e);
            return Optional.empty();
        }
    }
}
