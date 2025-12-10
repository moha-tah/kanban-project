package client.comm.messages;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;

/**
 * Message sent from IHM-Kanban to Client Communication to close a Kanban visualization.
 * This message is forwarded to the server.
 */
public class CloseKanban extends Message {
    private static final Logger LOGGER = Logger.getLogger(CloseKanban.class.getName());
    private static final long serialVersionUID = 1L;

    private final LightKanban lightKanban;
    private final LightUser lightUser;

    public CloseKanban(LightKanban lightKanban, LightUser lightUser) {
        this.lightKanban = lightKanban;
        this.lightUser = lightUser;
    }

    @Override
    public Optional<Message> handle() {
        LOGGER.log(Level.INFO, "Handling CloseKanban for Kanban ID: {0}, User ID: {1}", 
                   new Object[]{lightKanban.getId(), lightUser.getId()});
        
        try {
            var dataServer = server.ServerContext.getData();
            
            if (dataServer != null) {
                LOGGER.log(Level.FINE, "Calling dataServer.closeKanban()");
                dataServer.closeKanban(lightKanban, lightUser);
                LOGGER.log(Level.FINE, "Kanban closed successfully on server");
            } else {
                LOGGER.log(Level.WARNING, "dataServer is null, cannot close kanban");
            }
        } catch (NoClassDefFoundError | Exception e) {
            LOGGER.log(Level.SEVERE, "Exception while closing kanban: {0}", e.getMessage());
        }
        
        // No response needed
        return Optional.empty();
    }
}
