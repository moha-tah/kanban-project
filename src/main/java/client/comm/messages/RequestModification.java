package client.comm.messages;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

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
    private final Modification myModification;

    public static final Logger LOGGER = Logger.getLogger("Request Modification");

    public RequestModification(LightUser user, Modification myModification) {
        this.user = user;
        this.myModification = myModification;
    }

    @Override
    public Optional<Message> handle() {
        try {
            System.out.println("[SERVER] Reçu demande de modification de carte " + myModification.getId() +
                    " pour le kanban \"" + myModification.getLightTargetKanban().getTitle() + "\" par " +
                    (user != null ? user.getUsername() : "Inconnu"));

            // Récupération du contexte serveur et du data server
            var dataServer = server.ServerContext.getData();
            if (dataServer == null) {
                System.err.println("[RequestModification] DataServer non disponible");
                return Optional.empty();
            }

            // Sauvegarder la modification et récupérer les utilisateurs à notifier
            List<LightUser> usersToNotify = dataServer.saveModifiedKanban(myModification);

            if (usersToNotify != null && !usersToNotify.isEmpty()) {
                System.out.println("[SERVER] Notification de " + usersToNotify.size() + " utilisateurs");

                // Notifier tous les utilisateurs autorisés via CommCoreServer
                try {
                    for (LightUser userToNotify : usersToNotify) {
                        NotifyEdition notifyMsg = new NotifyEdition(
                                myModification.getLightTargetKanban(), myModification);
                        CommCoreServer.sendToUser(userToNotify.getId(), notifyMsg);
                        System.out.println("[SERVER] Notification envoyée à " + userToNotify.getUsername());
                    }
                } catch (Exception e) {
                    System.err.println(
                            "[RequestModification] Erreur lors de l'envoi des notifications: " + e.getMessage());
                }
            }

            // Notifier aussi les viewers (utilisateurs ayant la visibilité du kanban
            // mais ne participant pas à la modification)
            try {
                List<LightUser> allUsers = dataServer.getUsersList();
                java.util.Set<java.util.UUID> alreadyNotified = new java.util.HashSet<>();
                if (usersToNotify != null) {
                    for (LightUser u : usersToNotify) {
                        if (u != null) alreadyNotified.add(u.getId());
                    }
                }

                for (LightUser candidate : allUsers) {
                    if (candidate == null || alreadyNotified.contains(candidate.getId())) continue;

                    List<common.dataClasses.LightKanban> visibles = dataServer.getVisibleKanbansForUser(candidate);
                    boolean canSeeKanban = visibles.stream()
                            .anyMatch(k -> k.getId().equals(myModification.getLightTargetKanban().getId()));
                    if (canSeeKanban) {
                        // Envoyer NotifyModification au viewer
                        NotifyModification viewerMsg = new NotifyModification(
                                myModification.getLightTargetKanban(), myModification);
                        server.comm.CommCoreServer.sendToUser(candidate.getId(), viewerMsg);
                    }
                }
            } catch (Exception e) {
                System.err.println("[RequestModification] Erreur notification viewers: " + e.getMessage());
            }

        } catch (Exception e) {
            java.util.logging.Logger.getLogger(RequestModification.class.getName())
                    .log(java.util.logging.Level.SEVERE, "Erreur lors du traitement de la modification", e);
        }
        return Optional.empty();
    }

    // Getters
    public LightUser getUser() {
        return user;
    }

    public Modification getMyModification() {
        return myModification;
    }
}