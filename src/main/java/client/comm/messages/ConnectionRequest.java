package client.comm.messages;

import java.util.List;
import java.util.Optional;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;
// RETIRÉ : import server.ServerContext;

public class ConnectionRequest extends Message {
    private static final long serialVersionUID = 1L;

    private final LightUser user;
    private final List<LightKanban> kanbans;

    public ConnectionRequest(LightUser user, List<LightKanban> kanbans) {
        this.user = user;
        this.kanbans = kanbans;
    }

    public LightUser getUser() { return user; }

    @Override
    public Optional<Message> handle() {
        try {
            // --- RÉFLEXION ---
            Class<?> contextClass = Class.forName("server.ServerContext");
            java.lang.reflect.Method getDataMethod = contextClass.getMethod("getData");
            Object dataServerObj = getDataMethod.invoke(null);

            if (dataServerObj != null) {
                server.interfaces.CommCallsDataServer dataServer = (server.interfaces.CommCallsDataServer) dataServerObj;

                // Le serveur enregistre l'utilisateur ET ses kanbans en mémoire
                dataServer.addNewUser(this.user, this.kanbans);

                var users = dataServer.getUsersList();
                var allKanbans = dataServer.getKanbansList();

                return Optional.of(new UpdateUsersAndKanbansListResponse(users, allKanbans));
            }
        } catch (ClassNotFoundException e) {
            // Normal côté client
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return Optional.empty();
    }
}