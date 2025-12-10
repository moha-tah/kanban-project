package client.ihmMain;

import client.MainApp;
import client.interfaces.MainCallsDataClient;
import client.interfaces.MainCallsKanban;
import client.interfaces.IhmMainCallsComm;

import client.interfaces.DataClientCallsMain;
import client.interfaces.KanbanCallsMain;
import client.interfaces.CommClientCallsMain;

import client.ihmMain.impl.dataCallsMainImpl;
import client.ihmMain.controllers.HomeViewController;
import client.ihmMain.controllers.KanbanCardController;
import client.ihmMain.impl.commCallsMainImpl;
import client.ihmMain.impl.kanbanCallsMainImpl;
import common.dataClasses.Kanban;
import common.dataClasses.LightKanban;
import client.data.DataClientProvider;
import client.data.MainCallsDataImplementation;
import common.dataClasses.LightUser;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import client.ihmMain.controllers.ProfileDistantController;


/**
 * Coeur IHM : orchestre les appels entre la UI et les couches DATA/COMM/KANBAN.
 */
public class MainCore {

    
    public static final Logger LOGGER = Logger.getLogger("MainCorps");

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

    public void launchApp() {
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

    public void addOrReplaceKanban(LightKanban k) {
        kanbans.removeIf(x -> x.getId().equals(k.getId()));
        kanbans.add(k);
        System.out.println("[MainCore] Kanban added/updated: " + k.getTitle() + " (total=" + kanbans.size() + ")");
    }

    public void addKanbans(List<LightKanban> list) {
        if (list == null) return;
        for (LightKanban k : list) addOrReplaceKanban(k);
        System.out.println("[MainCore] Bulk add kanbans, now total=" + kanbans.size());
    }

    public void addUser(LightUser u) {
        users.removeIf(x -> x.getId().equals(u.getId())); users.add(u);
    }

    public void addUsers(List<LightUser> list) { for (var u : list) addUser(u); }

    public void updateAllKanbansForUser(LightUser userId) {
        System.out.println("[MainCore] updateAllKanbansForUser: " + userId);
    }

    public void viewKanban(UUID kanbanId) {
        System.out.println("[MainCore] Opening kanban " + kanbanId);

        if (commPort == null) {
            System.err.println("[MainCore] ERROR: commPort is null");
            return;
        }
        
        if (me == null) {
            System.err.println("[MainCore] ERROR: user not logged in");
            return;
        }

        // Rechercher le LightKanban dans la liste
        LightKanban light = null;
        for (LightKanban lk : kanbans) {
            if (lk.getId().equals(kanbanId)) {
                light = lk;
                break;
            }
        }

        if (light != null) {
            // Demander le Kanban complet via COMM
            System.out.println("[MainCore] Requesting Kanban via COMM: " + light.getTitle());
            commPort.getKanban(light, me);
        } else {
            System.err.println("[MainCore] Kanban non trouvé (ID: " + kanbanId + ")");
        }
    }

    public void replaceUsers(List<LightUser> newUsers) {
        users.clear();
        if (newUsers != null) {
            for (LightUser u : newUsers) {
                if (u != null) {
                    users.add(u);
                }
            }
        }
    }

    public void replaceKanbans(List<LightKanban> newKanbans) {
        kanbans.clear();
        if (newKanbans != null) {
            for (LightKanban k : newKanbans) {
                if (k != null) {
                    kanbans.add(k);
                }
            }
        }
    }

    public LightUser searchUserByUsername(String username) {
        for (LightUser u : users) {
            if (u.getUsername().equalsIgnoreCase(username)) {
                return u;
            }
        }
        return null;
    }

    public DataClientProvider getDataClientProvider() {
        if (dataPort instanceof MainCallsDataImplementation mainCallsDataImplementation) {
            return mainCallsDataImplementation.getProvider();
        }
        System.err.println("[MainCore] dataPort n'est pas une instance de MainCallsDataImplementation");
        return null;
    }

    /**
     * Retourne la liste des Kanbans disponibles
     */
    public List<LightKanban> getAvailableLightKanbans() {
        return new ArrayList<>(kanbans);
    }

    public void launchMainWindow(Stage stage) {
        try {

            
            
            final String first = "/landing.fxml";
            URL url = MainApp.class.getResource(first);
            if (url == null) throw new IllegalStateException("FXML introuvable: " + first);

            Parent root = FXMLLoader.load(url);
            Scene scene = new Scene(root, 1280, 720);

            URL css = MainApp.class.getResource("/styles.css");
            if (css != null) scene.getStylesheets().add(css.toExternalForm());

            stage.setTitle("Login");
            stage.setScene(scene);

            stage.show();
            System.out.println("[MainCore] Launched main window with FXML: " + first);

        } catch (IOException | IllegalStateException e) {
            System.err.println("Error while launching main window: " + e.getMessage());
            throw new RuntimeException("Impossible d’ouvrir la fenêtre Login", e);
        }
    }

    private void loadScene(String fxmlPath, String title) {
        try {
            URL fxmlUrl = MainApp.class.getResource(fxmlPath);
            if (fxmlUrl == null) {
                LOGGER.info("FXML introuvable");
                return;
            }

            Parent root = FXMLLoader.load(fxmlUrl);
            Scene scene = new Scene(root, 1280, 720);

            URL cssUrl = MainApp.class.getResource("/styles.css");
            if (cssUrl != null) {
                scene.getStylesheets().setAll(cssUrl.toExternalForm());
            }

            Stage stage = (Stage) Window.getWindows().stream()
                    .filter(Window::isShowing)
                    .findFirst()
                    .orElse(new Stage());

            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.INFO, "Error while  launching main window: {0}", e.getMessage());
        }
    }

  

