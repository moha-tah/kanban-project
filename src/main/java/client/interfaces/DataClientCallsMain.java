package client.interfaces;

import java.util.List;
import java.util.UUID;
import common.dataClasses.LightUser;
import common.dataClasses.LightKanban;

public interface DataClientCallsMain {
    void updateListsKanbansUsers(List<LightKanban> kanbansOfUserDisconnected, UUID userId);
    void updateListKanban(LightKanban lightKanban);
    void addListModifiers(UUID kanbanId, UUID userId);
    void uploadKanbans(UUID kanbanId);
    void addUserToList(LightUser users);
    void publishUsersList(List<LightUser> users);
    void publishKanbansList(List<LightKanban> kanbans);
    void addKanbansList(List<LightKanban> kanbans);
}
