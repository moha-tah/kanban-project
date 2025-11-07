package client.interfaces;
import common.dataClasses.LightUser;
import common.dataClasses.LightKanban;

public interface CommClientCallsMain {
    void displayDecision(LightUser user, LightKanban kanban, boolean decision);
    void displayPermissionRequest(LightUser user, LightKanban kanban);
}
