package client.comm.messages;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

import common.dataClasses.LightUser;
import common.dataClasses.User;

/**
 * Message envoyé par un client au serveur pour demander le profil
 * d'un utilisateur distant par ses identifiants UUID.
 * 
 * Ce message est traité côté serveur qui transmet la demande au client
 * du propriétaire du profil via un message {@link FetchDistantProfile}.
 * Le serveur ne stocke pas les profils complets, il fait simplement
 * transiter la demande.
 * 
 * @author Équipe Kanban
 * @version 1.0
 * @since 1.0
 * @see Message
 * @see FetchDistantProfile
 * @see DistProfileAnswer
 */
public class DistProfileRequest extends Message {
    private static final long serialVersionUID = 1L;
    
    /**
     * Logger pour les messages de log de cette classe.
     */
    private static final Logger LOGGER = Logger.getLogger(DistProfileRequest.class.getName());

    /**
     * L'identifiant de l'utilisateur qui fait la demande.
     */
    private final UUID requesterId;
    
    /**
     * L'identifiant de l'utilisateur dont on veut le profil.
     */
    private final UUID requestedUserId;

    /**
     * Constructeur du message de demande de profil distant.
     * 
     * @param requesterId L'identifiant de l'utilisateur qui fait la demande (ne doit pas être null)
     * @param requestedUserId L'identifiant de l'utilisateur dont on veut le profil (ne doit pas être null)
     */
    public DistProfileRequest(UUID requesterId, UUID requestedUserId) {
        this.requesterId = requesterId;
        this.requestedUserId = requestedUserId;
    }

    /**
     * Récupère l'identifiant de l'utilisateur qui fait la demande.
     * 
     * @return L'identifiant du demandeur
     */
    public UUID requesterId() {
        return requesterId;
    }

    /**
     * Récupère l'identifiant de l'utilisateur dont on veut le profil.
     * 
     * @return L'identifiant de l'utilisateur demandé
     */
    public UUID requestedUserId() {
        return requestedUserId;
    }

    @Override
    public Optional<Message> handle() {
        // Executed on SERVER side
        try {
            // New behavior: do NOT read or store full profiles on server.
            // Forward the request to the concerned user so their client can reply.
            LOGGER.info(String.format("[SERVER] Forwarding FetchDistantProfile to target=%s for requester=%s",
                    requestedUserId, requesterId));
            boolean sent = server.comm.CommCoreServer.sendToUser(
                    requestedUserId,
                    new FetchDistantProfile(requesterId, requestedUserId));

            if (!sent) {
                LOGGER.warning(() -> "[SERVER] Target user not connected; cannot fetch distant profile: "
                        + requestedUserId);
            }
            // No immediate response from server; the target client will send DistProfileAnswer
            // which the server will relay to the requester.
            return Optional.empty();
        } catch (Exception e) {
            LOGGER.log(java.util.logging.Level.SEVERE, "Error handling DistProfileRequest", e);
            return Optional.empty();
        }
    }
    
}
