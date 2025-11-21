package client.comm.messages;

import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;
import client.interfaces.DataClientCallsMain;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public class UpdateUsersAndKanbansListResponse extends Message implements Serializable {

    private static final long serialVersionUID = 1L;

    private final List<LightUser> users;
    private final List<LightKanban> kanbans;

    // Ce constructeur est appelé côté serveur
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
        // Récupérer la couche Data côté client (à adapter selon ton contexte)
        assert client.MainApp.getCore() != null;
        DataClientCallsMain dataMain = client.MainApp.getCore().getDATService();

        if (dataMain != null) {
            // On remplace la liste complète côté MainCore
            dataMain.publishUsersList(users);
            dataMain.publishKanbansList(kanbans);
        }

        // Pas de réponse à renvoyer au serveur
        return Optional.empty();
    }
}