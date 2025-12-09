package client.comm.messages;

import java.util.List;
import java.util.Optional;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;
import common.dataClasses.User;
// RETIRÉ : import server.ServerContext;

public class ConnectionRequest extends Message {
    private static final long serialVersionUID = 1L;

    private final LightUser user;
    private final List<LightKanban> kanbans;
    // Optional full user details to be cached server-side on connect
    private final User fullUser;

    public ConnectionRequest(LightUser user, List<LightKanban> kanbans) {
        this(user, kanbans, null);
    }

    public ConnectionRequest(LightUser user, List<LightKanban> kanbans, User fullUser) {
        this.user = user;
        this.kanbans = kanbans;
        this.fullUser = fullUser;
    }

    public LightUser getUser() { return user; }
    public User getFullUser() { return fullUser; }

    @Override
    public Optional<Message> handle() {
        try {
            // --- RÉFLEXION ---
            Class<?> contextClass = Class.forName("server.ServerContext");
            java.lang.reflect.Method getDataMethod = contextClass.getMethod("getData");
            Object dataServerObj = getDataMethod.invoke(null);

            if (dataServerObj != null) {
                // Access via interface for standard operations
                server.interfaces.CommCallsDataServer dataServer = (server.interfaces.CommCallsDataServer) dataServerObj;
                dataServer.addNewUser(this.user, this.kanbans);

                // Also cache full user on the server model if provided
                try {
                    server.data.DataServProvider provider = server.ServerContext.getProvider();
                    if (provider != null && this.fullUser != null) {
                        server.data.ServerModel model = provider.getModel();
                        if (model != null) {
                            model.putFullUser(this.fullUser);
                            java.util.logging.Logger.getLogger(ConnectionRequest.class.getName())
                                .info("[SERVER] Cached full user on connect: " + this.fullUser.getUsername()
                                        + " (ID=" + this.fullUser.getId() + ") | cacheSize="
                                        + model.getConnectedUsersFull().size());
                        }
                    } else {
                        java.util.logging.Logger.getLogger(ConnectionRequest.class.getName())
                                .info("[SERVER] No full user provided to cache on connect.");
                    }
                } catch (ClassCastException ignored) {
                    // In some contexts, getData() may be exposed via interface; caching skipped
                }

                var users = dataServer.getUsersList();
                var allKanbans = dataServer.getKanbansList();

                return Optional.of(new UpdateUsersAndKanbansListResponse(users, allKanbans));
            }
        } catch (ClassNotFoundException e) {
            // Normal côté client
        } catch (Throwable t) {
            java.util.logging.Logger.getLogger(ConnectionRequest.class.getName())
                    .log(java.util.logging.Level.SEVERE, "Erreur traitement connection", t);
        }
        return Optional.empty();
    }
}