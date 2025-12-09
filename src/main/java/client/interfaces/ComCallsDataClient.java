package client.interfaces;

import common.dataClasses.Kanban;
import common.dataClasses.LightUser;
import common.dataClasses.LightKanban;
import common.dataClasses.Modification;
import common.dataClasses.User;

import java.util.List;

public interface ComCallsDataClient {

    void send(Kanban kanban);

    void updateLists(LightUser user);

    void uploadKanbans(LightKanban kanban);

    void addListModifiers(LightUser user, LightKanban kanban);

    boolean addAuthorizedUser(LightKanban kanbanId, LightUser userId);

    void updateUserList(List<LightUser> users, List<LightKanban> kanbans);

    void addToListKanban(LightKanban kanban);

    void saveModifiedKanban(Modification modification, LightKanban kanban);

    void saveTempKanban(Kanban kanban);

    void addUserToList(LightUser user, List<LightKanban> kanbans);
    LightUser askIdUser();
    User getDistantProfile();
}
