package client.interfaces;
import common.dataClasses.Kanban;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;
import java.util.List;

public interface IhmMainCallsComm {
    void logout(LightUser LightUserId);
    void askListModifiers(LightUser LightUserId);
    void sendPermissionRequest(LightUser LightUserId, LightKanban LightKanbanId);
    void sendPermissionResponse(LightUser LightUserId, LightKanban LightKanbanId, boolean accepted);
    void sendNewKanban(Kanban kanban);
    boolean connect(String host, int port);
    void connectionRequest(LightUser LightUser, List<LightKanban> listKanbans);
    void notifyDecision(LightUser LightUserId, LightKanban LightKanbanId, boolean accepted);
    void notifyEditions(LightKanban LightKanban);
    void askKanban(LightKanban LightKanbanId);
    void connectServer(LightUser user, List<LightKanban> kanbans);
    void askAddListModifiers(LightUser userId, LightKanban kanbanId);
    void getKanban(LightKanban LightKanbanId, LightUser LightUserId);
}
