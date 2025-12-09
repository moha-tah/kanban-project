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
            var data = server.ServerContext.getData();
            if (data == null) {
                java.util.logging.Logger.getLogger(DistProfileRequest.class.getName())
                        .severe("Server data interface is null");
                return Optional.empty();
            }
            // First, try the server-side full user cache for uniform behavior
            common.dataClasses.User full = null;
            try {
                server.data.DataServProvider provider = server.ServerContext.getProvider();
                if (provider != null) {
                    server.data.ServerModel srvModel = provider.getModel();
                    if (srvModel != null && srvModel.getConnectedUsersFull() != null) {
                        full = srvModel.getConnectedUsersFull().get(requestedUserId);
                    }
                }
                java.util.logging.Logger.getLogger(DistProfileRequest.class.getName())
                    .info("[SERVER] DistProfileRequest cache lookup: "
                            + (full != null ? "HIT" : "MISS")
                            + " for userId=" + requestedUserId);
            } catch (Throwable ignored) {}

            if (full == null) {
                // Fallback: find LightUser, then enrich from JSON files
                var users = data.getUsersList();
                common.dataClasses.LightUser found = null;
                if (users != null) {
                    for (common.dataClasses.LightUser u : users) {
                        if (u.getId().equals(requestedUserId)) { found = u; break; }
                    }
                }
                if (found != null) {
                    String rawUserJson = readUserJson(found.getId());
                    full = buildUserFromJson(found.getId(), found.getUsername(), rawUserJson);
                    java.util.List<common.dataClasses.Kanban> created = loadUserKanbans(rawUserJson);
                    if (full != null && created != null) {
                        full.setMyKanban(created);
                    }
                    java.util.logging.Logger.getLogger(DistProfileRequest.class.getName())
                        .info("[SERVER] JSON fallback used for user=" + (found != null ? found.getUsername() : "<null>")
                                + ", kanbans=" + (full != null && full.getMyKanban() != null ? full.getMyKanban().size() : 0));
                    if (full == null) {
                        String uname = found.getUsername();
                        full = new common.dataClasses.User(found.getId(), uname, uname, "", null);
                    }
                } else {
                    java.util.logging.Logger.getLogger(DistProfileRequest.class.getName())
                        .warning("[SERVER] Requested user not found in connected list: " + requestedUserId);
                }
            } else {
                // If full user came from cache but has no kanbans populated, enrich from JSON
                try {
                    if (full.getMyKanban() == null || full.getMyKanban().isEmpty()) {
                        String rawUserJson = readUserJson(full.getId());
                        java.util.List<common.dataClasses.Kanban> created = loadUserKanbans(rawUserJson);
                        if (created != null && !created.isEmpty()) {
                            full.setMyKanban(created);
                            java.util.logging.Logger.getLogger(DistProfileRequest.class.getName())
                                .info("[SERVER] Cache enrichment: added " + created.size() + " kanbans for user=" + full.getUsername());
                        }
                    }
                } catch (Throwable ignored) {}
            }
            // Build answer (may be null if not found)
            java.util.logging.Logger.getLogger(DistProfileRequest.class.getName())
                .info("[SERVER] Forwarding profile answer to requester=" + requesterId
                        + ", user=" + (full != null ? full.getUsername() : "<null>")
                        + ", kanbans=" + (full != null && full.getMyKanban() != null ? full.getMyKanban().size() : 0));
            return Optional.of(new ForwardProfileAnswer(requesterId, full));
        } catch (Exception e) {
            LOGGER.log(java.util.logging.Level.SEVERE, "Error handling DistProfileRequest", e);
            return Optional.empty();
        }
    }
    
}