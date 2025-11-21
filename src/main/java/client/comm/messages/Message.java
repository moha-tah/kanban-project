package client.comm.messages;

import java.io.Serializable;
import java.util.Optional;

import client.ClientContext;
import server.ServerContext;

/**
 * Base class for all messages exchanged between client and server.
 * Each concrete message should override {@link #handle} to process itself
 * and optionally produce a response message. Returning an empty Optional
 * means no reply is sent.
 */
public abstract class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    // Transient runtime context that will NOT be serialized with the message.
    // The comm layer must set this on the receiver side before calling `handle()`.
    private transient ClientContext clientContext;
    private transient ServerContext serverContext;
    /**
     * Handle this message and optionally return a response message to send back.
     * @return Optional containing a response Message to send, or empty if no reply
     * @throws Exception if handling fails
     */
    public abstract Optional<Message> handle() throws Exception;

    /**
     * Set a runtime-only context object. This field is transient and won't be
     * serialized when the message is sent over the wire.
     */

    public void setClientContext(ClientContext context) {
        this.clientContext = context;
    }

    public void setServerContext(ServerContext context) {
        this.serverContext = context;
    }
    /**
     * Get the runtime-only context object (may be null).
     */

    public ServerContext getServerContext() {
        return this.serverContext;
    }

    public ClientContext getClientContext() {
        return this.clientContext;
    }   

}
