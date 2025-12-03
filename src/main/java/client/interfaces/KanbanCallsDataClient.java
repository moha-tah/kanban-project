package client.interfaces;

import common.dataClasses.Kanban;
import common.dataClasses.Snapshot;

import java.util.List;
import java.util.UUID;

import common.dataClasses.Modification;

public interface KanbanCallsDataClient {
    void saveSnapshot(Kanban kanban);
    List<Snapshot> getListSnapshot();
    Snapshot getSnapshot(Snapshot snap);
    void deleteSnapshot(Snapshot snap);
    void getModified(Modification modifi, UUID kanbanID);
}