import java.util.UUID;

public interface DataCallsComm {
    void sendNewKanban(Kanban kanban);
    void sendModifiedKanban(Kanban kanban);
    void askDeleteKanban(UUID LightKanbanId, UUID LightUserId);    
}
