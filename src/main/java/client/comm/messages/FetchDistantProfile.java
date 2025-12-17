package client.comm.messages;

import java.util.Optional;
import java.util.UUID;

import common.dataClasses.User;

/**
 * Message envoyé par le serveur au client propriétaire du profil pour demander
 * de récupérer le profil complet de l'utilisateur local.
 * 
 * Ce message est traité côté client qui construit le profil complet depuis
 * sa couche de données locale et répond au serveur avec un message
 * {@link DistProfileAnswer}.
 * 
 * @author Équipe Kanban
 * @version 1.0
 * @since 1.0
 * @see Message
 * @see DistProfileRequest
 * @see DistProfileAnswer
 */
public class FetchDistantProfile extends Message {
    private static final long serialVersionUID = 1L;

    /**
     * L'identifiant de l'utilisateur qui a fait la demande initiale.
     */
    private final UUID requesterId;
    
    /**
     * L'identifiant de l'utilisateur dont on veut le profil (correspond au propriétaire local).
     */
    private final UUID requestedUserId;

    /**
     * Constructeur du message de demande de récupération de profil.
     * 
     * @param requesterId L'identifiant de l'utilisateur qui a fait la demande (ne doit pas être null)
     * @param requestedUserId L'identifiant de l'utilisateur dont on veut le profil (ne doit pas être null)
     */
    public FetchDistantProfile(UUID requesterId, UUID requestedUserId) {
        this.requesterId = requesterId;
        this.requestedUserId = requestedUserId;
    }

    /**
     * Récupère l'identifiant de l'utilisateur qui a fait la demande.
     * 
     * @return L'identifiant du demandeur
     */
    public UUID requesterId() { return requesterId; }
    
    /**
     * Récupère l'identifiant de l'utilisateur dont on veut le profil.
     * 
     * @return L'identifiant de l'utilisateur demandé
     */
    public UUID requestedUserId() { return requestedUserId; }

    @Override
    public Optional<Message> handle() {
        // Executed on CLIENT (AppOwner) side: build full User and respond to server
        try {
            var dataCli = this.getClientContext() != null ? this.getClientContext().getData() : null;
            User full = null;
            if (dataCli != null) {
                // Let the local data layer assemble the full profile of the local user
                full = dataCli.getDistantProfile();
            }
            // Respond to server with DistProfileAnswer including the full user
            return Optional.of(new DistProfileAnswer(requesterId, full));
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(FetchDistantProfile.class.getName())
                .log(java.util.logging.Level.SEVERE, "Error handling FetchDistantProfile", e);
            return Optional.empty();
        }
    }
}
