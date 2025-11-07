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

public class CommCoreClient {
    private final String serverAddress;
    private final int serverPort;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
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
    }

    public void disconnect() throws IOException {
        if (in != null) in.close();
        if (out != null) out.close();
        if (socket != null) socket.close();
    }

    public void sendMessage(Object message) throws IOException {
        out.writeObject(message);
        out.flush();
    }

    public Object receiveMessage() throws IOException, ClassNotFoundException {
        return in.readObject();
    }
}