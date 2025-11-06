package client.src.interfaces;
import common.src.dataClasses.LightKanban;
import common.src.dataClasses.LightUser;
import java.util.List;
import java.util.UUID;

public interface IhmMainCallsComm {
    void logout(UUID LightUserId);
    void askListModifiers(UUID LightUserId);
    void sendPermissionRequest(UUID LightUserId, UUID LightKanbanId);
    void sendPermissionResponse(UUID LightUserId, UUID LightKanbanId, boolean accepted);
    void connectToServer(UUID LightUserId, List<LightKanban> listKanbans);
    void connectionRequest(LightUser LightUser, List<LightKanban> listKanbans);
    void notifyDecision(UUID LightUserId, UUID LightKanbanId, boolean accepted);
    void notifyEditions(LightKanban LightKanban);
    void askKanban(UUID LightKanbanId);
    void getKanban(UUID LightKanbanId);
    void connectServer(LightUser user, List<LightKanban> kanbans);
    void askAddListModifiers(UUID kanbanId);
}
