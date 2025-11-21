package client.comm.messages;

import java.util.List;
import java.util.Optional;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;
import javafx.application.Platform;

/**
 * Message sent from server to clients to update their users and kanbans lists.
 * This is sent when a new user connects or when lists need to be synchronized.
 */
public class UpdateUsersAndKanbansListResponse extends Message {
    private static final long serialVersionUID = 1L;

    private final List<LightUser> users;
    private final List<LightKanban> kanbans;

    public UpdateUsersAndKanbansListResponse(List<LightUser> users, List<LightKanban> kanbans) {
        this.users = users;
        this.kanbans = kanbans;
    }

    public List<LightUser> getUsers() {
        return users;
    }

    public List<LightKanban> getKanbans() {
        return kanbans;
    }

    @Override
    public Optional<Message> handle() {
        java.util.logging.Logger logger = java.util.logging.Logger.getLogger(UpdateUsersAndKanbansListResponse.class.getName());
        logger.info("UpdateUsersAndKanbansListResponse received: " + this.users.size() + " users, " + this.kanbans.size() + " kanbans");
        
        try {
            // Update the client's local data model with the lists from server
            // Utiliser le ClientContext attaché au message (défini par le MsgReceiver)
            client.ClientContext ctx = this.getClientContext();
            if (ctx == null) {
                logger.warning("UpdateUsersAndKanbansListResponse: ClientContext is null!");
                return Optional.empty();
            }
            if (ctx.getData() == null) {
                logger.warning("UpdateUsersAndKanbansListResponse: Data interface is null!");
                return Optional.empty();
            }
            
            logger.info("UpdateUsersAndKanbansListResponse: Updating model with " + this.users.size() + " users");
            // Mettre à jour le modèle avec les listes reçues (peut être fait sur le thread réseau)
            ctx.getData().updateUserList(this.users, this.kanbans);
            
            // Notifier l'UI des changements via les méthodes existantes
            // IMPORTANT: Les modifications de l'UI JavaFX doivent être faites sur le thread JavaFX
            Platform.runLater(() -> {
                try {
                    logger.info("UpdateUsersAndKanbansListResponse: Updating UI on JavaFX thread");
                    // Appeler updateLists pour publier la liste complète des utilisateurs
                    ctx.getData().updateLists(null); // null car on veut publier toute la liste
                } catch (Exception e) {
                    logger.log(java.util.logging.Level.SEVERE, "Error updating UI in Platform.runLater", e);
                }
            });
        } catch (Throwable t) {
            // Log l'erreur pour le débogage
            logger.log(java.util.logging.Level.SEVERE, "Error handling UpdateUsersAndKanbansListResponse", t);
        }
        
        return Optional.empty();
    }
}
