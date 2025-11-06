package client.src.interfaces;

import common.src.dataClasses.LightKanban;
import common.src.dataClasses.LightUser;

import java.util.UUID;

public interface KanbanCallsMain {
    void closingKanbanToServer(LightKanban kanban, LightUser user);
}
