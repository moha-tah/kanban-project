package client.comm;

import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.Objects;

/**
 * Helper responsible for sending messages safely over an ObjectOutputStream.
 * It centralizes synchronization, flushing and basic error handling.
 */
public class MsgSender implements AutoCloseable {
    private final ObjectOutputStream out;

    public MsgSender(ObjectOutputStream out) {
        this.out = Objects.requireNonNull(out);
    }

    /**
     * Send a serializable message synchronously. Thread-safe.
     * @param message the message to send
     * @throws IOException when underlying stream fails
     */
    public synchronized void send(Object message) throws IOException {
        out.writeObject(message);
        out.flush();
    }

    @Override
    public void close() throws IOException {
        out.close();
    }
}
