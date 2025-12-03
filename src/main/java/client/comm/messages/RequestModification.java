package client.comm.messages;

import java.util.List;
import java.util.Optional;
import common.dataClasses.LightUser;
import common.dataClasses.Modification;
import server.comm.CommCoreServer;

/**
 * Message pour demander la modification d'une carte dans un kanban.
 * Correspond au diagramme 1 - Requête de modification.
 */
public class RequestModification extends Message {
    private static final long serialVersionUID = 1L;

    private final LightUser user;
    private Modification modification;

    public RequestModification(LightUser user, Modification modification) {
        this.user = user;
        this.modification = modification;
    }

    @Override
    public Optional<Message> handle() {
        try {
            System.out.println("[SERVER] Reçu demande de modification de carte " + modification.getId() + 
                " pour le kanban \"" + modification.getTargetKanban().getTitle() + "\" par " + 
                (user != null ? user.getUsername() : "Inconnu"));

            // Récupération du contexte serveur et du data server
            var dataServer = this.getServerContext().getData();
            if (dataServer == null) {
                System.err.println("[RequestModification] DataServer non disponible");
                return Optional.empty();
            }

            // Sauvegarder la modification et récupérer les utilisateurs à notifier
            List<LightUser> usersToNotify = 
                dataServer.saveModifiedKanban(modification.getTargetKanban(), modification);

            if (usersToNotify != null && !usersToNotify.isEmpty()) {
                System.out.println("[SERVER] Notification de " + usersToNotify.size() + " utilisateurs");
                
                // Notifier tous les utilisateurs autorisés via CommCoreServer
                try {
                    for (LightUser userToNotify : usersToNotify) {
                        NotifyEdition notifyMsg = new NotifyEdition(
                            modification.getTargetKanban(), modification);
                        CommCoreServer.sendToUser(userToNotify.getId(), notifyMsg);
                        System.out.println("[SERVER] Notification envoyée à " + userToNotify.getUsername());
                    }
                } catch (Exception e) {
                    System.err.println("[RequestModification] Erreur lors de l'envoi des notifications: " + e.getMessage());
                }
            }

        } catch (Exception e) {
            java.util.logging.Logger.getLogger(RequestModification.class.getName())
                    .log(java.util.logging.Level.SEVERE, "Erreur lors du traitement de la modification", e);
        }
        return Optional.empty();
    }   

}