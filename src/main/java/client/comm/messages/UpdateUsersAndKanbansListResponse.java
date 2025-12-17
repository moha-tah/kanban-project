package client.comm.messages;

import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;
import client.interfaces.DataClientCallsMain;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import javafx.application.Platform;

/**
 * Message envoyé par le serveur en réponse à une demande de connexion.
 * 
 * Ce message contient la liste complète des utilisateurs connectés et
 * des kanbans disponibles. Il est traité côté client qui met à jour
 * son modèle local et l'interface utilisateur.
 * 
 * @author Équipe Kanban
 * @version 1.0
 * @since 1.0
 * @see Message
 * @see ConnectionRequest
 */
public class UpdateUsersAndKanbansListResponse extends Message {

    private static final long serialVersionUID = 1L;

    /**
     * Liste complète des utilisateurs connectés au serveur.
     */
    private final List<LightUser> users;
    
    /**
     * Liste complète des kanbans disponibles sur le serveur.
     */
    private final List<LightKanban> kanbans;

    /**
     * Constructeur du message de réponse avec les listes d'utilisateurs et kanbans.
     * 
     * Ce constructeur est appelé côté serveur lors de la connexion d'un client.
     * 
     * @param users Liste complète des utilisateurs connectés (ne doit pas être null)
     * @param kanbans Liste complète des kanbans disponibles (ne doit pas être null)
     */
    public UpdateUsersAndKanbansListResponse(List<LightUser> users, List<LightKanban> kanbans) {
        this.users = users;
        this.kanbans = kanbans;
    }

    /**
     * Récupère la liste des utilisateurs connectés.
     * 
     * @return La liste des utilisateurs connectés
     */
    public List<LightUser> getUsers() {
        return users;
    }

    /**
     * Récupère la liste des kanbans disponibles.
     * 
     * @return La liste des kanbans disponibles
     */
    public List<LightKanban> getKanbans() {
        return kanbans;
    }

    @Override
    public Optional<Message> handle() {
        java.util.logging.Logger logger = java.util.logging.Logger.getLogger(UpdateUsersAndKanbansListResponse.class.getName());
        logger.log(Level.INFO, "UpdateUsersAndKanbansListResponse received: {0} users, {1} kanbans", new Object[]{this.users.size(), this.kanbans.size()});
        
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
            
            logger.log(Level.INFO, "UpdateUsersAndKanbansListResponse: Updating model with {0} users", this.users.size());
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

        // Récupérer la couche Data côté client (à adapter selon ton contexte)
        assert client.MainApp.getCore() != null;
        DataClientCallsMain dataMain = client.MainApp.getCore().getDATService();

        if (dataMain != null) {
            // On remplace la liste complète côté MainCore
            dataMain.publishUsersList(users);
            dataMain.publishKanbansList(kanbans);
        }

        // Pas de réponse à renvoyer au serveur
        return Optional.empty();
    }
}