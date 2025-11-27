package client.interfaces;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;

import java.util.UUID;

public interface IhmKanbanCallsComm {
    void closingKanban(LightKanban LightKanbanId, LightUser LightUserId);
}
    