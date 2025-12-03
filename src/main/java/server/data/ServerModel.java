package server.data; // <--- CRUCIAL

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import common.dataClasses.Kanban;
import common.dataClasses.LightUser;

public class ServerModel {

    private List<LightUser> connectedUsers;
    private List<Kanban> inUseKanbans;

    public ServerModel() {
        this.connectedUsers = new ArrayList<>();
        this.inUseKanbans = new ArrayList<>();
    }

    public List<LightUser> getConnectedUsers() {
        return connectedUsers;
    }

    public void setConnectedUsers(List<LightUser> connectedUsers) {
        this.connectedUsers = connectedUsers;
    }

    public List<Kanban> getInUseKanbans() {
        return inUseKanbans;
    }

    public void setInUseKanbans(List<Kanban> inUseKanbans) {
        this.inUseKanbans = inUseKanbans;
    }

    public void removeConnectedUser(UUID userId) {
        connectedUsers.removeIf(u -> u.getId().equals(userId));
    }
}