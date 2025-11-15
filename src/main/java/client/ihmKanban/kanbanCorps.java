package client.ihmKanban;

import client.MainApp;
import client.interfaces.KanbanCallsDataClient;
import client.interfaces.KanbanCallsMain;
import client.interfaces.MainCallsDataClient;
import client.interfaces.IhmKanbanCallsComm;
import client.interfaces.IhmMainCallsComm;
import client.interfaces.DataClientCallsKanban;
import client.interfaces.MainCallsKanban;
import client.interfaces.CommClientCallsKanban;

import client.ihmKanban.impl.CommCallsKanbanImpl;
import client.ihmKanban.impl.DataCallsKanbanImpl;
import client.ihmKanban.impl.MainCallsKanbanImpl;

import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


import client.ihmKanban.controllers.ManageDisplay;


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
    private final DataCallsKanbanImpl  datCallbacks   = new DataCallsKanbanImpl(this);
    private final CommCallsKanbanImpl  commCallbacks  = new CommCallsKanbanImpl(this);
    private final MainCallsKanbanImpl mainCallbacks = new MainCallsKanbanImpl(this);

    public void launchApp() {
        users.clear(); kanbans.clear(); me = null;
    }

    // Exposition des callbacks (pour câblage)
    public DataClientCallsKanban getDATService()   { return datCallbacks; }
    public CommClientCallsKanban  getCOMMService() { return commCallbacks; }
    public MainCallsKanban      getKANBANService(){ return mainCallbacks; }

    // Injection des ports sortants
    public void setDataPort(KanbanCallsDataClient dataPort) { this.dataPort = dataPort; }
    public void setKanbanPort(KanbanCallsMain mainPort) { this.mainPort = mainPort; }
    public void setCommPort(IhmKanbanCallsComm commPort)    { this.commPort = commPort; }

    public KanbanCallsDataClient getDataPort() { return dataPort; }
    public KanbanCallsMain getKanbanPort()   { return mainPort; }
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

    public void addKanbans(List<LightKanban> list) { for (var k : list) addOrReplaceKanban(k); }

    public void addUser(LightUser u) {
        users.removeIf(x -> x.getId().equals(u.getId())); users.add(u);
    }

    public void addUsers(List<LightUser> list) { for (var u : list) addUser(u); }

    public void updateAllKanbansForUser(UUID userId) {
        System.out.println("[MainCore] updateAllKanbansForUser: " + userId);
    }

    public void launchMainWindow(Stage stage, LightKanban kanban) {
        try {
            final String first = "/displayKanban.fxml";
            URL url = getClass().getResource(first);
            if (url == null) throw new IllegalStateException("FXML introuvable: " + first);

            Parent root = FXMLLoader.load(url);
            Scene scene = new Scene(root, 1280, 720);

            URL css = getClass().getResource("/styles.css");
            if (css != null) scene.getStylesheets().add(css.toExternalForm());

            stage.setTitle("Kanban");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.err.println("Error while launching main window: " + e.getMessage());
            throw new RuntimeException("Impossible d’ouvrir la fenêtre Login", e);
        }
    }


    private void loadScene(String fxmlPath, String title) {
        try {
            URL fxmlUrl = MainApp.class.getResource(fxmlPath);
            if (fxmlUrl == null) {
                System.err.println("FXML introuvable : " + fxmlPath);
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
            System.err.println("Error while launching main window: " + e.getMessage());
        }
    }
    /* ce qui est fait chez main s'en inspirée pour la suite 
    public void showLoginView()  { loadScene("/login.fxml",  "Login"); }
    public void showSignupView() { loadScene("/signup.fxml", "Sign up"); }
    public void showHomeView()   { loadScene("/home.fxml",   "Home"); }
    public void showLandingView() { loadScene("/landing.fxml", "Welcome");}
    
     */


}
