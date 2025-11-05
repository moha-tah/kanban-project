package src.main.java.client.src.interfaces;
import src.main.java.common.src.dataClasses.LightUser;
import src.main.java.common.src.dataClasses.LightKanban;

public interface CommClientCallsMain {
    void displayDecision(LightUser user, LightKanban kanban, boolean decision);
    void displayPermissionRequest(LightUser user, LightKanban kanban);
}
