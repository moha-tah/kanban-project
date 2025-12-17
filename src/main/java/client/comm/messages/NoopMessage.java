package client.comm.messages;

import java.util.Optional;

/**
 * Message "no-op" (no operation) utilisé pour satisfaire les exigences de réponse.
 * 
 * Ce message ne fait rien et est utilisé lorsqu'une réponse est nécessaire
 * mais qu'aucune action n'est requise. Par exemple, lorsqu'un profil utilisateur
 * n'est pas trouvé mais qu'une réponse doit être envoyée pour éviter des avertissements.
 * 
 * @author Équipe Kanban
 * @version 1.0
 * @since 1.0
 * @see Message
 */
public class NoopMessage extends Message {
    private static final long serialVersionUID = 1L;

    /**
     * Traite le message (ne fait rien).
     * 
     * @return Toujours un Optional vide
     * @throws Exception jamais
     */
    @Override
    public Optional<Message> handle() throws Exception {
        return Optional.empty();
    }
}
