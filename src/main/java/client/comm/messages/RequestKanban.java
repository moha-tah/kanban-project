package client.comm.messages;
import java.util.logging.Logger;
import java.util.logging.Level;

import common.dataClasses.Kanban;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;
import java.util.Optional;

public class RequestKanban extends Message {

    private static final Logger LOGGER = Logger.getLogger(RequestKanban.class.getName());
    private final LightKanban lightKanbanId;
    private final LightUser lightUserId;

    public RequestKanban(LightKanban kanbanId, LightUser userId) {
        this.lightKanbanId = kanbanId;
        this.lightUserId = userId;
    }
    
    @Override
    public Optional<Message> handle() {        
        try {
            var serverCtx = this.getServerContext();
            if (serverCtx == null) {
                return Optional.empty();
            }
            var dataServer = serverCtx.getData();

            if (dataServer != null) {
                LOGGER.log(Level.FINE, "dataServer found, requesting Kanban...");

                Kanban fullKanban = dataServer.requestKanban(lightUserId, lightKanbanId);

                if (fullKanban != null) {
                    LOGGER.log(Level.FINE, "Full Kanban received from server: {0}", fullKanban.getTitle());
                    LOGGER.log(Level.FINE, "Sending SendKanban response to client");
                    return Optional.of(new SendKanban(fullKanban));
                } else {
                    LOGGER.log(Level.WARNING, "dataServer.requestKanban returned null");
                }
            } else {
                LOGGER.log(Level.WARNING, "dataServer is null");
            }
        } catch (NoClassDefFoundError | Exception e) {
            LOGGER.log(Level.SEVERE, "Exception in handle: {0}", e.getMessage());
        }
        
        LOGGER.log(Level.FINE, "Returning empty Optional");
        return Optional.empty();
    }

}
