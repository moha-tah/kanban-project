package client.comm.messages;

import java.util.List;
import java.util.Optional;
import common.dataClasses.LightUser;
import common.dataClasses.LightKanban;

/**
 * Message envoyé par le serveur au client pour transmettre les listes
 * actuelles des utilisateurs et des kanbans.
 * 
 * Ce message est traité côté client qui met à jour son état local.
 * Aucune réponse n'est nécessaire.
 * 
 * @author Équipe Kanban
 * @version 1.0
 * @since 1.0
 * @see Message
 */
public class SendUserAndKanbanList extends Message {
    private static final long serialVersionUID = 1L;

    /**
     * Liste des utilisateurs connectés.
     */
    private final List<LightUser> users;
    
    /**
     * Liste des kanbans disponibles.
     */
    private final List<LightKanban> kanbans;

    /**
     * Constructeur du message d'envoi des listes d'utilisateurs et kanbans.
     * 
     * @param users Liste des utilisateurs connectés (ne doit pas être null)
     * @param kanbans Liste des kanbans disponibles (ne doit pas être null)
     */
    public SendUserAndKanbanList(List<LightUser> users, List<LightKanban> kanbans) {
        this.users = users;
        this.kanbans = kanbans;
    }

    /**
     * Récupère la liste des utilisateurs connectés.
     * 
     * @return La liste des utilisateurs connectés
     */
    public List<LightUser> getUsers() {
        return users;
    }

    /**
     * Récupère la liste des kanbans disponibles.
     * 
     * @return La liste des kanbans disponibles
     */
    public List<LightKanban> getKanbans() {
        return kanbans;
    }

    @Override
    public Optional<Message> handle() throws Exception {
        // Client-side will consume this to update its state; no reply needed.
        return Optional.empty();
    }
}
