package client.ihmMain.controllers;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import client.MainApp;
import client.data.KanbanCallsDataImplementation;
import client.ihmKanban.controllers.DisplayKanbanController;
import client.ihmMain.MainCore;
import common.dataClasses.Kanban;
import common.dataClasses.LightKanban;
import common.dataClasses.User;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
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
        if (notifVisible) notifPanel.toFront();
    }

    /**
     * Affiche une notification interactive pour une demande d'accès.
     */
    public void addRequestNotification(common.dataClasses.LightUser requester, common.dataClasses.LightKanban kanban) {
        if (notifContainer == null) return;

        VBox card = new VBox(8);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-border-radius: 8; -fx-padding: 12; -fx-border-color: #e0e0e0;");

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.1));
        shadow.setRadius(5);
        shadow.setOffsetY(2);
        card.setEffect(shadow);

        Label title = new Label("Demande d'accès");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #333;");

        Label msg = new Label(requester.getUsername() + " souhaite rejoindre\nle kanban : " + kanban.getTitle());
        msg.setWrapText(true);
        msg.setStyle("-fx-font-size: 13px; -fx-text-fill: #666;");

        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        Button btnRefuse = new Button("Refuser");
        btnRefuse.setStyle("-fx-background-color: #ffebee; -fx-text-fill: #c62828; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;");

        Button btnAccept = new Button("Accepter");
        btnAccept.setStyle("-fx-background-color: #e8f5e9; -fx-text-fill: #2e7d32; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;");

        // Actions
        btnRefuse.setOnAction(e -> {
            handleDecision(requester, kanban, false);
            notifContainer.getChildren().remove(card);
            if (notifVisible) toggleNotif();
        });

        btnAccept.setOnAction(e -> {
            handleDecision(requester, kanban, true);
            notifContainer.getChildren().remove(card);
            if (notifVisible) toggleNotif();
        });

        buttons.getChildren().addAll(btnRefuse, btnAccept);
        card.getChildren().addAll(title, msg, buttons);

        if (notifContainer.getChildren().size() > 0) {
            notifContainer.getChildren().add(1, card);
        } else {
            notifContainer.getChildren().add(card);
        }
        if (!notifVisible) toggleNotif();
    }

    public void addNotification(String message) {
        if (notifContainer == null) return;
        HBox box = new HBox();
        box.setSpacing(10);
        box.setStyle("-fx-background-color: #f2f2f2; -fx-padding: 10; -fx-background-radius: 8; -fx-border-color: #ddd;");
        Label msg = new Label(message);
        msg.setStyle("-fx-font-size: 14; -fx-text-fill: #333;");
        msg.setWrapText(true);
        msg.setMaxWidth(250);
        box.getChildren().add(msg);
        notifContainer.getChildren().add(0, box);
    }

    private void handleDecision(common.dataClasses.LightUser requester, common.dataClasses.LightKanban kanban, boolean accepted) {
        if (core != null) {
            core.sendPermissionResponse(requester, kanban, accepted);
            
            // Si accepté, mettre à jour le kanban local (DATA) pour lier l'utilisateur au kanban
            if (accepted) {
                try {
                    client.data.DataClientProvider provider = core.getDataClientProvider();
                    if (provider != null) {
                        client.interfaces.MainCallsDataClient dataClient = provider.getToMainImpl();
                        if (dataClient != null) {
                            dataClient.addAuthorizedUserToKanban(requester, kanban);
                        }
                    }
                } catch (Exception e) {
                    LOGGER.log(java.util.logging.Level.SEVERE, "Erreur lors de l'ajout de l'utilisateur au kanban", e);
                }
            }
        }
    }

    // ==================== KANBANS ====================

    public void refreshKanbansFromModel() {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::refreshKanbansFromModel);
            return;
        }

        if (createdKanbansContainer != null) createdKanbansContainer.getChildren().clear();
        if (participateKanbansContainer != null) participateKanbansContainer.getChildren().clear();
        if (availableKanbansContainer != null) availableKanbansContainer.getChildren().clear();

        if (core == null) return;

        List<LightKanban> allKanbans = core.getAvailableLightKanbans();
        if (allKanbans == null || allKanbans.isEmpty()) return;

        User meTemp = null;
        try {
            meTemp = core.getDataClientProvider().getMyModel().getLocalUser();
        } catch (Exception e) { /* ignore */ }

        // Variable finale pour utilisation dans la lambda
        final User me = meTemp;

        for (LightKanban lk : allKanbans) {
            // 1. Charger ou reconstruire le Kanban complet
            Kanban details = null;
            try {
                details = KanbanCallsDataImplementation.loadKanbanFromJson(lk);
            } catch (Exception e) { /* ignore */ }

            // Fallback serveur
            if (details == null && lk instanceof Kanban) {
                details = (Kanban) lk;
            }
            // Fallback défaut
            if (details == null) {
                details = new Kanban(lk.getId(), lk.getTitle(), null, "Private", null);
            }

            // 2. Réparer le créateur
            if (details.getCreator() == null) {
                if (isMyKanban(lk.getId(), me)) {
                    details.setCreator(me);
                } else {
                    UUID cId = details.getCreatorId();
                    User foundCreator = findUserById(cId);
                    if (foundCreator != null) {
                        details.setCreator(foundCreator);
                    } else {
                        String name = (cId != null) ? "User " + cId.toString().substring(0, 5) : "Unknown";
                        details.setCreator(new User(name, name, "", null));
                    
                    }
                }
            }

            // 3. Vérification des droits
            boolean isMine = isMyKanban(lk.getId(), me);
            boolean isParticipating = false;

            // CORRECTION ICI : Utilisation de la variable 'me' qui est maintenant effectivement finale
            // (car elle n'est pas modifiée à l'intérieur de la boucle ou après son initialisation finale)
            if (details.getAccessList() != null && me != null) {
                isParticipating = details.getAccessList().stream()
                        .anyMatch(acc -> acc.getUser().getId().equals(me.getId()));
            }
            // Fallback: si la décision d'accès a été acceptée mais que l'accessList n'est pas encore synchronisée
            try {
                if (!isParticipating && core != null && core.isExplicitlyParticipating(lk.getId())) {
                    isParticipating = true;
                }
            } catch (Exception ignore) { }

            // 4. Création et tri
            Node cardNode = createKanbanCardFromFXML(details, isMine, isParticipating);

            if (cardNode != null) {
                if (isMine) {
                    createdKanbansContainer.getChildren().add(cardNode);
                } else if (isParticipating) {
                    participateKanbansContainer.getChildren().add(cardNode);
                } else {
                    availableKanbansContainer.getChildren().add(cardNode);
                }
            }
        }
    }

    private User findUserById(UUID id) {
        if (id == null) return null;
        List<common.dataClasses.LightUser> users = core.getUsersSnapshot();
        if (users != null) {
            for (common.dataClasses.LightUser u : users) {
                if (u.getId().equals(id)) {
                    return new User(u.getUsername(), u.getUsername(), "", null);
                }
            }
        }
        return null;
    }

    private Node createKanbanCardFromFXML(Kanban kanban, boolean isMine, boolean isParticipating)
 {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/kanban_card.fxml"));
            Node cardNode = loader.load();

            KanbanCardController controller = loader.getController();
            controller.setMainCore(core);

            String color;

            if (isMine) {
                color = "#D8E9FF"; // bleu
            } else if (isParticipating) {
                color = "#EAD8FF"; // violet
            } else {
                color = "#D9FFE3"; // vert
            }


            controller.setKanbanData(kanban, color, isMine, isParticipating);
            core.registerKanbanCardController(kanban.getId(), controller);


            return cardNode;

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Impossible de charger kanban_card.fxml", e);
            return null;
        }
    }

    private boolean isMyKanban(UUID kanbanId, User me) {
        if (me == null || me.getMyKanban() == null) return false;
        return me.getMyKanban().stream().anyMatch(k -> k.getId().equals(kanbanId));
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
            if (controller instanceof CreateKanbanController c) c.setMainCore(core);
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

    @FXML private ScrollPane kanbanArea; 

    public ScrollPane getKanbanArea() {
        return kanbanArea;
    }   

    public void displayKanban(Kanban kanban) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/displayKanban.fxml"));
            Parent kanbanView = loader.load();

            DisplayKanbanController controller = loader.getController();
            //controller.set(kanban);

            // Remplacer le contenu central
            kanbanArea.setContent(kanbanView);

            core.getKanbanPort().openCreateForm(kanban,this); 

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Impossible de charger displayKanban.fxml", e);
        }
    }

    public void showHomeKanbanList() {
    try {
        // Recharger le contenu original (la liste des kanbans)
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/homeKanbanCentral.fxml"));
        Node homeContent = loader.load();
        kanbanArea.setContent(homeContent);

        // Rafraîchir les données des kanbans
        refreshKanbansFromModel();

    } catch (Exception e) {
        LOGGER.log(Level.SEVERE, "Impossible de charger homeKanbanCentral.fxml", e);
    }
}


}