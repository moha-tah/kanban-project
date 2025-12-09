package server.data; // <--- CRUCIAL

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import common.dataClasses.Kanban;
import common.dataClasses.LightUser;
import common.dataClasses.User;

public class ServerModel {

    private List<LightUser> connectedUsers;
    private List<Kanban> inUseKanbans;
    // Full user cache to ensure uniform profile access for local/distant
    private Map<UUID, User> connectedUsersFull;

    public ServerModel() {
        this.connectedUsers = new ArrayList<>();
        this.inUseKanbans = new ArrayList<>();
        this.connectedUsersFull = new HashMap<>();
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
        if (connectedUsersFull != null) {
            connectedUsersFull.remove(userId);
        }
    }

    // ----------------------------
    // Full user cache accessors
    // ----------------------------
    public Map<UUID, User> getConnectedUsersFull() {
        return connectedUsersFull;
    }

    public void setConnectedUsersFull(Map<UUID, User> connectedUsersFull) {
        this.connectedUsersFull = connectedUsersFull;
    }

    public void putFullUser(User user) {
        if (user == null || user.getId() == null) return;
        if (connectedUsersFull == null) connectedUsersFull = new HashMap<>();
        connectedUsersFull.put(user.getId(), user);
    }
}