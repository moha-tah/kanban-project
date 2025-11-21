package client.ihmMain.controllers;

import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.Level;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class MenuController {

    @FXML private ImageView profilePic;

    private static final Logger LOGGER = Logger.getLogger(MenuController.class.getName());

    @FXML
    private void handleProfileClick() throws IOException {
        LOGGER.info("👤 Ouverture de la page Profil");
        switchScene("/profile.fxml", "Mon Profil", profilePic);
    }

    @FXML
    private void handleHome() {
        LOGGER.info("🏠 Déjà sur la page d'accueil.");
    }

    @FXML
    private void handleNotif() {
        LOGGER.info("🔔 Ouverture/Masquage des notifications");
        HomeViewController.handleNotif();
    }

    @FXML
    private void handleLogout() {
        LOGGER.warning("🚪 Déconnexion — à implémenter plus tard.");
    }

    private void switchScene(String fxmlPath, String title, Node triggerNode) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = (Stage) triggerNode.getScene().getWindow();
            stage.setTitle(title);
            stage.setScene(new Scene(root, 1280, 720));

            LOGGER.info("🔄 Changement de scène vers : " + title);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "❌ Erreur lors du changement de scène vers " + title, e);
            throw e;
        }
    }
}
