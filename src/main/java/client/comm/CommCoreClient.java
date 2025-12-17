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

/**
 * Cœur de la couche de communication côté client.
 * 
 * Cette classe gère la connexion au serveur, l'envoi et la réception de messages,
 * et fournit les interfaces de communication vers les différentes couches
 * (IHM Main, IHM Kanban, Data). Elle initialise et coordonne les composants
 * nécessaires à la communication réseau.
 * 
 * @author Équipe Kanban
 * @version 1.0
 * @since 1.0
 * @see MsgSender
 * @see MsgReceiver
 * @see ClientContext
 */
public class CommCoreClient {
    /**
     * Adresse du serveur auquel se connecter.
     */
    private String serverAddress;
    
    /**
     * Port du serveur auquel se connecter.
     */
    private int serverPort;
    
    /**
     * Socket de connexion au serveur.
     */
    private Socket socket;
    
    /**
     * Flux de sortie pour envoyer des objets au serveur.
     */
    private ObjectOutputStream out;
    
    /**
     * Flux d'entrée pour recevoir des objets du serveur.
     */
    private ObjectInputStream in;
    
    /**
     * Gestionnaire d'envoi de messages.
     */
    private MsgSender msgSender;
    
    /**
     * Gestionnaire de réception de messages.
     */
    private MsgReceiver msgReceiver;
    
    /**
     * Interface de communication vers l'IHM principale.
     */
    private final IhmMainCallsComm ihmMainCallsComm;
    
    /**
     * Interface de communication vers la couche données.
     */
    private final DataCallsComm dataCallsComm;
    
    /**
     * Interface de communication vers l'IHM Kanban.
     */
    private final IhmKanbanCallsComm ihmKanbanCallsComm;
    
    /**
     * Contexte client contenant les interfaces vers les autres couches.
     */
    private final ClientContext clientContext;

    /**
     * Constructeur du cœur de communication client.
     * 
     * Initialise toutes les interfaces de communication et le contexte client.
     */
    public CommCoreClient() {
        this.ihmMainCallsComm = new IhmMainCallsCommImp(this);
        this.dataCallsComm = new DataCallsCommImp(this);
        this.ihmKanbanCallsComm = new IhmKanbanCallsCommImp(this);
        this.clientContext = new ClientContext();
    }

    /**
     * Récupère l'adresse du serveur.
     * 
     * @return L'adresse du serveur, ou null si non connecté
     */
    public String getServerAddress() {
        return serverAddress;
    }

    /**
     * Récupère le port du serveur.
     * 
     * @return Le port du serveur, ou 0 si non connecté
     */
    public int getServerPort() {
        return serverPort;
    }

    /**
     * Récupère le socket de connexion.
     * 
     * @return Le socket de connexion, ou null si non connecté
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * Récupère le flux de sortie.
     * 
     * @return Le flux de sortie, ou null si non connecté
     */
    public ObjectOutputStream getOut() {
        return out;
    }

    /**
     * Récupère le flux d'entrée.
     * 
     * @return Le flux d'entrée, ou null si non connecté
     */
    public ObjectInputStream getIn() {
        return in;
    }

    /**
     * Récupère l'interface de communication vers l'IHM principale.
     * 
     * @return L'interface de communication vers l'IHM principale
     */
    public IhmMainCallsComm getIhmMainCallsComm() {
        return ihmMainCallsComm;
    }

    /**
     * Récupère l'interface de communication vers la couche données.
     * 
     * @return L'interface de communication vers la couche données
     */
    public DataCallsComm getDataCallsComm() {
        return dataCallsComm;
    }

    /**
     * Récupère l'interface de communication vers l'IHM Kanban.
     * 
     * @return L'interface de communication vers l'IHM Kanban
     */
    public IhmKanbanCallsComm getIhmKanbanCallsComm() {
        return ihmKanbanCallsComm;
    }

    /**
     * Définit l'interface de données dans le contexte client.
     * 
     * @param dataInterface L'interface de données à définir (ne doit pas être null)
     */
    public void setDataInterface(ComCallsDataClient dataInterface) {
        this.clientContext.setDataInterface(dataInterface);
    }

    /**
     * Définit l'interface Kanban dans le contexte client.
     * 
     * @param kanbanInterface L'interface Kanban à définir (ne doit pas être null)
     */
    public void setIhmKanbanInterface(CommClientCallsKanban kanbanInterface) {
        this.clientContext.setKanbanInterface(kanbanInterface);
    }

    /**
     * Définit l'interface principale dans le contexte client.
     * 
     * @param mainInterface L'interface principale à définir (ne doit pas être null)
     */
    public void setIhmMainInterface(CommClientCallsMain mainInterface) {
        this.clientContext.setMainInterface(mainInterface);
    }

    /**
     * Établit une connexion au serveur avec l'adresse et le port spécifiés.
     * 
     * Si une connexion existe déjà au même serveur, elle est réutilisée.
     * Sinon, l'ancienne connexion est fermée et une nouvelle est établie.
     * 
     * @param host L'adresse du serveur (ex: "localhost" ou une adresse IP)
     * @param port Le port sur lequel se connecter
     * @return true si la connexion a réussi, false sinon
     */
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
    /**
     * Établit la connexion au serveur en utilisant l'adresse et le port
     * stockés dans les champs de la classe.
     * 
     * Cette méthode crée le socket, initialise les flux d'entrée/sortie,
     * et démarre le récepteur de messages dans un thread séparé.
     * 
     * @throws IOException si la connexion échoue ou si les flux ne peuvent pas être créés
     */
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

    /**
     * Ferme proprement la connexion au serveur.
     * 
     * Cette méthode arrête le récepteur de messages, ferme le socket
     * et tous les flux associés, puis nettoie les références.
     */
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
    
    /**
     * Envoie un message au serveur.
     * 
     * @param message Le message à envoyer (doit être sérialisable)
     * @throws IOException si l'envoi échoue ou si la connexion n'est pas établie
     */
    public void sendMessage(Object message) throws IOException {
        if (msgSender == null) throw new IOException("Not connected or MsgSender not initialized");
        msgSender.send(message);
    }

    /**
     * Récupère le récepteur de messages.
     * 
     * @return Le récepteur de messages, ou null si non connecté
     */
    public MsgReceiver getMsgReceiver() {
        return msgReceiver;
    }

    /**
     * Récupère l'expéditeur de messages.
     * 
     * @return L'expéditeur de messages, ou null si non connecté
     */
    public MsgSender getMsgSender() {
        return msgSender;
    }


}