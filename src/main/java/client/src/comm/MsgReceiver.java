package client.src.comm;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * Runnable that continuously reads objects from an ObjectInputStream and
 * dispatches them to the provided handler. It runs on its own thread.
 */
public class MsgReceiver implements Runnable, AutoCloseable {
    private final ObjectInputStream in;
    private final Consumer<Object> handler;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private Thread worker;

    public MsgReceiver(ObjectInputStream in, Consumer<Object> handler) {
        this.in = Objects.requireNonNull(in);
        this.handler = Objects.requireNonNull(handler);
    }

    /** Start the receiver loop on a dedicated thread. */
    public void start() {
        if (running.compareAndSet(false, true)) {
            worker = new Thread(this, "MsgReceiver-thread");
            worker.setDaemon(true);
            worker.start();
        }
    }

    /** Stop the receiver loop and wait for thread to finish. */
    public void stop() {
        running.set(false);
        if (worker != null && Thread.currentThread() != worker) {
            try {
                worker.join(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public void run() {
        try {
            while (running.get()) {
                Object msg = in.readObject();
                try {
                    handler.accept(msg);
                } catch (Throwable t) {
                    // Handler exception should not kill the receiver loop
                    t.printStackTrace();
                }
            }
        } catch (IOException e) {
            // Stream closed or network issue; stop running
            // Optionally log or notify via handler (not implemented)
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            running.set(false);
        }
    }

    @Override
    public void close() throws IOException {
        stop();
        in.close();
    }
}
