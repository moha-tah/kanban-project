package client.interfaces;

import common.dataClasses.Kanban;
import common.dataClasses.Snapshot;
import java.util.List;
import java.util.UUID;

public interface KanbanCallsDataClient {
    void saveSnapshot(Kanban kanban);
    List<Snapshot> getListSnapshot();
    Snapshot getSnapshot(Snapshot snap);
    void deleteSnapshot(Snapshot snap);
    void getModified(UUID modificationID, UUID kanbanID);
}