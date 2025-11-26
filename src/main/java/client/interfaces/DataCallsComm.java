package client.interfaces;
import java.util.UUID;
import common.dataClasses.Kanban;
import common.dataClasses.LightKanban;
import common.dataClasses.Modification;


public interface DataCallsComm {
    void askDeleteKanban(UUID LightKanbanId, UUID LightUserId);
    void sendKanban(Kanban Kanban); 
    void saveModifiedKanban(Modification modification, LightKanban kanban);    
}

