package client.ihmKanban;

import client.interfaces.KanbanCallsDataClient;
import client.interfaces.KanbanCallsMain;
import client.interfaces.IhmKanbanCallsComm;
import client.interfaces.DataClientCallsKanban;
import client.interfaces.MainCallsKanban;
import client.interfaces.CommClientCallsKanban;

import client.ihmKanban.impl.CommClientCallsKanbanImpl;
import client.ihmKanban.impl.DataClientCallsKanbanImpl;
import client.ihmKanban.impl.MainCallsKanbanImpl;
import common.dataClasses.Kanban;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import client.ihmKanban.controllers.ManageDisplay;

import java.util.logging.Logger;


/**
 * Coeur IHM : orchestre les appels entre la UI et les couches DATA/COMM/KANBAN.
 */
public class kanbanCorps {

    // ---- Ports sortants (UI/Main -> autres couches) ----
    private KanbanCallsDataClient dataPort;
    private KanbanCallsMain    mainPort;
    private IhmKanbanCallsComm   commPort;       // <— ajouté pour suivre le diagramme

    // ---- État IHM ----
    private LightUser me;
    private final List<LightUser>   users   = new ArrayList<>();
    private final List<LightKanban> kanbans = new ArrayList<>();


    // ---- Impl des callbacks (autres couches -> Kanban) ----
    private final DataClientCallsKanbanImpl  datCallbacks   = new DataClientCallsKanbanImpl(this);
    private final CommClientCallsKanbanImpl  commCallbacks  = new CommClientCallsKanbanImpl(this);
    private final MainCallsKanbanImpl mainCallbacks = new MainCallsKanbanImpl(this);

    public void launchApp() {
        users.clear(); kanbans.clear(); me = null;
    }

    // Exposition des callbacks (pour câblage)
    public DataClientCallsKanban getDATService()   { return datCallbacks; }
    public CommClientCallsKanban  getCOMMService() { return commCallbacks; }
    public MainCallsKanban      getMAINService(){ return mainCallbacks; }

    // Injection des ports sortants
    public void setDataPort(KanbanCallsDataClient dataPort) { this.dataPort = dataPort; }
    public void setMainPort(KanbanCallsMain mainPort) { this.mainPort = mainPort; }
    public void setCommPort(IhmKanbanCallsComm commPort)    { this.commPort = commPort; }

    public KanbanCallsDataClient getDataPort() { return dataPort; }
    public KanbanCallsMain getMainPort()   { return mainPort; }
    public IhmKanbanCallsComm getCommPort()    { return commPort; }

    // Accès état
    public void setMe(LightUser me) { this.me = me; }
    public LightUser getMe()        { return me; }

    public List<LightUser> getUsersSnapshot()     { return new ArrayList<>(users); }
    public List<LightKanban> getKanbansSnapshot() { return new ArrayList<>(kanbans); }

    public void addOrReplaceKanban(LightKanban k) {
        kanbans.removeIf(x -> x.getId().equals(k.getId()));
        kanbans.add(k);
    }

    
    private final ManageDisplay manageDisplay = new ManageDisplay( this);


    public static final Logger LOGGER = Logger.getLogger("Kanban Corps");

    public void addKanbans(List<LightKanban> list) { for (var k : list) addOrReplaceKanban(k); }

    public void addUser(LightUser u) {
        users.removeIf(x -> x.getId().equals(u.getId())); users.add(u);
    }

    public void addUsers(List<LightUser> list) { for (var u : list) addUser(u); }

    public void updateAllKanbansForUser(UUID userId) {
        LOGGER.log(Level.INFO, "[MainCore] updateAllKanbansForUser: {0}", userId);
    }


    public void displayKanban(Kanban kanban)  { 
        manageDisplay.openKanbanScreen(kanban); 
    }



}
