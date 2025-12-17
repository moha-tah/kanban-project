package client.comm.messages;

import java.util.Optional;
import java.util.UUID;

import common.dataClasses.User;

/**
 * Message envoyé par le client propriétaire du profil au serveur en réponse
 * à une demande de profil distant.
 * 
 * Ce message contient le profil complet de l'utilisateur et est traité
 * côté serveur qui le transmet au demandeur via un message
 * {@link ForwardProfileAnswer}.
 * 
 * @author Équipe Kanban
 * @version 1.0
 * @since 1.0
 * @see Message
 * @see FetchDistantProfile
 * @see ForwardProfileAnswer
 */
public class DistProfileAnswer extends Message {
    private static final long serialVersionUID = 1L;

    /**
     * L'identifiant de l'utilisateur qui a fait la demande initiale.
     */
    private final UUID requesterId;
    
    /**
     * Le profil complet de l'utilisateur demandé.
     */
    private final User requestedUser;

    /**
     * Constructeur du message de réponse avec le profil complet.
     * 
     * @param requesterId L'identifiant de l'utilisateur qui a fait la demande (ne doit pas être null)
     * @param requestedUser Le profil complet de l'utilisateur demandé (peut être null si non trouvé)
     */
    public DistProfileAnswer(UUID requesterId, User requestedUser) {
        this.requesterId = requesterId;
        this.requestedUser = requestedUser;
    }

    /**
     * Récupère l'identifiant de l'utilisateur qui a fait la demande.
     * 
     * @return L'identifiant du demandeur
     */
    public UUID requesterId() { return requesterId; }
    
    /**
     * Récupère le profil complet de l'utilisateur demandé.
     * 
     * @return Le profil complet, ou null si non trouvé
     */
    public User requestedUser() { return requestedUser; }

    @Override
    public Optional<Message> handle() {
        // Executed on SERVER side: forward to the requester client
        try {
            server.comm.CommCoreServer.sendToUser(requesterId, new ForwardProfileAnswer(requesterId, requestedUser));
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(DistProfileAnswer.class.getName())
                .log(java.util.logging.Level.SEVERE, "Error handling DistProfileAnswer", e);
        }
        return Optional.empty();
    }
}
