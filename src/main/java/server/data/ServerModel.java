package server.data;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import common.dataClasses.Kanban;
import common.dataClasses.LightUser;
import common.dataClasses.User;

public class ServerModel {

    // Thread-safe lists to prevent ConcurrentModificationException
    private List<LightUser> connectedUsers;
    private List<Kanban> inUseKanbans;

    // Full user cache
    private Map<UUID, User> connectedUsersFull;

    public ServerModel() {
        this.connectedUsers = new CopyOnWriteArrayList<>();
        this.inUseKanbans = new CopyOnWriteArrayList<>();
        this.connectedUsersFull = new ConcurrentHashMap<>();
    }

    public List<LightUser> getConnectedUsers() {
        return connectedUsers;
    }

    public void setConnectedUsers(List<LightUser> connectedUsers) {
        if (connectedUsers != null) {
            this.connectedUsers = new CopyOnWriteArrayList<>(connectedUsers);
        } else {
            this.connectedUsers.clear();
        }
    }

    public List<Kanban> getInUseKanbans() {
        return inUseKanbans;
    }

    public void setInUseKanbans(List<Kanban> inUseKanbans) {
        if (inUseKanbans != null) {
            this.inUseKanbans = new CopyOnWriteArrayList<>(inUseKanbans);
        } else {
            this.inUseKanbans.clear();
        }
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
        if (connectedUsersFull == null) connectedUsersFull = new ConcurrentHashMap<>();
        connectedUsersFull.put(user.getId(), user);
    }
}