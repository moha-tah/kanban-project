package client.comm.messages;

import java.util.Optional;

import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;
import server.interfaces.CommCallsDataServer;


public class AskAddListModifiers extends Message {
    private static final long serialVersionUID = 1L;

    private final LightUser userToAdd;
    private final LightKanban targetKanban;

    public AskAddListModifiers(LightUser userToAdd, LightKanban targetKanban) {
        this.userToAdd = userToAdd;
        this.targetKanban = targetKanban;
    }

    @Override
    public Optional<Message> handle() {
        try {
            CommCallsDataServer dataServer = server.ServerContext.getData();
            
            if (dataServer != null) {
                // Ajoute l'utilisateur à la liste des modificateurs du kanban côté serveur
                dataServer.askAddListModifiers(userToAdd, targetKanban);
            }
        } catch (Throwable t) {
            // Ignoré sur le client
        }
        return Optional.empty();
    }
}