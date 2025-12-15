package client.comm.messages;

import java.util.Optional;
import java.util.UUID;

import common.dataClasses.User;

/**
 * AppOwner Client -> Server: answer with the full requested user profile,
 * which will be forwarded to the requester.
 */
public class DistProfileAnswer extends Message {
    private static final long serialVersionUID = 1L;

    private final UUID requesterId;
    private final User requestedUser;

    public DistProfileAnswer(UUID requesterId, User requestedUser) {
        this.requesterId = requesterId;
        this.requestedUser = requestedUser;
    }

    public UUID requesterId() { return requesterId; }
    public User requestedUser() { return requestedUser; }

    @Override
    public Optional<Message> handle() {
        // Executed on SERVER side: forward to the requester client
        try {
            server.comm.CommCoreServer.sendToUser(requesterId, new ForwardProfileAnswer(requesterId, requestedUser));
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(DistProfileAnswer.class.getName())
                .log(java.util.logging.Level.SEVERE, "Error handling DistProfileAnswer", e);
        }
        return Optional.empty();
    }
}