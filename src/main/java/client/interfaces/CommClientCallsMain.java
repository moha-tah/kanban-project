package client.interfaces;
import common.dataClasses.LightUser;
import common.dataClasses.LightKanban;

import java.util.List;

public interface CommClientCallsMain {
    void displayDecision(LightUser user, LightKanban kanban, boolean decision);
    void displayPermissionRequest(LightUser user, LightKanban kanban);
    void connectionAccepted(LightUser user, List<LightKanban> kanban);
    void connectServer(LightUser user, List<LightKanban> kanbans);
}
