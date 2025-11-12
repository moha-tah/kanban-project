package client.src.comm;

import java.net.Socket;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import client.src.comm.imp.IhmMainCallsCommImp;
import client.src.comm.imp.DataCallsCommImp;
import client.src.comm.imp.IhmKanbanCallsCommImp;
import client.src.interfaces.IhmMainCallsComm;
import client.src.interfaces.DataCallsComm;
import client.src.interfaces.IhmKanbanCallsComm;
import client.src.comm.messages.Message;
import java.util.Optional;

public class CommCoreClient {
    private final String serverAddress;
    private final int serverPort;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private MsgSender msgSender;
    private MsgReceiver msgReceiver;
    private final IhmMainCallsComm ihmMainCallsComm;
    private final DataCallsComm dataCallsComm;
    private final IhmKanbanCallsComm ihmKanbanCallsComm;

    public CommCoreClient(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.ihmMainCallsComm = new IhmMainCallsCommImp(this);
        this.dataCallsComm = new DataCallsCommImp(this);
        this.ihmKanbanCallsComm = new IhmKanbanCallsCommImp(this);
    }

    // Getters
    public String getServerAddress() {
        return serverAddress;
    }

    public int getServerPort() {
        return serverPort;
    }

    public Socket getSocket() {
        return socket;
    }

    public ObjectOutputStream getOut() {
        return out;
    }

    public ObjectInputStream getIn() {
        return in;
    }

    public IhmMainCallsComm getIhmMainCallsComm() {
        return ihmMainCallsComm;
    }

    public DataCallsComm getDataCallsComm() {
        return dataCallsComm;
    }

    public IhmKanbanCallsComm getIhmKanbanCallsComm() {
        return ihmKanbanCallsComm;
    }

    public void connect() throws IOException {
        socket = new Socket(serverAddress, serverPort);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        // initialize message helpers
        this.msgSender = new MsgSender(out);
        // receiver: dispatch to message.handle() and send any optional response
        this.msgReceiver = new MsgReceiver(in, obj -> {
            if (obj instanceof Message) {
                try {
                    Optional<Message> resp = ((Message) obj).handle();
                    if (resp != null && resp.isPresent()) {
                        try {
                            sendMessage(resp.get());
                        } catch (IOException e) {
                            // sending failed; log and continue
                            e.printStackTrace();
                        }
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            } else {
                // non-message objects are ignored
            }
        });
        this.msgReceiver.start();
    }

    public void disconnect() throws IOException {
        if (in != null) in.close();
        if (out != null) out.close();
        if (socket != null) socket.close();
        if (msgReceiver != null) msgReceiver.stop();
        if (msgSender != null) msgSender.close();
    }
    /**
     * Synchronously send a message using the MsgSender helper.
     */
    public void sendMessage(Object message) throws IOException {
        if (msgSender == null) throw new IOException("Not connected or MsgSender not initialized");
        msgSender.send(message);
    }

    // Deprecated: MessageDispatcher removed; use sendMessage(...) directly when needed.

    /**
     * Convenience: get MsgReceiver to attach a handler and start it.
     */
    public MsgReceiver getMsgReceiver() {
        return msgReceiver;
    }

    /**
     * Convenience: get MsgSender to send messages directly.
     */
    public MsgSender getMsgSender() {
        return msgSender;
    }
}