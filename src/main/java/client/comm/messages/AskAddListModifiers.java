package client.comm.messages;

import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;

import java.util.Optional;
import java.util.UUID;


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
            // LOGIQUE SERVEUR
            var dataServer = this.getServerContext().getData();
            
            if (dataServer != null) {
                // TO DO
            }
        } catch (Throwable t) {
            // Ignoré sur le client
        }
        return Optional.empty();
    }
}