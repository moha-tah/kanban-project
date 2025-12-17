package client.comm.messages;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;

/**
 * Message envoyé depuis l'interface Kanban pour fermer la visualisation d'un kanban.
 * 
 * Ce message est transmis au serveur qui retire l'utilisateur de la liste
 * des viewers du kanban. Le kanban reste disponible en mémoire pour les autres utilisateurs.
 * 
 * @author Équipe Kanban
 * @version 1.0
 * @since 1.0
 * @see Message
 */
public class CloseKanban extends Message {
    /**
     * Logger pour les messages de log de cette classe.
     */
    private static final Logger LOGGER = Logger.getLogger(CloseKanban.class.getName());
    private static final long serialVersionUID = 1L;

    /**
     * Le kanban à fermer.
     */
    private final LightKanban lightKanban;
    
    /**
     * L'utilisateur qui ferme le kanban.
     */
    private final LightUser lightUser;

    /**
     * Constructeur du message de fermeture de kanban.
     * 
     * @param lightKanban Le kanban à fermer (ne doit pas être null)
     * @param lightUser L'utilisateur qui ferme le kanban (ne doit pas être null)
     */
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
