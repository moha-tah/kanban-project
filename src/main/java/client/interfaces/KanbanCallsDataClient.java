package client.interfaces;


import java.util.List;
import java.util.UUID;

import common.dataClasses.Kanban;
import common.dataClasses.Modification;
import common.dataClasses.Snapshot;

public interface KanbanCallsDataClient {
    void saveSnapshot(Kanban kanban);
    List<Snapshot> getListSnapshot();
    Snapshot getSnapshot(Snapshot snap);
    void deleteSnapshot(Snapshot snap);
    void getModified(Modification modifi, UUID kanbanID);
    void setCurrentKanban(Kanban kanban);
    Kanban getLocalKanban();
}