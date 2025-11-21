package server.data;
import java.util.ArrayList;
import java.util.List;
import common.dataClasses.Access;

import common.dataClasses.Kanban;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;
import common.dataClasses.Role;


public class ServerModel {
    private List<Kanban> inUseKanbans;
    private List<LightUser> connectedUsers;
    private List<AssociationUsersOnKanban> usersOnKanbans;

    public ServerModel() {
        this.inUseKanbans = new ArrayList<Kanban>();
        this.connectedUsers = new ArrayList<LightUser>();
        this.usersOnKanbans = new ArrayList<AssociationUsersOnKanban>();
    }

    public List<LightKanban> getInUseLightKanbans() {
        ArrayList<LightKanban> myList = new  ArrayList<LightKanban>();
        for (Kanban l:inUseKanbans) {
            myList.add(l.getLightKanban());
        }

        return myList;
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
    
    public boolean addAuthorizedUser(LightKanban kanban, LightUser user) {

        if (kanban == null || user == null) {
        return false;
        }
        if (kanban.getAccessList() == null) {
        kanban.setAccessList(new ArrayList<>());
        }
        List<Access> accessList = kanban.getAccessList();
        for (Access a : accessList) {
            if (a.getUser().getId().equals(user.getId())) {
                return true; 
            }
        }
        Access newAccess = new Access(user, Role.VIEWER);
        accessList.add(newAccess);
        return true;
    }

}

