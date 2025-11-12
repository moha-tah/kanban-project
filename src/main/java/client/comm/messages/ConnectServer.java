package client.comm.messages;

import java.util.Optional;

/**
 * Client -> Server: initial connection trigger. No payload, no direct reply.
 */
public class ConnectServer extends Message {
    private static final long serialVersionUID = 1L;

    @Override
    public Optional<Message> handle() throws Exception {
        // Handling is performed by the server-side dispatcher.
        return Optional.empty();
    }
}
