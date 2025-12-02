package client.interfaces;
import common.dataClasses.Kanban;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;

public interface DataCallsComm {
    void askDeleteKanban(LightKanban LightKanbanId, LightUser LightUserId);
    void sendKanban(Kanban Kanban);   
    void addAuthorizedUser(LightKanban kanbanId, LightUser userId);
}
