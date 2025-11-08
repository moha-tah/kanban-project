package client.ihmMain;

import client.interfaces.MainCallsDataClient;
import client.interfaces.MainCallsKanban;
import client.interfaces.IhmMainCallsComm;

import client.interfaces.DataClientCallsMain;
import client.interfaces.KanbanCallsMain;
import client.interfaces.CommClientCallsMain;

import client.ihmMain.impl.dataCallsMainImpl;
import client.ihmMain.impl.commCallsMainImpl;
import client.ihmMain.impl.kanbanCallsMainImpl;

import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Coeur IHM : orchestre les appels entre la UI et les couches DATA/COMM/KANBAN.
 */
public class MainCore {

    // ---- Ports sortants (UI/Main -> autres couches) ----
    private MainCallsDataClient dataPort;
    private MainCallsKanban    kanbanPort;
    private IhmMainCallsComm   commPort;       // <— ajouté pour suivre le diagramme

    // ---- État IHM ----
    private LightUser me;
    private final List<LightUser>   users   = new ArrayList<>();
    private final List<LightKanban> kanbans = new ArrayList<>();

    // ---- Impl des callbacks (autres couches -> Main) ----
    private final dataCallsMainImpl  datCallbacks   = new dataCallsMainImpl(this);
    private final commCallsMainImpl  commCallbacks  = new commCallsMainImpl(this);
    private final kanbanCallsMainImpl kanbanCallbacks = new kanbanCallsMainImpl(this);

    public void initialize() {
        users.clear(); kanbans.clear(); me = null;
    }

    // Exposition des callbacks (pour câblage)
    public DataClientCallsMain getDATService()   { return datCallbacks; }
    public CommClientCallsMain  getCOMMService() { return commCallbacks; }
    public KanbanCallsMain      getKANBANService(){ return kanbanCallbacks; }

    // Injection des ports sortants
    public void setDataPort(MainCallsDataClient dataPort) { this.dataPort = dataPort; }
    public void setKanbanPort(MainCallsKanban kanbanPort) { this.kanbanPort = kanbanPort; }
    public void setCommPort(IhmMainCallsComm commPort)    { this.commPort = commPort; }

    public MainCallsDataClient getDataPort() { return dataPort; }
    public MainCallsKanban getKanbanPort()   { return kanbanPort; }
    public IhmMainCallsComm getCommPort()    { return commPort; }

    // Accès état
    public void setMe(LightUser me) { this.me = me; }
    public LightUser getMe()        { return me; }

    public List<LightUser> getUsersSnapshot()     { return new ArrayList<>(users); }
    public List<LightKanban> getKanbansSnapshot() { return new ArrayList<>(kanbans); }

    // Helpers internes
    public void addOrReplaceKanban(LightKanban k) {
        kanbans.removeIf(x -> x.getId().equals(k.getId()));
        kanbans.add(k);
    }
    public void addKanbans(List<LightKanban> list) { for (var k : list) addOrReplaceKanban(k); }
    public void addUser(LightUser u) {
        users.removeIf(x -> x.getId().equals(u.getId())); users.add(u);
    }
    public void addUsers(List<LightUser> list) { for (var u : list) addUser(u); }

    public void updateAllKanbansForUser(UUID userId) {
        System.out.println("[MainCore] updateAllKanbansForUser: " + userId);
    }
}