    public void requestAccessToKanban(Kanban kanban) {
        if (kanban == null || kanban.getId() == null) {
            System.err.println("[MainCore] requestAccessToKanban: kanban invalide");
            return;
        }
        if (me == null) {
            System.err.println("[MainCore] ERROR: utilisateur non connecté");
            return;
        }
        if (commPort == null) {
            System.err.println("[MainCore] ERROR: commPort est null");
            return;
        }

        System.out.println("[MainCore] sendPermissionRequest user=" + me + " kanban=" + kanban);

        // Envoi au serveur
        commPort.sendPermissionRequest(me, kanban);
    }

    public void sendPermissionResponse(LightUser requesterId, LightKanban kanbanId, boolean accepted) {
        if (commPort != null) {
            System.out.println("[MainCore] Sending permission response: " + accepted);
            commPort.sendPermissionResponse(requesterId, kanbanId, accepted);
        } else {
            System.err.println("[MainCore] ERROR: commPort is null, cannot send response.");
        }
    }

    public void onPermissionResponse(LightKanban kanban, boolean accepted) {
        Platform.runLater(() -> {
            HomeViewController.getInstance().refreshKanbansFromModel();
        });
}
    private Map<UUID, KanbanCardController> kanbanControllers = new HashMap<>();

    public void registerKanbanCardController(UUID id, KanbanCardController controller) {
        kanbanControllers.put(id, controller);
    }

    public void notifyKanbanPermission(UUID kanbanId, boolean accepted) {
        KanbanCardController controller = kanbanControllers.get(kanbanId);
        if (controller != null) {
            controller.updatePermissionStatus(accepted);
        }
    }

    public void showLoginView()  { loadScene("/login.fxml",  "Login"); }
    public void showSignupView() { loadScene("/signup.fxml", "Sign up"); }
    public void showHomeView()   { loadScene("/home.fxml",   "Home"); }
    public void showLandingView() { loadScene("/landing.fxml", "Welcome");}
    public void showEditProfileView() { loadScene("/editProfile.fxml", "EditProfile");}
    public void showProfileView() { loadScene("/profile.fxml", "Profile");}
}
