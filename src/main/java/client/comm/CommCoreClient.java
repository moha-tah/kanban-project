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


import java.util.Optional;

import client.ClientContext;



public class CommCoreClient {
    private String serverAddress;
    private int serverPort;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private MsgSender msgSender;
    private MsgReceiver msgReceiver;
    private final IhmMainCallsComm ihmMainCallsComm;
    private final DataCallsComm dataCallsComm;
    private final IhmKanbanCallsComm ihmKanbanCallsComm;
    private final ClientContext clientContext;


    public CommCoreClient() {
        this.ihmMainCallsComm = new IhmMainCallsCommImp(this);
        this.dataCallsComm = new DataCallsCommImp(this);
        this.ihmKanbanCallsComm = new IhmKanbanCallsCommImp(this);
        this.clientContext = new ClientContext();
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

    public void setDataInterface(ComCallsDataClient dataInterface) {
        this.clientContext.setDataInterface(dataInterface);
    }

    public void setIhmKanbanInterface(CommClientCallsKanban kanbanInterface) {
        this.clientContext.setKanbanInterface(kanbanInterface);
    }

    public void setIhmMainInterface(CommClientCallsMain mainInterface) {
        this.clientContext.setMainInterface(mainInterface);
    }

    public boolean connect_host_port(String host, int port) {
        try {
            // Vérifier si on est déjà connecté au même host/port
            if (socket != null && socket.isConnected() && !socket.isClosed() 
                && this.serverAddress != null && this.serverAddress.equals(host) && this.serverPort == port) {
                // Déjà connecté au bon serveur, pas besoin de reconnecter
                return true;
            }
            
            disconnect(); // Tenter de déconnecter proprement l'ancienne connexion (si elle existe)
            this.serverAddress = host; // récupérer dynamiquement les host et port
            this.serverPort = port;
            connect(); // Appelle la méthode connect() sans argument qui utilise maintenant les champs mis à jour
            return true;
        } catch (IOException e) {
            System.err.println("Connection failed to " + host + ":" + port + ": " + e.getMessage());
            return false;
        }
    }
    public void connect() throws IOException {
        socket = new Socket(serverAddress, serverPort);
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());

        this.msgSender = new MsgSender(out);

        this.msgReceiver = new MsgReceiver(in, msg -> {
            try {
                if (this.clientContext != null) {
                    msg.setClientContext(this.clientContext);
                }

                Optional<Message> response = msg.handle();

                if (response.isPresent()) {
                    sendMessage(response.get());
                }
            } catch (Exception e) {
                java.util.logging.Logger.getLogger(CommCoreClient.class.getName())
                        .log(java.util.logging.Level.SEVERE, "MsgReceiver: Exception in handler.", e);
            }
        }, () -> {
            if (this.clientContext != null && this.clientContext.getMainComm() != null) {
                this.clientContext.getMainComm().handleServerConnectionLost();
            }
        });

        this.msgReceiver.start();
    }

    public void disconnect() {
        try {
            if (msgReceiver != null) msgReceiver.stop();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            java.util.logging.Logger.getLogger(CommCoreClient.class.getName())
                    .log(java.util.logging.Level.WARNING, "Error while disconnecting", e);
        } finally {
            if (in != null) try { in.close(); } catch (IOException ignored) {}
            if (out != null) try { out.close(); } catch (IOException ignored) {}
            socket = null;
            in = null;
            out = null;
            msgReceiver = null;
            msgSender = null;
        }
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