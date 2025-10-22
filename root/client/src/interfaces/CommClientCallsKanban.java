package root.client.src.interfaces;

import root.common.src.dataClasses.Kanban;
import root.common.src.dataClasses.Snapshot;
import java.util.List;
import java.util.UUID;

// CommCallsKanban
public interface CommClientCallsKanban {
    void deliverNotification(LightKanban idKanban, Modification modif);
}