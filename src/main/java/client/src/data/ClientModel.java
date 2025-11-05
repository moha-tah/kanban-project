package src.main.java.client.src.data;
import java.util.List;
import src.main.java.common.src.dataClasses.Kanban;
import src.main.java.common.src.dataClasses.LightKanban;
import src.main.java.common.src.dataClasses.LightUser;
import src.main.java.common.src.dataClasses.User;

public class ClientModel {
    private Kanban currentKanban;
    private List<LightKanban> myLightKanbans;
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

    public List<LightKanban> getMyLightKanbans() {
        return myLightKanbans;
    }

    public void setMyLightKanbans(List<LightKanban> myLightKanbans) {
        this.myLightKanbans = myLightKanbans;
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

}
