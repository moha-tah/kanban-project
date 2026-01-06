package client.interfaces;

import common.dataClasses.Kanban;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;
import common.dataClasses.User;

public interface DataCallsComm {
    void askDeleteKanban(LightKanban LightKanbanId, LightUser LightUserId);
    void sendKanban(Kanban Kanban);
    void addAuthorizedUser(LightKanban kanbanId, LightUser userId);

    // Méthode ajoutée pour corriger l'erreur de compilation
    void sendUpdateUserList(User user);
}