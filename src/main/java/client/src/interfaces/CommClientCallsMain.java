package client.src.interfaces;
import common.src.dataClasses.LightUser;
import common.src.dataClasses.LightKanban;

import java.util.UUID;

public interface CommClientCallsMain {
    void displayDecision(LightUser user, LightKanban kanban, boolean decision);
    void displayPermissionRequest(LightUser user, LightKanban kanban);
}
