package client.interfaces;
import java.util.UUID;
import common.dataClasses.Kanban;

public interface DataCallsComm {
    void askDeleteKanban(UUID LightKanbanId, UUID LightUserId);
    void sendKanban(Kanban Kanban);   
    void addAuthorizedUser(UUID kanbanId, UUID userId);  
}
