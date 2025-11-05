import java.util.UUID;

public interface DataCallsComm {
    void askDeleteKanban(UUID LightKanbanId, UUID LightUserId);
    void sendKanban(Kanban Kanban);     
}
