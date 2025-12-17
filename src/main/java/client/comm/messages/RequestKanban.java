package client.comm.messages;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import common.dataClasses.Kanban;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;

/**
 * Message envoyé par un client pour demander un kanban complet au serveur.
 * 
 * Ce message est traité côté serveur qui renvoie le kanban complet
 * via un message {@link SendKanban} si le kanban existe.
 * 
 * @author Équipe Kanban
 * @version 1.0
 * @since 1.0
 * @see Message
 * @see SendKanban
 */
public class RequestKanban extends Message {

    /**
     * Logger pour les messages de log de cette classe.
     */
    private static final Logger LOGGER = Logger.getLogger(RequestKanban.class.getName());
    
    /**
     * La version légère du kanban à récupérer.
     */
    private final LightKanban lightKanbanId;
    
    /**
     * L'utilisateur qui demande le kanban.
     */
    private final LightUser lightUserId;

    /**
     * Constructeur du message de demande de kanban.
     * 
     * @param kanbanId La version légère du kanban à récupérer (ne doit pas être null)
     * @param userId L'utilisateur qui demande le kanban (ne doit pas être null)
     */
    public RequestKanban(LightKanban kanbanId, LightUser userId) {
        this.lightKanbanId = kanbanId;
        this.lightUserId = userId;
    }
    
    @Override
    public Optional<Message> handle() {        
        try {
            var dataServer = server.ServerContext.getData();

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
