package client.comm.messages;

import java.util.Optional;

import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;
import server.interfaces.CommCallsDataServer;

/**
 * Message envoyé pour demander l'ajout d'un utilisateur à la liste des modificateurs d'un kanban.
 * 
 * Ce message est traité côté serveur qui ajoute l'utilisateur à la liste
 * des personnes autorisées à modifier le kanban.
 * 
 * @author Équipe Kanban
 * @version 1.0
 * @since 1.0
 * @see Message
 */
public class AskAddListModifiers extends Message {
    private static final long serialVersionUID = 1L;

    /**
     * L'utilisateur à ajouter comme modificateur.
     */
    private final LightUser userToAdd;
    
    /**
     * Le kanban pour lequel ajouter le modificateur.
     */
    private final LightKanban targetKanban;

    /**
     * Constructeur du message de demande d'ajout de modificateur.
     * 
     * @param userToAdd L'utilisateur à ajouter comme modificateur (ne doit pas être null)
     * @param targetKanban Le kanban pour lequel ajouter le modificateur (ne doit pas être null)
     */
    public AskAddListModifiers(LightUser userToAdd, LightKanban targetKanban) {
        this.userToAdd = userToAdd;
        this.targetKanban = targetKanban;
    }

    @Override
    public Optional<Message> handle() {
        try {
            CommCallsDataServer dataServer = server.ServerContext.getData();
            
            if (dataServer != null) {
                // Ajoute l'utilisateur à la liste des modificateurs du kanban côté serveur
                dataServer.askAddListModifiers(userToAdd, targetKanban);
            }
        } catch (Throwable t) {
            // Ignoré sur le client
        }
        return Optional.empty();
    }
}