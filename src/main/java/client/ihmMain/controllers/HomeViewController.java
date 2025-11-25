package client.ihmMain.controllers;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;

import client.MainApp;
import client.ihmMain.MainCore;
import common.dataClasses.Kanban;
import common.dataClasses.LightKanban;
import common.dataClasses.User;
import javafx.application.Platform;
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
    private UsersController usersController;
    private static HomeViewController instance;
    private static final Logger LOGGER = Logger.getLogger(HomeViewController.class.getName());

    public static HomeViewController getInstance() {
        return instance;
    }

    @FXML
    private void initialize() {
        instance = this;
        LOGGER.info("HomeView loaded!");

        core = MainApp.getCore();
        if (core == null) {
            LOGGER.severe("MainCore est null dans HomeViewController !");
        }

        loadUsersBar();
        refreshKanbansFromModel();
    }

    private void loadUsersBar() {
        if (usersBar == null) return;
        try {
            FXMLLoader usersLoader = new FXMLLoader(getClass().getResource("/users.fxml"));
            Node usersNode = usersLoader.load();
            usersBar.getChildren().setAll(Collections.singletonList(usersNode));

            this.usersController = usersLoader.getController();
            if (usersController != null) {
                usersController.setCore(core);
                usersController.refreshUsers();
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement de users.fxml", e);
        }
    }

    public void refreshUsersBar() {
        if (usersController != null) {
            usersController.refreshUsers();
        }
    }

    // ==================== NOTIFICATIONS ====================

    public static void handleNotif() {
        if (instance != null) instance.toggleNotif();
    }

    private void toggleNotif() {
        notifVisible = !notifVisible;
        notifPanel.setVisible(notifVisible);
        notifPanel.setMouseTransparent(!notifVisible);
        if (notifVisible) {
            notifPanel.toFront();
            loadNotifications();
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

    // ==================== KANBANS (LOGIQUE CORRIGÉE) ====================

    public void refreshKanbansFromModel() {
        // On s'assure d'être sur le thread JavaFX pour modifier l'UI
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::refreshKanbansFromModel);
            return;
        }

        // 1. Nettoyage des zones
        if (createdKanbansContainer != null) createdKanbansContainer.getChildren().clear();
        if (participateKanbansContainer != null) participateKanbansContainer.getChildren().clear();
        if (availableKanbansContainer != null) availableKanbansContainer.getChildren().clear();

        if (core == null) return;

        // 2. Récupérer la liste globale (broadcastée par le serveur)
        List<LightKanban> allKanbans = core.getAvailableLightKanbans();
        if (allKanbans == null || allKanbans.isEmpty()) return;

        // 3. Récupérer les IDs des Kanbans que j'ai créés (depuis le modèle local User)
        Set<UUID> myCreatedIds = new HashSet<>();
        try {
            // Accès au modèle lourd local
            User me = core.getDataClientProvider().getMyModel().getLocalUser();
            LOGGER.info("DEBUG: User object: " + (me != null ? me.getUsername() : "NULL"));
            if (me != null && me.getMyKanban() != null) {
                LOGGER.info("DEBUG: User has " + me.getMyKanban().size() + " kanbans in myKanban list");
                for (Kanban k : me.getMyKanban()) {
                    myCreatedIds.add(k.getId());
                    LOGGER.info("DEBUG: My created kanban ID: " + k.getId());
                }
            } else {
                LOGGER.warning("DEBUG: User or User.myKanban is null!");
            }
            LOGGER.info("DEBUG: Total my created kanbans: " + myCreatedIds.size());
        } catch (Exception e) {
            LOGGER.warning("Impossible de récupérer la liste des kanbans de l'utilisateur : " + e.getMessage());
        }

        // 4. Tri et Affichage
        LOGGER.info("DEBUG: Processing " + allKanbans.size() + " kanbans");
        for (LightKanban k : allKanbans) {
            LOGGER.info("DEBUG: Checking kanban " + k.getId() + " - isMyCreated: " + myCreatedIds.contains(k.getId()));
            VBox card = createKanbanCard(k);

            if (myCreatedIds.contains(k.getId())) {
                // C'est un Kanban que j'ai créé
                createdKanbansContainer.getChildren().add(card);
                LOGGER.info("DEBUG: Added to CREATED");
            } else {
                // C'est un Kanban public ou partagé venant des autres
                availableKanbansContainer.getChildren().add(card);
                LOGGER.info("DEBUG: Added to AVAILABLE");
            }
        }
    }

    private VBox createKanbanCard(LightKanban kanban) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(15));
        card.setPrefSize(200, 120);
        card.setMinWidth(200);
        card.setMinHeight(120);

        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 10;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);" +
                        "-fx-border-color: #eee;" +
                        "-fx-border-radius: 10;"
        );
        card.setCursor(Cursor.HAND);

        Label titleLabel = new Label(kanban.getTitle());
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #333;");
        titleLabel.setWrapText(true);

        String shortId = kanban.getId().toString().substring(0, 8);
        Label idLabel = new Label("ID: " + shortId);
        idLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #888;");

        card.getChildren().addAll(titleLabel, idLabel);

        card.setOnMouseClicked(event -> {
            LOGGER.info("Ouverture du Kanban : " + kanban.getTitle());
            if (core != null) {
                core.viewKanban(kanban.getId());
            }
        });

        // Effet visuel au survol
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
            return;
        }

        Stage stage = (Stage) triggerNode.getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(new Scene(root, 1280, 720));
    }
}