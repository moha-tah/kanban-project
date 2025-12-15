package server.data;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;

public class AssociationUsersOnKanban {
    private LightKanban kanban;
    private final CopyOnWriteArrayList<LightUser> usersOnKanban;

    public AssociationUsersOnKanban(LightKanban kanban) {
        this.kanban = kanban;
        this.usersOnKanban = new CopyOnWriteArrayList<>();
    }

    public LightKanban getKanban() {
        return kanban;
    }

    public void setKanban(LightKanban kanban) {
        this.kanban = kanban;
    }

    public List<LightUser> getUsersOnKanban() {
        return usersOnKanban;
    }

    public void addUserOnKanban(LightUser newUser) {
        if (newUser != null) {
            // addIfAbsent is thread-safe and atomic in CopyOnWriteArrayList
            usersOnKanban.addIfAbsent(newUser);
        }
    }

    public void removeUserOnKanban(LightUser oldUser) {
        usersOnKanban.remove(oldUser);
    }

}