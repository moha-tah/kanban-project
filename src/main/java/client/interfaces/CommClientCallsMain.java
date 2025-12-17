package client.interfaces;
import java.util.List;

import client.comm.messages.Message;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;
import common.dataClasses.User;

public interface CommClientCallsMain {
    void displayDecision(LightUser user, LightKanban kanban, boolean decision);
    void displayPermissionRequest(LightUser user, LightKanban kanban);
    void connectionAccepted(LightUser user, List<LightKanban> kanban);
    void addUserToList(LightUser user, List<LightKanban> kanban);
    void displayDistantProfile(User requestedUser);
    void handleServerConnectionLost();
    void receiveMessage(Message message);
}
