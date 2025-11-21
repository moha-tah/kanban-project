package client.comm.messages;

import java.util.List;
import java.util.Optional;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;

public class ConnectionRequest extends Message {
    private static final long serialVersionUID = 1L;

    private final LightUser user;
    private final List<LightKanban> kanbans;

    public ConnectionRequest(LightUser user, List<LightKanban> kanbans) {
        this.user = user;
        this.kanbans = kanbans;
    }

    @Override
    public Optional<Message> handle() {
        try {
            // On appelle la méthode addNewUser de l'interface Serveur
            if (this.getServerContext().getData() != null) {

                this.getServerContext().getData().addNewUser(this.user, this.kanbans);
                var users = this.getServerContext().getData().getUsersList();
                var allKanbans = this.getServerContext().getData().getKanbansList();

                return Optional.of(new UpdateUsersAndKanbansListResponse(users, allKanbans));
            }    
        } catch (Throwable t) {
            // Log error on server side
            java.util.logging.Logger.getLogger(ConnectionRequest.class.getName())
                    .log(java.util.logging.Level.SEVERE, "Error handling connection request", t);
        }
        
        return Optional.empty();
    }
}