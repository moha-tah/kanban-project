package client.comm.messages;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import common.dataClasses.LightUser;
import common.dataClasses.Modification;
import server.comm.CommCoreServer;

/**
 * Message envoyé par un client pour demander la modification d'une carte dans un kanban.
 * 
 * Ce message est traité côté serveur qui sauvegarde la modification et notifie
 * tous les utilisateurs autorisés à visualiser le kanban via un message
 * {@link NotifyEdition}. Correspond au diagramme 1 - Requête de modification.
 * 
 * @author Équipe Kanban
 * @version 1.0
 * @since 1.0
 * @see Message
 * @see NotifyEdition
 * @see Modification
 */
public class RequestModification extends Message {
    private static final long serialVersionUID = 1L;

    /**
     * L'utilisateur qui demande la modification.
     */
    private final LightUser user;
    
    /**
     * La modification à appliquer (transitoire, non sérialisée).
     */
    private final transient Modification myModification;

    /**
     * Logger pour les messages de log de cette classe.
     */
    public static final Logger LOGGER = Logger.getLogger("Request Modification");

    /**
     * Constructeur du message de demande de modification.
     * 
     * @param user L'utilisateur qui demande la modification (ne doit pas être null)
     * @param myModification La modification à appliquer (ne doit pas être null)
     */
    public RequestModification(LightUser user, Modification myModification) {
        this.user = user;
        this.myModification = myModification;
    }

    @Override
    public Optional<Message> handle() {
        try {
            System.out.println("[SERVER] Reçu demande de modification de carte " + myModification.getId() +
                    " pour le kanban \"" + myModification.getTargetKanban().getTitle() + "\" par " +
                    (user != null ? user.getUsername() : "Inconnu"));

            // Récupération du contexte serveur et du data server
            var dataServer = server.ServerContext.getData();
            if (dataServer == null) {
                System.err.println("[RequestModification] DataServer non disponible");
                return Optional.empty();
            }

            // Sauvegarder la modification et récupérer les utilisateurs à notifier
            List<LightUser> usersToNotify = dataServer.saveModifiedKanban(myModification.getTargetKanban(),
                    myModification);

            if (usersToNotify != null && !usersToNotify.isEmpty()) {
                System.out.println("[SERVER] Notification de " + usersToNotify.size() + " utilisateurs");

                // Notifier tous les utilisateurs autorisés via CommCoreServer
                try {
                    for (LightUser userToNotify : usersToNotify) {
                        NotifyEdition notifyMsg = new NotifyEdition(
                                myModification.getTargetKanban(), myModification);
                        CommCoreServer.sendToUser(userToNotify.getId(), notifyMsg);
                        System.out.println("[SERVER] Notification envoyée à " + userToNotify.getUsername());
                    }
                } catch (Exception e) {
                    System.err.println(
                            "[RequestModification] Erreur lors de l'envoi des notifications: " + e.getMessage());
                }
            }

        } catch (Exception e) {
            java.util.logging.Logger.getLogger(RequestModification.class.getName())
                    .log(java.util.logging.Level.SEVERE, "Erreur lors du traitement de la modification", e);
        }
        return Optional.empty();
    }

    /**
     * Récupère l'utilisateur qui demande la modification.
     * 
     * @return L'utilisateur qui demande la modification
     */
    public LightUser getUser() {
        return user;
    }

    /**
     * Récupère la modification à appliquer.
     * 
     * @return La modification à appliquer
     */
    public Modification getMyModification() {
        return myModification;
    }
}