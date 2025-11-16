package client.ihmMain.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;

import common.dataClasses.User;

public class HomeViewController {

    @FXML private VBox usersContainer;
    @FXML private HBox createdKanbansContainer;
    @FXML private HBox participateKanbansContainer;
    @FXML private HBox availableKanbansContainer;
    @FXML private TextField searchField;
    @FXML private Button createKanbanButton;
    @FXML private ImageView profilePic;


    @FXML private Pane notifPanel;
    @FXML private VBox notifContainer;

    private boolean notifVisible = false;



    @FXML
    private void initialize() {
        System.out.println("🏠 HomeView loaded !");
        loadDummyKanbans();
        loadDummyUsers();
    }

    // ===============================================================
    // 🟩 CHARGEMENT DES KANBAN CARDS
    // ===============================================================
    private void loadDummyKanbans() {
        System.out.println("📋 Loading dummy Kanban cards...");

        User alice = new User("aaalice","Alice","Biden", LocalDate.of(1990, 5, 15));
        User chloe = new User("ccchloe","Chloe","Smith", LocalDate.of(1988, 8, 22));

        User eve = new User("eeve","Eve","Davis", LocalDate.of(1992, 12, 1));

        // Kanbans créés par l’utilisateur → en général publics ou privés
        addKanban(createdKanbansContainer, "Projet Alpha", alice, 5, "public", "#72e379");
        addKanban(createdKanbansContainer, "Projet Beta", alice, 4, "private", "#72e379");

        // Kanbans où l’utilisateur participe
        addKanban(participateKanbansContainer, "Projet Gamma", chloe, 3, "public", "#f79a3e");

        // Kanbans disponibles → peut être privés (demander accès)
        addKanban(availableKanbansContainer, "Projet Delta", eve, 4, "private", "#d16ef5");
    }

    private void addKanban(HBox container, String title, User creator, int columns, String visibility, String color) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/kanban_card.fxml"));
            Node card = loader.load();

            KanbanCardController controller = loader.getController();
            controller.setKanbanData(title, creator, columns, visibility, color);

            container.getChildren().add(card);
            System.out.println("✅ Added Kanban card: " + title);
        } catch (IOException e) {
            System.err.println("❌ Erreur lors du chargement de kanban_card.fxml");
            e.printStackTrace();
        }
    }

    // ===============================================================
    // 🟦 CHARGEMENT DES USER CARDS
    // ===============================================================
    private void loadDummyUsers() {
        System.out.println("👥 Loading dummy users...");

        Object[][] dummyUsers = {
                {"Jenny", "https://randomuser.me/api/portraits/women/1.jpg"},
                {"Mina", "https://randomuser.me/api/portraits/women/65.jpg"},
                {"Thomas", "https://randomuser.me/api/portraits/men/22.jpg"}
        };

        for (Object[] user : dummyUsers) {
            addUser((String) user[0], (String) user[1]);
        }
    }

    private void addUser(String username, String imagePath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/user_card.fxml"));
            Node userCard = loader.load();

            UserCardController controller = loader.getController();
            controller.setUserData(username, imagePath);

            usersContainer.getChildren().add(userCard);
            System.out.println("✅ Added user: " + username);
        } catch (IOException e) {
            System.err.println("❌ Erreur lors du chargement de user_card.fxml");
            e.printStackTrace();
        }
    }

    // ===============================================================
    // 🧭 NAVIGATION
    // ===============================================================
    @FXML
    private void handleProfileClick() throws IOException {
        switchScene("/profile.fxml", "Mon Profil", profilePic);
    }

    @FXML
    private void handleCreateKanban() throws IOException {
        switchScene("/create_kanban.fxml", "Créer un Kanban", createKanbanButton);
    }

    @FXML
    private void handleHome() {
        System.out.println("🏠 Déjà sur la page d'accueil.");
    }

    @FXML
    private void handleNotif() {
        notifVisible = !notifVisible;

        notifPanel.setVisible(notifVisible);
        notifPanel.setMouseTransparent(!notifVisible);

        if (notifVisible) {
            System.out.println("Ouverture du panneau de notifications");
            loadNotifications();
        } else {
            System.out.println("Fermeture du panneau de notifications");
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
        box.getStyleClass().add("notification-item");

        Label msg = new Label(message);
        msg.setStyle("-fx-font-size: 14;");

        box.getChildren().add(msg);

        notifContainer.getChildren().add(box);
    }




    @FXML
    private void handleLogout() {
        System.out.println("🚪 Déconnexion — à implémenter plus tard.");
    }

    // ===============================================================
    // 🔁 SWITCH DE SCÈNE UTILITAIRE
    // ===============================================================
    private void switchScene(String fxmlPath, String title, Node triggerNode) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();

        Stage stage = (Stage) triggerNode.getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(new Scene(root, 1280, 720));
    }
}
