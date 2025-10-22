import java.util.List;
import java.util.UUID;

public interface IhmMainCallsComm {
    void logout(UUID LightUserId);
    void askAddListModifiers(UUID LightUserId);
    void sendPermissionRequest(UUID LightUserId, UUID LightKanbanId);
    void sendPermissionResponse(UUID LightUserId, UUID LightKanbanId, boolean accepted);
    void connectToServer(UUID LightUserId, List<LightKanban> listKanbans);
    void askKanban(UUID LightKanbanId);
    Kanban getKanban(UUID LightKanbanId, UUID LightUserId);   
}
