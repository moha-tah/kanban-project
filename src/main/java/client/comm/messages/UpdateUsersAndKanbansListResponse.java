package client.comm.messages;

import java.util.List;
import java.util.Optional;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;

/**
 * Message sent from server to clients to update their users and kanbans lists.
 * This is sent when a new user connects or when lists need to be synchronized.
 */
public class UpdateUsersAndKanbansListResponse extends Message {
    private static final long serialVersionUID = 1L;

    private final List<LightUser> users;
    private final List<LightKanban> kanbans;

    public UpdateUsersAndKanbansListResponse(List<LightUser> users, List<LightKanban> kanbans) {
        this.users = users;
        this.kanbans = kanbans;
    }

    public List<LightUser> getUsers() {
        return users;
    }

    public List<LightKanban> getKanbans() {
        return kanbans;
    }

    @Override
    public Optional<Message> handle() {
        try {
            // Update the client's local data model with the lists from server
            client.ClientContext ctx = new client.ClientContext();
            if (ctx.getData() != null) {
                // Mettre à jour le modèle avec les listes reçues
                ctx.getData().updateUserList(this.users, this.kanbans);
                
                // Notifier l'UI des changements via les méthodes existantes
                if (!this.users.isEmpty()) {
                    ctx.getData().updateLists(this.users.get(0));
                }
                if (!this.kanbans.isEmpty()) {
                    ctx.getData().uploadKanbans(this.kanbans.get(0));
                }
            }
        } catch (Throwable t) {
            // Ignored on client side - already connected
        }
        
        return Optional.empty();
    }
}
