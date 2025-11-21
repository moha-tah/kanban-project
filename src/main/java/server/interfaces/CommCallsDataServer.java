package server.interfaces;

import java.util.List;
import java.util.UUID;

import common.dataClasses.Kanban;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;
import common.dataClasses.Modification;


// CommCallsData
public interface CommCallsDataServer {
    Kanban requestKanban(LightUser lightUser, UUID lightKanbId);
    List<Kanban> notifyLogout(UUID userId);
    void askDeleteKanban(LightUser user, LightKanban kanban);
    void askAddListModifiers(LightUser user, LightKanban kanban);
    boolean addAuthorizedUser(UUID kanbanId, UUID userId);
    void addNewUser(LightUser user, List<LightKanban> kanbans);
    List<LightUser> getUsersList();
    List<LightKanban> getKanbansList();
    LightKanban saveKanban(Kanban kanban);
    List<LightUser> saveModifiedKanban(LightKanban kanban, Modification modif);
    Kanban getKanban(LightKanban kanban, LightUser user);
    void closeKanban(LightKanban kaban, LightUser user);
}