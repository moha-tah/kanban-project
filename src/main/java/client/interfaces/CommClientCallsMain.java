package client.interfaces;
import common.dataClasses.LightUser;
import common.dataClasses.LightKanban;
import common.dataClasses.User;

import java.util.List;

public interface CommClientCallsMain {
    void displayDecision(LightUser user, LightKanban kanban, boolean decision);
    void displayPermissionRequest(LightUser user, LightKanban kanban);
    void connectionAccepted(LightUser user, List<LightKanban> kanban);
    void addUserToList(LightUser user, List<LightKanban> kanban);
    void displayDistantProfile(User requestedUser);
}
