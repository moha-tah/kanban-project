package client.comm.messages;

import java.util.Optional;
import java.util.UUID;

import common.dataClasses.User;

/**
 * Message envoyé par le serveur au client demandeur pour transmettre
 * le profil complet de l'utilisateur distant.
 * 
 * Ce message est traité côté client du demandeur qui affiche le profil
 * dans l'interface utilisateur. Si le profil n'est pas trouvé (null),
 * un message {@link NoopMessage} est retourné pour éviter les avertissements.
 * 
 * @author Équipe Kanban
 * @version 1.0
 * @since 1.0
 * @see Message
 * @see DistProfileAnswer
 * @see NoopMessage
 */
public class ForwardProfileAnswer extends Message {
    private static final long serialVersionUID = 1L;

    /**
     * L'identifiant de l'utilisateur qui a fait la demande (le destinataire de ce message).
     */
    private final UUID requesterId;
    
    /**
     * Le profil complet de l'utilisateur demandé.
     */
    private final User requestedUser;

    /**
     * Constructeur du message de transmission de profil.
     * 
     * @param requesterId L'identifiant de l'utilisateur qui a fait la demande (ne doit pas être null)
     * @param requestedUser Le profil complet de l'utilisateur demandé (peut être null si non trouvé)
     */
    public ForwardProfileAnswer(UUID requesterId, User requestedUser) {
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
        // Executed on CLIENT side
        try {
            var commToMain = this.getClientContext() != null ? this.getClientContext().getMainComm() : null;
            // Invoke callback; underlying UI layer should handle null user gracefully
            if (commToMain != null) {
                commToMain.displayDistantProfile(requestedUser);
            }
            // Return a Noop reply when user not found to avoid constant return warnings
            return requestedUser == null ? Optional.of(new NoopMessage()) : Optional.empty();
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(ForwardProfileAnswer.class.getName())
                    .log(java.util.logging.Level.SEVERE, "Error handling ForwardProfileAnswer", e);
            return Optional.empty();
        }
    }
}
