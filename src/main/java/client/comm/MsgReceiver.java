package client.comm;

import client.comm.messages.Message;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Runnable that continuously reads objects from an ObjectInputStream and
 * dispatches them to the provided handler. It runs on its own thread.
 */
public class MsgReceiver implements Runnable, AutoCloseable {
    private final ObjectInputStream in;
    private final Consumer<Message> handler;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final Runnable onDisconnect;
    private Thread worker;
    private static final Logger LOGGER = Logger.getLogger(MsgReceiver.class.getName());

    public MsgReceiver(ObjectInputStream in, Consumer<Message> handler, Runnable onDisconnect) {
        this.in = Objects.requireNonNull(in);
        this.handler = Objects.requireNonNull(handler);
        this.onDisconnect = onDisconnect;
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
                Object obj = in.readObject();
                if (obj instanceof Message msg) {
                    try {
                        handler.accept(msg);
                    } catch (Throwable t) {
                        LOGGER.log(Level.SEVERE, "MsgReceiver: Exception in handler.", t);
                    }
                }
            }
        } catch (java.io.EOFException | java.net.SocketException e) {
            LOGGER.info("Connexion au serveur interrompue.");
            if (onDisconnect != null) {
                onDisconnect.run();
            }
        } catch (IOException | ClassNotFoundException e) {
            if (running.get()) {
                LOGGER.log(Level.INFO, "MsgReceiver: I/O error or stream closed.", e);
            }
        } finally {
            running.set(false);
        }
    }

    @Override
    public void close() throws IOException {
        try (in) {
            stop();
        }
    }
}