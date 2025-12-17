package client.ihmMain.controllers;

import java.io.File;
import java.io.IOException;

import client.MainApp;
import client.ihmMain.MainCore;
import common.dataClasses.LightUser;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

public class MenuController {

    @FXML private ImageView profilePic;
    private static final String DEFAULT_AVATAR_RESOURCE = "/profile_pic.png";

    @FXML
    private void initialize() {
        MainCore core = MainApp.getCore();
        if (core == null || profilePic == null) return;

        LightUser me = core.getMe();
        if (me == null) return;

        Image avatar = loadAvatar(me.getAvatar());
        profilePic.setImage(avatar);
    }

    private Image loadAvatar(String avatarPath) {
        // (Votre code existant pour charger l'avatar...)
        if (avatarPath != null && !avatarPath.isBlank()) {
            try {
                if (avatarPath.startsWith("http") || avatarPath.startsWith("file:")) {
                    return new Image(avatarPath, true);
                }
                File f = new File(avatarPath);
                if (f.exists()) return new Image(f.toURI().toString(), true);
            } catch (Exception ignored) {}
        }
        try {
            var url = getClass().getResource(DEFAULT_AVATAR_RESOURCE);
            if (url != null) return new Image(url.toExternalForm(), true);
        } catch (Exception ignored) {}
        return null;
    }

    @FXML
    private void handleProfileClick() throws IOException {
        // Charger le FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/profile.fxml"));
        Parent root = loader.load();

        // Récupérer le controller
        ProfileController controller = loader.getController();

        // Injecter MainCore
        controller.setCore(MainApp.getCore());  // récupère le Core global

        // Optionnel : mettre directement l'utilisateur courant
        if (MainApp.getCore() != null && MainApp.getCore().getMe() != null) {
            controller.setUser(MainApp.getCore().getMe());
        }

        // Afficher la scène
        Stage stage = (Stage) profilePic.getScene().getWindow();
        stage.setTitle("Mon Profil");
        stage.setScene(new Scene(root, 1280, 720));
    }


    @FXML
    private void handleHome() {
        if (MainApp.getCore() != null) MainApp.getCore().showHomeView();
    }


    @FXML
    private void handleNotif() {
        HomeViewController.handleNotif();
    }

    @FXML
    private void handleLogout() {
        MainCore core = MainApp.getCore();
        if (core == null) return;

        LightUser me = core.getMe();
        if (me != null) {
            try {
                if (core.getDataClientProvider() != null) {
                    System.out.println("[CLIENT] Sauvegarde locale avant déconnexion...");
                    core.getDataClientProvider().getToMainImpl().saveUser();
                }
            } catch (Exception e) {
                System.err.println("Erreur sauvegarde logout : " + e.getMessage());
            }

            // 2. DIAGRAMME : logout() vers la couche comm
            if (core.getCommPort() != null) {
                System.out.println("[CLIENT] Envoi demande de déconnexion pour " + me.getUsername());
                core.getCommPort().logout(me);
            }
        }

        // 3. Nettoyer l'état local et revenir au Login
        core.launchApp(); // Vide les listes et l'utilisateur courant
        core.showLoginView();
    }

    private void switchScene(String fxmlPath, String title, Node triggerNode) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();
        Stage stage = (Stage) triggerNode.getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(new Scene(root, 1280, 720));
    }
}