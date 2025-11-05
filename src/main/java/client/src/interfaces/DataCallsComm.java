package client.src.interfaces;
import java.util.UUID;
import common.src.dataClasses.Kanban;

public interface DataCallsComm {
    void askDeleteKanban(UUID LightKanbanId, UUID LightUserId);
    void sendKanban(Kanban Kanban);     
}
