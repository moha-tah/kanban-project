package client.src;

import client.src.ihmMain.MainCore;
import client.src.interfaces.MainCallsDataClient;
import client.src.interfaces.MainCallsKanban;
import client.src.interfaces.IhmMainCallsComm;

import common.src.dataClasses.LightKanban;
import common.src.dataClasses.LightUser;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/** MainApp minimaliste pour tester l'écran Login et le flux de base. */
public class MainApp extends Application {

    private static MainCore CORE;

    public static MainCore getCore() { return CORE; }

    @Override
    public void start(Stage stage) throws Exception {
        CORE = new MainCore();
        wireMocks();         // mocks très simples pour tester sans serveur
        CORE.initialize();

        Parent root = FXMLLoader.load(require("/login.fxml"));
        Scene scene = new Scene(root, 1280, 720);

        URL css = MainApp.class.getResource("/styles.css");
        if (css != null) scene.getStylesheets().add(css.toExternalForm());

        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }

    public static void loadScene(String fxmlPath, String title) throws Exception {
        Parent root = FXMLLoader.load(require(fxmlPath));
        Scene scene = new Scene(root, 1280, 720);

        URL css = MainApp.class.getResource("/styles.css");
        if (css != null) scene.getStylesheets().add(css.toExternalForm());

        Stage stage = (Stage) Window.getWindows().stream()
                .filter(Window::isShowing)
                .findFirst()
                .orElse(new Stage());

        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }

    private static URL require(String path) {
        return Objects.requireNonNull(MainApp.class.getResource(path),
                "FXML introuvable: " + path);
    }

    /** Mocks minimalistes pour valider le flux Login -> Data -> Comm. */
    private void wireMocks() {
        // --- DATA mock ---
        CORE.setDataPort(new MainCallsDataClient() {
            @Override public boolean authentify(String username, String password) {
                return !username.isBlank() && password != null && password.length() >= 6;
            }
            @Override public LightUser getMyLightUser() { return new LightUser("demoUser"); }
            @Override public List<LightKanban> getMyListLightKanbans() { return List.of(); }

            // non utilisés dans ce test
            @Override public void saveUser() {}
            @Override public void exportProfile(UUID lightUserId, String path) {}
            @Override public void importMyProfile(String path) {}
            @Override public void sendCreateProfile(List<?> profileDetails) {}
        });

        CORE.setCommPort(new IhmMainCallsComm() {
            @Override
            public void logout(UUID lightUserId) {
                System.out.println("[MOCK COMM] logout user " + lightUserId);
            }

            @Override
            public void askListModifiers(UUID lightUserId) {
                System.out.println("[MOCK COMM] askListModifiers for " + lightUserId);
            }

            @Override
            public void sendPermissionRequest(UUID lightUserId, UUID lightKanbanId) {
                System.out.println("[MOCK COMM] sendPermissionRequest: user=" + lightUserId + ", kanban=" + lightKanbanId);
            }

            @Override
            public void sendPermissionResponse(UUID lightUserId, UUID lightKanbanId, boolean accepted) {
                System.out.println("[MOCK COMM] sendPermissionResponse: user=" + lightUserId + ", kanban=" + lightKanbanId + ", accepted=" + accepted);
            }

            @Override
            public void connectToServer(UUID lightUserId, List<LightKanban> listKanbans) {
                System.out.println("[MOCK COMM] connectToServer: user=" + lightUserId + ", kanbans=" + listKanbans.size());
            }

            @Override
            public void connectionRequest(LightUser lightUser, List<LightKanban> listKanbans) {
                System.out.println("[MOCK COMM] connectionRequest: " + lightUser.getUsername());
            }

            @Override
            public void notifyDecision(UUID lightUserId, UUID lightKanbanId, boolean accepted) {
                System.out.println("[MOCK COMM] notifyDecision: user=" + lightUserId + ", kanban=" + lightKanbanId + ", accepted=" + accepted);
            }

            @Override
            public void notifyEditions(LightKanban lightKanban) {
                System.out.println("[MOCK COMM] notifyEditions on " + lightKanban.getTitle());
            }

            @Override
            public void askKanban(UUID lightKanbanId) {
                System.out.println("[MOCK COMM] askKanban: " + lightKanbanId);
            }

            @Override
            public void getKanban(UUID lightKanbanId) {
                System.out.println("[MOCK COMM] getKanban: " + lightKanbanId);
            }

            @Override
            public void connectServer(LightUser user, List<LightKanban> kanbans) {
                System.out.println("[MOCK COMM] connectServer (old) -> " + user.getUsername());
            }

            @Override
            public void askAddListModifiers(UUID kanbanId) {
                System.out.println("[MOCK COMM] askAddListModifiers for kanban " + kanbanId);
            }
        });

        // --- KANBAN mock (pas utilisé ici) ---
        CORE.setKanbanPort(new MainCallsKanban() {
            @Override public void openCreateFrom() {}
            @Override public void displaySnapshotList() {}
            @Override public void openCreateForm() {}
        });
    }

    public static void main(String[] args) { launch(args); }
}