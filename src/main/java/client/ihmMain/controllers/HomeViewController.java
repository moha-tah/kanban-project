package client.ihmMain.controllers;

import client.MainApp;
import client.ihmMain.MainCore;
import client.ihmMain.controllers.CreateKanbanController;
import common.dataClasses.Kanban;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;

public class HomeViewController {

    @FXML private VBox usersContainer;
    @FXML private HBox createdKanbansContainer;
    @FXML private HBox participateKanbansContainer;
    @FXML private HBox availableKanbansContainer;
    @FXML private TextField searchField;
    @FXML private Button createKanbanButton;
    @FXML private ImageView profilePic;

    @FXML
    private void initialize() {
        System.out.println("HomeView loaded !");
        // Render kanbans from current model snapshot
        renderFromModel();
    }

    private void renderFromModel() {
        try {
            MainCore core = MainApp.getCore();
            if (core == null) return;

            // Clear existing cards
            createdKanbansContainer.getChildren().clear();

            LightUser me = core.getMe();
            String creatorName = (me != null) ? me.getUsername() : "me";
            var snapshot = core.getKanbansSnapshot();
            System.out.println("[Home] Rendering " + snapshot.size() + " kanbans to UI...");
            for (LightKanban lk : snapshot) {
                int columns = 0;
                if (lk instanceof Kanban k) {
                    columns = k.getAllColumns().size();
                }
                addKanban(createdKanbansContainer, lk.getTitle(), creatorName, columns, "Active", "#4D96FF");
            }
        } catch (Exception e) {
            System.err.println("Error rendering kanbans: " + e.getMessage());
        }
    }

    private void addKanban(HBox container, String title, String creator, int columns, String status, String color) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/kanban_card.fxml"));
            Node card = loader.load();

            KanbanCardController controller = loader.getController();
            controller.setKanbanData(title, creator, columns, status, color);

            container.getChildren().add(card);
            System.out.println("Added Kanban card: " + title);
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de kanban_card.fxml");
            e.printStackTrace();
        }
    }

    // ===============================================================
    // CHARGEMENT DES USER CARDS
    // ===============================================================

    private void addUser(String username, String imagePath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/user_card.fxml"));
            Node userCard = loader.load();

            UserCardController controller = loader.getController();
            controller.setUserData(username, imagePath);

            usersContainer.getChildren().add(userCard);
            System.out.println("Added user: " + username);
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de user_card.fxml");
            e.printStackTrace();
        }
    }

    // ===============================================================
    //  NAVIGATION
    // ===============================================================
    @FXML
    private void handleProfileClick() throws IOException {
        switchScene("/profile.fxml", "Mon Profil", profilePic);
    }

    @FXML
    private void handleCreateKanban() throws IOException {
        openPopup("/createKanban.fxml", "Créer un Kanban");
    }

    @FXML
    private void handleHome() {
        System.out.println("Déjà sur la page d'accueil.");
    }

    @FXML
    private void handleNotif() {
        System.out.println("Notifications — à implémenter plus tard.");
    }

    @FXML
    private void handleLogout() {
        System.out.println("Déconnexion — à implémenter plus tard.");
    }

    private void openPopup(String fxmlPath, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();

        // Inject MainCore into popup controller if applicable
        Object ctrl = loader.getController();
        if (ctrl instanceof CreateKanbanController) {
            CreateKanbanController ckc = (CreateKanbanController) ctrl;
            ckc.setMainCore(MainApp.getCore());
            System.out.println("[Home] Wired CreateKanbanController with MainCore");
        }

        Stage popup = new Stage();
        popup.setTitle(title);
        popup.setScene(new Scene(root));
        popup.setResizable(false);
        popup.initOwner(createKanbanButton.getScene().getWindow());
        popup.setOnHidden(e -> {
            // Re-render after popup closes in case list changed
            System.out.println("[Home] Popup closed, refreshing kanban cards...");
            renderFromModel();
        });
        popup.show();
    }

    // ===============================================================
    //  MÉTHODE UTILITAIRE POUR CHANGER DE SCÈNE
    // ===============================================================
    private void switchScene(String fxmlPath, String title, Node triggerNode) throws IOException {
        java.net.URL resource = getClass().getResource(fxmlPath);
        if (resource == null) {
            java.util.logging.Logger.getLogger(HomeViewController.class.getName())
                    .log(java.util.logging.Level.SEVERE, "HomeViewController: FXML resource not found: " + fxmlPath);
            throw new IOException("FXML resource not found: " + fxmlPath);
        }
        FXMLLoader loader = new FXMLLoader(resource);
        Parent root = loader.load();

        Stage stage = (Stage) triggerNode.getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(new Scene(root, 1280, 720));
    }
}
