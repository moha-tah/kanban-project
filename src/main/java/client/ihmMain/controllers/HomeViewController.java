package client.ihmMain.controllers;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;

import client.MainApp;
import client.ihmMain.MainCore;
import client.data.KanbanCallsDataImplementation; // Import nécessaire pour lire le JSON
import common.dataClasses.Kanban;
import common.dataClasses.LightKanban;
import common.dataClasses.User;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
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

    public static void handleNotif() {
        if (instance != null) instance.toggleNotif();
    }

    private void toggleNotif() {
        notifVisible = !notifVisible;
        notifPanel.setVisible(notifVisible);
        notifPanel.setMouseTransparent(!notifVisible);
        if (notifVisible) {
            notifPanel.toFront();
            // loadNotifications(); // À implémenter si besoin
        }
    }

    // ==================== KANBANS AVEC DESIGN FXML ====================

    public void refreshKanbansFromModel() {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::refreshKanbansFromModel);
            return;
        }

        // 1. Nettoyage
        if (createdKanbansContainer != null) createdKanbansContainer.getChildren().clear();
        if (participateKanbansContainer != null) participateKanbansContainer.getChildren().clear();
        if (availableKanbansContainer != null) availableKanbansContainer.getChildren().clear();

        if (core == null) return;

        List<LightKanban> allKanbans = core.getAvailableLightKanbans();
        if (allKanbans == null || allKanbans.isEmpty()) return;

        // 2. Récupération de "Moi"
        User me = null;
        try {
            me = core.getDataClientProvider().getMyModel().getLocalUser();
        } catch (Exception e) { /* ignore */ }

        // 3. Création des cartes
        for (LightKanban lk : allKanbans) {

            // A. Chargement du JSON local (qui contient creatorId, visibility, columns...)
            // Astuce: Si vous testez sur le même PC, le fichier existe pour les 2 clients !
            Kanban details = KanbanCallsDataImplementation.loadKanbanFromJson(lk.getId());

            // B. Fallback si pas de JSON (cas sur des PC différents sans partage de fichier)
            if (details == null) {
                details = new Kanban(lk.getId(), lk.getTitle(), "Public", null);
            }

            // C. RÉPARATION DU CRÉATEUR (C'est ici que ça se joue)
            // Le JSON a chargé creatorId mais creator est null (car transient)
            if (details.getCreator() == null) {
                if (isMyKanban(lk.getId(), me)) {
                    // C'est moi
                    details.setCreator(me);
                } else {
                    // C'est un autre : on essaie de trouver son nom via son ID
                    UUID cId = details.getCreatorId();
                    User foundCreator = findUserById(cId);

                    if (foundCreator != null) {
                        // On a trouvé l'utilisateur dans la liste des connectés !
                        details.setCreator(foundCreator);
                    } else {
                        // On ne le connaît pas, on crée un User temporaire avec l'ID comme nom
                        // pour éviter "Unknown" si possible, ou au moins afficher l'ID
                        String name = (cId != null) ? "User " + cId.toString().substring(0, 5) : "Unknown";
                        details.setCreator(new User(name, name, "", null));
                    }
                }
            }

            // D. Création visuelle
            Node cardNode = createKanbanCardFromFXML(details);

            if (cardNode != null) {
                if (isMyKanban(lk.getId(), me)) {
                    createdKanbansContainer.getChildren().add(cardNode);
                } else {
                    availableKanbansContainer.getChildren().add(cardNode);
                }
            }
        }
    }

    private User findUserById(UUID id) {
        if (id == null) return null;
        // On regarde dans la liste des users connectés reçue du serveur
        List<common.dataClasses.LightUser> users = core.getUsersSnapshot();
        if (users != null) {
            for (common.dataClasses.LightUser u : users) {
                if (u.getId().equals(id)) {
                    // On recrée un objet User compatible avec Kanban.setCreator
                    // LightUser a username, on l'utilise pour firstName/lastName pour l'affichage
                    return new User(u.getUsername(), u.getUsername(), "", null);
                }
            }
        }
        return null;
    }

    /**
     * Charge le fichier kanban_card.fxml et initialise son contrôleur.
     */
    private Node createKanbanCardFromFXML(Kanban kanban) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/kanban_card.fxml"));
            // Node est souvent un AnchorPane selon ton FXML
            Node cardNode = loader.load();

            KanbanCardController controller = loader.getController();
            controller.setMainCore(core);

            // Logique de couleur simple pour différencier visuellement
            String color = "#FFFFFF"; // Blanc défaut
            if (kanban.getVisibility() != null && kanban.getVisibility().equalsIgnoreCase("Private")) {
                color = "#FFE5E5"; // Rouge très pâle pour Privé
            } else {
                color = "#E5FFE5"; // Vert très pâle pour Public
            }

            // Injection des données dans le contrôleur de la carte
            controller.setKanbanData(kanban, color);

            return cardNode;

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Impossible de charger kanban_card.fxml pour " + kanban.getTitle(), e);
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