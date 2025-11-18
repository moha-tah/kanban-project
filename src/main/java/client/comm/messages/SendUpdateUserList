package common.messages;

import java.util.List;
import java.util.Optional;
import common.model.LightKanban;
import common.model.LightUser;

public class SendUpdateUserList extends Message {
    private static final long serialVersionUID = 1L;

    private LightUser newUser;
    private List<LightKanban> newKanbans;

    public SendUpdateUserList(LightUser newUser, List<LightKanban> newKanbans) {
        this.newUser = newUser;
        this.newKanbans = newKanbans;
    }

    @Override
    public Optional<Message> handle() {
        try {
            // LOGIQUE CLIENT (Fig 17 bas)
            // Les clients déjà connectés ajoutent ce nouvel arrivant à leur liste
            if (client.ClientContext.getData() != null) {
                System.out.println("CLIENT: Un nouvel utilisateur s'est connecté : " + newUser.getLogin());
                
                // Appel de addUserToList(LightUser, List<LightKanban>) dans ComCallsDataClient
                client.ClientContext.getData().addUserToList(this.newUser, this.newKanbans);
            }
        } catch (Throwable t) {
            // Ignoré sur le serveur
        }
        return Optional.empty();
    }
}