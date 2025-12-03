package client.comm.messages;

import java.util.Optional;
import java.util.UUID;

import common.dataClasses.LightUser;

/**
 * Message pour demander la modification d'une carte dans un kanban.
 * Correspond au diagramme 1 - Requête de modification.
 */
public class RequestModification extends Message {
    private static final long serialVersionUID = 1L;

    private final LightUser user;
    private final UUID cardId;
    private final String newStatus;

    public RequestModification(LightUser user, UUID cardId, String newStatus) {
        this.user = user;
        this.cardId = cardId;
        this.newStatus = newStatus;
    }

    @Override
    public Optional<Message> handle() {
        try {
            System.out.println("[SERVER] Reçu demande de modification de carte " + cardId + 
                " vers status " + newStatus + " par " + 
                (user != null ? user.getUsername() : "Inconnu"));

            // TODO: Implémenter la logique de modification
            // 1. Trouver le kanban contenant cette carte
            // 2. Créer l'objet Modification
            // 3. Appeler saveModification sur Data Server
            // 4. Déclencher les notifications aux utilisateurs autorisés

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

    public UUID getCardId() {
        return cardId;
    }

    public String getNewStatus() {
        return newStatus;
    }
}