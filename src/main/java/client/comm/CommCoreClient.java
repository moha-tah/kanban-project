package client.comm;

import java.net.Socket;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;

import client.interfaces.*;
import client.comm.imp.DataCallsCommImp;
import client.comm.imp.IhmKanbanCallsCommImp;
import client.comm.imp.IhmMainCallsCommImp;
import client.comm.messages.Message;
import server.interfaces.CommCallsDataServer;

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

    private CommClientCallsMain mainInterface;
    private ComCallsDataClient dataInterface;


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

    public void setMainInterface(CommClientCallsMain mainInterface) {
        this.mainInterface = mainInterface;
    }

    public void setDataInterface(ComCallsDataClient dataInterface) {
        this.dataInterface = dataInterface;
    }

    public CommClientCallsMain getMainInterface() { return mainInterface; }
    public ComCallsDataClient getDataInterface() { return dataInterface; }


    public void connect() throws IOException {
        socket = new Socket(serverAddress, serverPort);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        // initialize message helpers
        this.msgSender = new MsgSender(out);

        this.msgReceiver = new MsgReceiver(in, obj -> {
            if (obj instanceof Message msg) {
                try {
                    Optional<Message> response = msg.handle();
                    
                    if (response.isPresent()) {
                        sendMessage(response.get());
                    }
                } catch (Exception e) {
                    // Convert checked exceptions to unchecked so the receiver can handle them,
                    // or add proper logging/handling here as needed.
                    throw new RuntimeException(e);
                }
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
    
    public void sendMessage(Object message) throws IOException {
        if (msgSender == null) throw new IOException("Not connected or MsgSender not initialized");
        msgSender.send(message);
    }

   
    public MsgReceiver getMsgReceiver() {
        return msgReceiver;
    }

   
    public MsgSender getMsgSender() {
        return msgSender;
    }
}