package client.comm.messages;

import java.io.Serializable;
import java.util.Optional;

/**
 * Base class for all messages exchanged between client and server.
 * Each concrete message should override {@link #handle} to process itself
 * and optionally produce a response message. Returning an empty Optional
 * means no reply is sent.
 */
public abstract class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Handle this message and optionally return a response message to send back.
     * @return Optional containing a response Message to send, or empty if no reply
     * @throws Exception if handling fails
     */
    public abstract Optional<Message> handle() throws Exception;
}
