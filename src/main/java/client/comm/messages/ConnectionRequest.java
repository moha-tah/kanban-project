package client.comm.messages;

import java.util.List;
import java.util.Optional;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;
import server.ServerContext; // Assurez-vous d'avoir cet import

public class ConnectionRequest extends Message {
    private static final long serialVersionUID = 1L;

    private final LightUser user;
    private final List<LightKanban> kanbans;

    public ConnectionRequest(LightUser user, List<LightKanban> kanbans) {
        this.user = user;
        this.kanbans = kanbans;
    }

    public LightUser getUser() {
        return user;
    }

    @Override
    public Optional<Message> handle() {
        try {
            // On stocke l'interface dans une variable pour alléger le code
            var dataServer = ServerContext.getData();

            if (dataServer != null) {
                dataServer.addNewUser(this.user, this.kanbans);

                var users = dataServer.getUsersList();
                var allKanbans = dataServer.getKanbansList();

                return Optional.of(new UpdateUsersAndKanbansListResponse(users, allKanbans));
            }
        } catch (Throwable t) {
            java.util.logging.Logger.getLogger(ConnectionRequest.class.getName())
                    .log(java.util.logging.Level.SEVERE, "Error handling connection request", t);
        }

        return Optional.empty();
    }
}