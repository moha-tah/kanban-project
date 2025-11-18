package client.ihmMain;

import client.MainApp;
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

    public void updateAllKanbansForUser(UUID userId) {
        System.out.println("[MainCore] updateAllKanbansForUser: " + userId);
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

    public void showLoginView()  { loadScene("/login.fxml",  "Login"); }
    public void showSignupView() { loadScene("/signup.fxml", "Sign up"); }
    public void showHomeView()   { loadScene("/home.fxml",   "Home"); }
    public void showLandingView() { loadScene("/landing.fxml", "Welcome");}
}

