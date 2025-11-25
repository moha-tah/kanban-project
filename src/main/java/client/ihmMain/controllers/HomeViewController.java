package client.ihmMain.controllers;

import java.io.IOException;
import java.util.Collections;
import java.util.logging.Logger;
import java.util.logging.Level;

import client.MainApp;
import client.ihmMain.MainCore;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.List;

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

    private MainCore core;

    // Référence vers le UsersController chargé depuis users.fxml
    private UsersController usersController;

    // Singleton instance pour accès statique (depuis dataCallsMainImpl)
    private static HomeViewController instance;

    public static HomeViewController getInstance() {
        return instance;
    }

    private static final Logger LOGGER = Logger.getLogger(HomeViewController.class.getName());

    @FXML
    private void initialize() {
        instance = this;
        LOGGER.info("HomeView loaded!");

        core = MainApp.getCore();
        if (core == null) {
            LOGGER.severe("MainCore est null dans HomeViewController !");
        }

        // Charger users.fxml et injecter MainCore dans UsersController
        loadUsersBar();

        // Charger les Kanbans à partir du modèle (sans dummy)
        refreshKanbansFromModel();
    }

    /**
     * Charge la barre des utilisateurs (users.fxml),
     * stocke son controller, injecte MainCore et fait un premier refresh.
     */
    private void loadUsersBar() {
        if (usersBar == null) {
            LOGGER.severe("usersBar est null dans HomeViewController !");
            return;
        }

        try {
            FXMLLoader usersLoader = new FXMLLoader(getClass().getResource("/users.fxml"));
            Node usersNode = usersLoader.load();
            usersBar.getChildren().setAll(Collections.singletonList(usersNode));

            this.usersController = usersLoader.getController();
            if (usersController != null) {
                usersController.setCore(core);
                usersController.refreshUsers();
            } else {
                LOGGER.severe("UsersController est null après le chargement de users.fxml");
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement de users.fxml", e);
        }
    }

    /**
     * Appelé depuis la couche Data quand la liste des users change.
     * Recharge juste la barre des utilisateurs.
     */
    public void refreshUsersBar() {
        if (usersController == null) {
            LOGGER.warning("usersController est null dans refreshUsersBar(), rechargement de users.fxml...");
            loadUsersBar();
            return;
        }
        usersController.refreshUsers();
    }

    // ==================== NOTIFICATIONS ====================

    public static void handleNotif() {
        if (instance != null) {
            instance.toggleNotif();
        } else {
            LOGGER.warning("HomeViewController instance is null. Cannot handle notifications.");
        }
    }

    private void toggleNotif() {
        notifVisible = !notifVisible;
        notifPanel.setVisible(notifVisible);
        notifPanel.setMouseTransparent(!notifVisible);

        if (notifVisible) {
            notifPanel.toFront();
            LOGGER.info("Ouverture du panneau de notifications");
            loadNotifications();
        } else {
            LOGGER.info("Fermeture du panneau de notifications");
        }
    }

    private void loadNotifications() {
        notifContainer.getChildren().clear();
        addNotification("Nouvelle notification (placeholder)");
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

    // ==================== KANBANS ====================

    public void refreshKanbansFromModel() {
        // 1. Nettoyage des conteneurs
        if (createdKanbansContainer != null) createdKanbansContainer.getChildren().clear();
        if (participateKanbansContainer != null) participateKanbansContainer.getChildren().clear();
        if (availableKanbansContainer != null) availableKanbansContainer.getChildren().clear();

        if (core == null) return;

        // 2. Récupération des données depuis le Core
        List<LightKanban> kanbans = core.getAvailableLightKanbans();
        LightUser currentUser = core.getMe(); // Assurez-vous que core.getMe() est accessible

        if (kanbans == null || kanbans.isEmpty()) {
            LOGGER.info("Aucun kanban à afficher.");
            return;
        }

        LOGGER.info("Rafraîchissement de " + kanbans.size() + " kanbans.");

        // 3. Création et tri des cartes
        for (LightKanban k : kanbans) {
            VBox card = createKanbanCard(k);
            // TODO: logique de créateur de kanban

            // Pour l'instant, on ajoute tout dans "Available" pour s'assurer qu'ils s'affichent
            if (availableKanbansContainer != null) {
                availableKanbansContainer.getChildren().add(card);
            }
        }
    }

    private VBox createKanbanCard(LightKanban kanban) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(15));
        card.setPrefSize(200, 120);
        card.setMinWidth(200);
        card.setMinHeight(120);

        // Style CSS directement en Java pour l'exemple (bordures arrondies, ombre légère, fond blanc)
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 10;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);" +
                        "-fx-border-color: #eee;" +
                        "-fx-border-radius: 10;"
        );
        card.setCursor(Cursor.HAND);

        // Titre du Kanban
        Label titleLabel = new Label(kanban.getTitle());
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #333;");
        titleLabel.setWrapText(true);

        // ID ou Description courte (visuel)
        String shortId = kanban.getId().toString().substring(0, 8);
        Label idLabel = new Label("ID: " + shortId);
        idLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #888;");

        // Ajout des éléments à la carte
        card.getChildren().addAll(titleLabel, idLabel);

        // Gestion du clic
        card.setOnMouseClicked(event -> {
            LOGGER.info("Ouverture du Kanban : " + kanban.getTitle());
            if (core != null) {
                core.viewKanban(kanban.getId());
            }
        });

        // Effet de survol (optionnel)
        card.setOnMouseEntered(e -> card.setStyle(card.getStyle() + "-fx-background-color: #f9f9f9;"));
        card.setOnMouseExited(e -> card.setStyle(card.getStyle().replace("-fx-background-color: #f9f9f9;", "-fx-background-color: white;")));

        return card;
    }

    // ==================== NAVIGATION ====================

    @FXML
    private void handleCreateKanban() throws IOException {
        switchScene("/createKanban.fxml", "Créer un Kanban", createKanbanButton);
    }

    private void switchScene(String fxmlPath, String title, Node triggerNode) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();
        // If opening the create-kanban view, open it as a dialog and pass MainCore to its controller
        if (fxmlPath.toLowerCase().contains("createkanban")) {
            Object controller = loader.getController();
            if (controller instanceof CreateKanbanController createKanbanController) {
                createKanbanController.setMainCore(core);
            }

            Stage dialog = new Stage();
            dialog.initOwner((Stage) triggerNode.getScene().getWindow());
            dialog.setTitle(title);
            dialog.setScene(new Scene(root, 900, 600));
            dialog.show();
            LOGGER.info("Opened dialog: " + title);
            return;
        }

        Stage stage = (Stage) triggerNode.getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(new Scene(root, 1280, 720));
        LOGGER.info("Scene switched to: " + title);
    }
}