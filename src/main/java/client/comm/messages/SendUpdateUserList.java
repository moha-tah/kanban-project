package client.comm.messages;

import java.util.List;
import java.util.Optional;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;

public class SendUpdateUserList extends Message {
    private static final long serialVersionUID = 1L;

    private final LightUser newUser;
    private final List<LightKanban> newKanbans;

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