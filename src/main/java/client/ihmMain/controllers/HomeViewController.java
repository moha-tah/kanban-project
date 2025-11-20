package client.ihmMain.controllers;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.logging.Logger;
import java.util.logging.Level;

import common.dataClasses.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class HomeViewController {

    @FXML private HBox createdKanbansContainer;
    @FXML private HBox participateKanbansContainer;
    @FXML private HBox availableKanbansContainer;
    @FXML private TextField searchField;
    @FXML private Button createKanbanButton;
    @FXML private Pane usersBar;
    @FXML private Pane notifPanel;
    @FXML private VBox notifContainer;

    private boolean notifVisible = false;

    // Singleton instance
    private static HomeViewController instance;

    public static HomeViewController getInstance() {
        return instance;
    }

    private static final Logger LOGGER = Logger.getLogger(HomeViewController.class.getName());

    @FXML
    private void initialize() {
        instance = this;
        LOGGER.info("🏠 HomeView loaded!");

        // Charger users.fxml
        FXMLLoader usersLoader = new FXMLLoader(getClass().getResource("/users.fxml"));
        try {
            Node usersNode = usersLoader.load();
            usersBar.getChildren().setAll(Collections.singletonList(usersNode));
            UsersController usersController = usersLoader.getController();
            usersController.loadDummyUsers();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "❌ Erreur lors du chargement de users.fxml", e);
        }

        loadDummyKanbans();
    }

    /**
     * Méthode statique pour gérer les notifications depuis l’extérieur
     */
    public static void handleNotif() {
        if (instance != null) {
            instance.toggleNotif();
        } else {
            LOGGER.warning("⚠️ HomeViewController instance is null. Cannot handle notifications.");
        }
    }

    private void toggleNotif() {
        notifVisible = !notifVisible;
        notifPanel.setVisible(notifVisible);
        notifPanel.setMouseTransparent(!notifVisible);

        if (notifVisible) {
            notifPanel.toFront();
            LOGGER.info("📨 Ouverture du panneau de notifications");
            loadNotifications();
        } else {
            LOGGER.info("📪 Fermeture du panneau de notifications");
        }
    }

    private void loadNotifications() {
        notifContainer.getChildren().clear();
        addNotification("Invitation à rejoindre Projet Alpha");
        addNotification("Chloe a commenté votre tâche");
        addNotification("Nouvelle mise à jour du Kanban Delta");
    }

    private void addNotification(String message) {
        HBox box = new HBox();
        box.setSpacing(10);
        box.setStyle("-fx-background-color: #f2f2f2; -fx-padding: 10; -fx-background-radius: 8;");

        Label msg = new Label(message);
        msg.setStyle("-fx-font-size: 14;");

        box.getChildren().add(msg);
        notifContainer.getChildren().add(box);
    }

    private void loadDummyKanbans() {
        LOGGER.info("📋 Loading dummy Kanban cards...");

        User alice = new User("aaalice", "Alice", "Biden", LocalDate.of(1990, 5, 15));
        User chloe = new User("ccchloe", "Chloe", "Smith", LocalDate.of(1988, 8, 22));
        User eve = new User("eeve", "Eve", "Davis", LocalDate.of(1992, 12, 1));

        addKanban(createdKanbansContainer, "Projet Alpha", alice, 5, "public", "#72e379");
        addKanban(createdKanbansContainer, "Projet Beta", alice, 4, "private", "#72e379");
        addKanban(participateKanbansContainer, "Projet Gamma", chloe, 3, "public", "#f79a3e");
        addKanban(availableKanbansContainer, "Projet Delta", eve, 4, "private", "#d16ef5");
    }

    private void addKanban(HBox container, String title, User creator, int columns, String visibility, String color) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/kanban_card.fxml"));
            Node card = loader.load();
            KanbanCardController controller = loader.getController();
            controller.setKanbanData(title, creator, columns, visibility, color);
            container.getChildren().add(card);
            LOGGER.info("✅ Added Kanban card: " + title);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "❌ Erreur lors du chargement de kanban_card.fxml", e);
        }
    }

    @FXML
    private void handleCreateKanban() throws IOException {
        switchScene("/create_kanban.fxml", "Créer un Kanban", createKanbanButton);
    }

    private void switchScene(String fxmlPath, String title, Node triggerNode) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();
        Stage stage = (Stage) triggerNode.getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(new Scene(root, 1280, 720));
        LOGGER.info("🔁 Scene switched to: " + title);
    }
}
