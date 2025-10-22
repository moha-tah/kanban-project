import java.util.List;
import java.util.UUID;
package interfaces;

public interface IhmMainCallsComm {
    void logout(UUID LightUserId);
    void askListModifiers(UUID LightUserId);
    void sendPermissionRequest(UUID LightUserId, UUID LightKanbanId);
    void sendPermissionResponse(UUID LightUserId, UUID LightKanbanId, boolean accepted);
    void connectToServer(UUID LightUserId, List<LightKanban> listKanbans);
    void connectionRequest(LightUser LightUser, List<LightKanban> listKanbans);
    void closingKanbanToServer(UUID LightUserId);
    void forwardRequestModification(UUID LightUserId, UUID CardId, STATUS);
    void notifyDecision(UUID LightUserId, UUID LightKanbanId, boolean accepted);
    void notifyEditions(LightKanban LightKanban);
    void askKanban(UUID LightKanbanId);
    Kanban getKanban(UUID LightKanbanId, UUID LightUserId);   
}
