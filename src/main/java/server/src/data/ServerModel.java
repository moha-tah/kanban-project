package src.main.java.server.src.data;
import java.util.List;
import java.util.ArrayList;
import src.main.java.common.src.dataClasses.Kanban;
import src.main.java.common.src.dataClasses.LightUser;
import src.main.java.server.src.data.AssociationUsersOnKanban;


public class ServerModel {
    private List<Kanban> inUseKanbans;
    private List<LightUser> connectedUsers;
    private List<AssociationUsersOnKanban> usersOnKanbans;

    public ServerModel() {
        this.inUseKanbans = new ArrayList<>();
        this.connectedUsers = new ArrayList<>();
        this.usersOnKanbans = new ArrayList<>();
    }

    public List<Kanban> getInUseKanbans() {
        return inUseKanbans;
    }
    public List<LightUser> getConnectedUsers() {
        return connectedUsers;
    }
    public void addInUseKanban(Kanban newKanban) {
        if (newKanban != null && !inUseKanbans.contains(newKanban)) {
            inUseKanbans.add(newKanban);
        }
    }
    public void removeInUseKanban(Kanban oldKanban) {
        inUseKanbans.remove(oldKanban);
    }
     public List<AssociationUsersOnKanban> getUsersOnKanbans() {
        return usersOnKanbans;
    }
     public void addUserOnKanban(AssociationUsersOnKanban newAssoc) {
        if (newAssoc != null && !usersOnKanbans.contains(newAssoc)) {
            usersOnKanbans.add(newAssoc);
        }
    }

    public void removeUserOnKanban(AssociationUsersOnKanban oldAssoc) {
        usersOnKanbans.remove(oldAssoc);
    }
}

