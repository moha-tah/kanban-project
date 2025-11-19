package client.ihmMain.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class MenuController {

    @FXML private ImageView profilePic;

    @FXML
    private void handleProfileClick() throws IOException {
        switchScene("/profile.fxml", "Mon Profil", profilePic);
    }

    @FXML
    private void handleHome() {
        System.out.println("🏠 Déjà sur la page d'accueil.");
    }

    @FXML
    private void handleNotif() {
        HomeViewController.handleNotif(); // Appel statique possible
    }

    @FXML
    private void handleLogout() {
        System.out.println("🚪 Déconnexion — à implémenter plus tard.");
    }

    private void switchScene(String fxmlPath, String title, Node triggerNode) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();

        Stage stage = (Stage) triggerNode.getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(new Scene(root, 1280, 720));
    }
}
