package client.src.interfaces;

import java.util.List;
import java.util.UUID;
import common.src.dataClasses.LightUser;
import common.src.dataClasses.LightKanban;

public interface DataClientCallsMain {
    void updateListsKanbansUsers(List<LightKanban> kanbansOfUserDisconnected, UUID userId);
    void updateListKanban(LightKanban lightKanban);
    void addListModifiers(UUID kanbanId, UUID userId);
    void uploadKanbans(UUID kanbanId);
    void publishUsersList(List<LightUser> users);
    void publishKanbansList(List<LightKanban> kanbans);
}
