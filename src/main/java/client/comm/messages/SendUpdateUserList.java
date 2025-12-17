package client.comm.messages;

import java.util.List;
import java.util.Optional;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;

/**
 * Message envoyé par le serveur pour notifier les clients de l'arrivée
 * d'un nouvel utilisateur et de ses kanbans.
 * 
 * Ce message est traité côté client qui ajoute le nouvel utilisateur
 * et ses kanbans à sa liste locale.
 * 
 * @author Équipe Kanban
 * @version 1.0
 * @since 1.0
 * @see Message
 */
public class SendUpdateUserList extends Message {
    private static final long serialVersionUID = 1L;

    /**
     * Le nouvel utilisateur qui vient de se connecter.
     */
    private final LightUser newUser;
    
    /**
     * Liste des kanbans du nouvel utilisateur.
     */
    private final List<LightKanban> newKanbans;

    /**
     * Constructeur du message de mise à jour de la liste d'utilisateurs.
     * 
     * @param newUser Le nouvel utilisateur qui vient de se connecter (ne doit pas être null)
     * @param newKanbans Liste des kanbans du nouvel utilisateur (peut être null ou vide)
     */
    public SendUpdateUserList(LightUser newUser, List<LightKanban> newKanbans) {
        this.newUser = newUser;
        this.newKanbans = newKanbans;
    }

    @Override
    public Optional<Message> handle() {
        try {
            // LOGIQUE CLIENT (Fig 17 bas)
            // Les clients déjà connectés ajoutent ce nouvel arrivant à leur liste
            if (this.getClientContext().getData() != null) {
                
                // Appel de addUserToList(LightUser, List<LightKanban>) dans ComCallsDataClient
                this.getClientContext().getData().addUserToList(this.newUser, this.newKanbans);
            }
        } catch (Throwable t) {
            // Ignoré sur le serveur
        }
        return Optional.empty();
    }
}