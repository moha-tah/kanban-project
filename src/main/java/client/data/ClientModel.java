package client.data;
import java.util.List;
import common.dataClasses.Kanban;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;
import common.dataClasses.User;

public class ClientModel {
    private Kanban currentKanban;
    private List<LightKanban> availableLightKanbans;
    private User localUser;
    private List<LightUser> connectedUsers;

    public ClientModel() {

    }

    public Kanban getCurrentKanban() {
        return currentKanban;
    }

    public void setCurrentKanban(Kanban currentKanban) {
        this.currentKanban = currentKanban;
    }

    public List<LightKanban> getAvailableLightKanbans() {
        return availableLightKanbans;
    }

    public void setAvailableLightKanbans(List<LightKanban> availableLightKanbans) {
        this.availableLightKanbans = availableLightKanbans;
    }

    public User getLocalUser() {
        return localUser;
    }

    public void setLocalUser(User localUser) {
        this.localUser = localUser;
    }

    public List<LightUser> getConnectedUsers() {
        return connectedUsers;
    }

    public void setConnectedUsers(List<LightUser> connectedUsers) {
        this.connectedUsers = connectedUsers;
    }

    public void saveUser(LightUser currentUser) {
        throw new UnsupportedOperationException("saveUser not implemented yet");
    }
}