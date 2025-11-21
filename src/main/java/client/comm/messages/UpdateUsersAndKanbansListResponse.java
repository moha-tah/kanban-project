package client.comm.messages;

import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;
import client.interfaces.DataClientCallsMain;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import javafx.application.Platform;

public class UpdateUsersAndKanbansListResponse extends Message implements Serializable {

    private static final long serialVersionUID = 1L;

    private final List<LightUser> users;
    private final List<LightKanban> kanbans;

    // Ce constructeur est appelé côté serveur
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
        logger.log(Level.INFO, "UpdateUsersAndKanbansListResponse received: {0} users, {1} kanbans", new Object[]{this.users.size(), this.kanbans.size()});
        
        try {
            // Update the client's local data model with the lists from server
            // Utiliser le ClientContext attaché au message (défini par le MsgReceiver)
            client.ClientContext ctx = this.getClientContext();
            if (ctx == null) {
                logger.warning("UpdateUsersAndKanbansListResponse: ClientContext is null!");
                return Optional.empty();
            }
            if (client.ClientContext.getData() == null) {
                logger.warning("UpdateUsersAndKanbansListResponse: Data interface is null!");
                return Optional.empty();
            }
            
            logger.log(Level.INFO, "UpdateUsersAndKanbansListResponse: Updating model with {0} users", this.users.size());
            // Mettre à jour le modèle avec les listes reçues (peut être fait sur le thread réseau)
            client.ClientContext.getData().updateUserList(this.users, this.kanbans);
            
            // Notifier l'UI des changements via les méthodes existantes
            // IMPORTANT: Les modifications de l'UI JavaFX doivent être faites sur le thread JavaFX
            Platform.runLater(() -> {
                try {
                    logger.info("UpdateUsersAndKanbansListResponse: Updating UI on JavaFX thread");
                    // Appeler updateLists pour publier la liste complète des utilisateurs
                    client.ClientContext.getData().updateLists(null); // null car on veut publier toute la liste
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