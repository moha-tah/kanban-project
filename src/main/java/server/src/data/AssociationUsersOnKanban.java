package server.src.data;
import java.util.List;

import src.main.java.common.src.dataClasses.LightKanban;
import src.main.java.common.src.dataClasses.LightUser;

public class AssociationUsersOnKanban {
    private LightKanban kanban;
    private List<LightUser> usersOnKanban;

    public AssociationUsersOnKanban(LightKanban kanban) {
        this.kanban = kanban;
        this.usersOnKanban = new List<>();
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
        if (newUser != null && !usersOnKanban.contains(newUser)) {
            usersOnKanban.add(newUser);
        }
    }

    public void removeUserOnKanban(LightUser oldUser) {
        usersOnKanban.remove(oldUser);
    }

}