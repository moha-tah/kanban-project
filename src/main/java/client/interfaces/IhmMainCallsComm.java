package client.interfaces;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;
import java.util.List;
import java.util.UUID;

public interface IhmMainCallsComm {
    void logout(UUID LightUserId);
    void askListModifiers(UUID LightUserId);
    void sendPermissionRequest(UUID LightUserId, UUID LightKanbanId);
    void sendPermissionResponse(UUID LightUserId, UUID LightKanbanId, boolean accepted);
    boolean connect(String host, int port);
    void connectionRequest(LightUser LightUser, List<LightKanban> listKanbans);
    void notifyDecision(UUID LightUserId, UUID LightKanbanId, boolean accepted);
    void notifyEditions(LightKanban LightKanban);
    void askKanban(UUID LightKanbanId);
    void connectServer(LightUser user, List<LightKanban> kanbans);
    void askAddListModifiers(UUID userId, UUID kanbanId);
    void getKanban(LightKanban LightKanbanId, LightUser LightUserId);
}
