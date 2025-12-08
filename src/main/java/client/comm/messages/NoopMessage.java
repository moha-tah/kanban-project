package client.comm.messages;

import java.util.Optional;

/**
 * A no-op message used to satisfy reply requirements; it does nothing.
 */
public class NoopMessage extends Message {
    private static final long serialVersionUID = 1L;

    @Override
    public Optional<Message> handle() throws Exception {
        return Optional.empty();
    }
}
