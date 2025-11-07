package client.interfaces;

import java.util.UUID;

public interface KanbanCallsMain {
    void closingKanbanToServer(UUID kanbanId, UUID userId);
}
