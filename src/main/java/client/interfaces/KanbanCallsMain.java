package client.interfaces;

import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;

public interface KanbanCallsMain {
    void closingKanbanToServer(LightKanban kanban, LightUser user);
}
