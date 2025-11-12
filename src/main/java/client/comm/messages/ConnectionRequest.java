package client.comm.messages;

import java.util.Optional;
import java.util.UUID;
import common.dataClasses.LightUser;

/**
 * Client -> Server: request to register/connect a user.
 * Server will add the user if new and respond with users and kanbans list.
 */
public class ConnectionRequest extends Message {
    private static final long serialVersionUID = 1L;

    private final LightUser user;
    private final boolean returningClient;

    public ConnectionRequest(LightUser user, boolean returningClient) {
        this.user = user;
        this.returningClient = returningClient;
    }

    public LightUser getUser() {
        return user;
    }

    public boolean isReturningClient() {
        return returningClient;
    }

    @Override
    public Optional<Message> handle() throws Exception {
        // Server-side logic should add new user if needed and reply with lists.
        return Optional.empty();
    }
}
