package root.client.src.interfaces;
import root.common.src.dataClasses.LightUser;
import root.common.src.dataClasses.LightKanban;

public interface CommClientCallsMain {
    void displayDecision(LightUser user, LightKanban kanban, boolean decision);
    void displayPermissionRequest(LightUser user, LightKanban kanban);
}
