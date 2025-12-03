package client.interfaces;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;
import common.dataClasses.Modification;


public interface IhmKanbanCallsComm {
    void closingKanban(LightKanban LightKanbanId, LightUser LightUserId);
    void sendRequestModification(LightUser user, Modification myModification);
}
    