package server.data;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;
import common.dataClasses.Kanban;
import common.dataClasses.LightUser;
import server.data.AssociationUsersOnKanban;
import common.dataClasses.LightKanban;


public class ServerModel {
    private List<Kanban> inUseKanbans;
    private List<LightUser> connectedUsers;
    private List<AssociationUsersOnKanban> usersOnKanbans;

    public ServerModel() {
        this.inUseKanbans = new ArrayList<Kanban>();
        this.connectedUsers = new ArrayList<LightUser>();
        this.usersOnKanbans = new ArrayList<AssociationUsersOnKanban>();
    }

    public List<LightKanban> getInUseKanbans() {
        ArrayList<LightKanban> myList = new  ArrayList<LightKanban>();
        for (Kanban l:inUseKanbans) {
            myList.add(l.getLightKanban());
        }

        return myList;
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

    public boolean addAuthorizedUser(UUID kanbanId, UUID userId) {
        LightUser targetUser = null;
        for (LightUser u : connectedUsers) {
            if (u.getId().equals(userId)) {
                targetUser = u;
                break;
            }
        }
        if (targetUser == null) return false; // Utilisateur inconnu
        Kanban targetKanban = null;
        for (Kanban k : inUseKanbans) {
            if (k.getId().equals(kanbanId)) {
                targetKanban = k;
                break;
            }
        }
        if (targetKanban == null) return false; // Kanban inconnu

        AssociationUsersOnKanban assoc = null;
        for (AssociationUsersOnKanban a : usersOnKanbans) {
            if (a.getKanban().getId().equals(kanbanId)) {
                assoc = a;
                break;
            }
        }
        if (assoc == null) {
            assoc = new AssociationUsersOnKanban(targetKanban.getLightKanban());
            usersOnKanbans.add(assoc);
        }
        assoc.addUserOnKanban(targetUser);

        return true;
    }
}

