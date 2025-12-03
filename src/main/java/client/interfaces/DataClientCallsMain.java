package client.interfaces;

import java.util.List;
import common.dataClasses.LightUser;
import common.dataClasses.LightKanban;

public interface DataClientCallsMain {
    void updateListsKanbansUsers(List<LightKanban> kanbansOfUserDisconnected, LightUser userId);
    void updateListKanban(LightKanban lightKanban);
    void addListModifiers(LightKanban kanbanId, LightUser userId);
    void uploadKanbans(LightKanban kanbanId);
    void addUserToList(LightUser users);
    void publishUsersList(List<LightUser> users);
    void publishKanbansList(List<LightKanban> kanbans);
    void addUserToList(LightUser user, List<LightKanban> kanbans);
    void addKanbansList(List<LightKanban> kanbans);
}
