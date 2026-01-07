package client.ihmMain.controllers;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Scene;

public class HomeKanbanCentralController {

    private static final Logger LOGGER = Logger.getLogger(HomeViewController.class.getName());

    @FXML private ScrollPane kanbanArea;
    @FXML private TextField searchField;
    @FXML private Button createKanbanButton;

    @FXML private HBox createdKanbansContainer;
    @FXML private HBox participateKanbansContainer;
    @FXML private HBox availableKanbansContainer;

    @FXML private Pane notifPanel;
    @FXML private VBox notifContainer;

    private boolean notifVisible = false;

    @FXML
    private void initialize() {
        // Initialisation simple
        notifPanel.setVisible(false);
        notifPanel.setMouseTransparent(true);
        LOGGER.info("HomeViewController initialized.");
    }

    @FXML
    private void handleCreateKanban() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/createKanban.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Créer un Kanban");
            stage.setScene(new Scene(root, 900, 600));
            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Impossible de charger createKanban.fxml", e);
        }
    }

    // ==================== Notifications ====================

    public void toggleNotif() {
        notifVisible = !notifVisible;
        notifPanel.setVisible(notifVisible);
        notifPanel.setMouseTransparent(!notifVisible);
        if (notifVisible) notifPanel.toFront();
    }

    public void addNotification(String message) {
        VBox notifBox = new VBox();
        notifBox.getChildren().add(new javafx.scene.control.Label(message));
        notifContainer.getChildren().add(notifBox);
        if (!notifVisible) toggleNotif();
    }

    // ==================== Kanbans ====================

    public void displayKanban(Node kanbanNode) {
        kanbanArea.setContent(kanbanNode);
    }

    public void showKanbanList(Node listNode) {
        kanbanArea.setContent(listNode);
    }

    public HBox getCreatedKanbansContainer() {
        return createdKanbansContainer;
    }

    public HBox getParticipateKanbansContainer() {
        return participateKanbansContainer;
    }

    public HBox getAvailableKanbansContainer() {
        return availableKanbansContainer;
    }
}