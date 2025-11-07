package client.interfaces;

import common.dataClasses.Kanban;
import common.dataClasses.LightUser;
import common.dataClasses.LightKanban;
import common.dataClasses.Modification;

import java.util.List;
import java.util.UUID;


public interface ComCallsDataClient {

    void send(Kanban kanban);

    void updateLists(LightUser user);

    void uploadKanbans(LightKanban kanban);

    void addListModifiers(LightUser user, LightKanban kanban);

    boolean addAuthorizedUser(UUID kanbanId, UUID userId);

    void updateUserList(List<LightUser> users, List<LightKanban> kanbans);

    void addToListKanban(LightKanban kanban);

    void saveModifiedKanban(Modification modification, LightKanban kanban);

    void saveTempKanban(Kanban kanban);
}
