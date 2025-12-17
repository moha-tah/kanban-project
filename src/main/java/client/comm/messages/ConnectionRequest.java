package client.comm.messages;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;
import common.dataClasses.User;
// RETIRÉ : import server.ServerContext;

public class ConnectionRequest extends Message {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(ConnectionRequest.class.getName());

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
            // Access the server context directly via the static getter for the provider
            server.data.DataServProvider provider = server.ServerContext.getProvider();
            if (provider == null) {
                LOGGER.info("[SERVER] DataServProvider not available yet (normal on client side)");
                return Optional.empty();
            }

            server.interfaces.CommCallsDataServer dataServer = provider.getDataCallsComServ();
            if (dataServer == null) {
                LOGGER.info("[SERVER] CommCallsDataServer not available");
                return Optional.empty();
            }
            
            dataServer.addNewUser(this.user, this.kanbans);

            // Also cache full user on the server model if provided
            try {
                server.data.ServerModel model = provider.getModel();
                if (model != null && this.fullUser != null) {
                    model.putFullUser(this.fullUser);
                    LOGGER.log(Level.INFO, "[SERVER] Cached full user on connect: {0} (ID={1}) | cacheSize={2}", new Object[]{this.fullUser.getUsername(), this.fullUser.getId(), model.getConnectedUsersFull().size()});
                }
            } catch (Exception ignored) {
                // Caching skipped if model unavailable
            }

            var users = dataServer.getUsersList();
            var allKanbans = dataServer.getKanbansList();

            return Optional.of(new UpdateUsersAndKanbansListResponse(users, allKanbans));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error during connection handling", e);
        }
        return Optional.empty();
    }
}