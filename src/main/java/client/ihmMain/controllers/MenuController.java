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

    @FXML
    private ImageView profilePic;

    // Ressource avatar par défaut
    private static final String DEFAULT_AVATAR_RESOURCE = "/profile_pic.png";

    @FXML
    private void initialize() {
        MainCore core = MainApp.getCore();
        if (core == null || profilePic == null) return;

        LightUser me = core.getMe();
        if (me == null) return;

        Image avatar = loadAvatar(me.getAvatar());

        // Définir l’image finale
        profilePic.setImage(avatar);
    }

    private Image loadAvatar(String avatarPath) {
        if (avatarPath != null && !avatarPath.isBlank()) {
            try {
                if (avatarPath.startsWith("http") || avatarPath.startsWith("file:")) {
                    return new Image(avatarPath, true);
                }

                // Fichier local classique
                File f = new File(avatarPath);
                if (f.exists()) {
                    return new Image(f.toURI().toString(), true);
                } else {
                    System.out.println("Avatar introuvable : " + avatarPath);
                }
            } catch (Exception e) {
                System.out.println("Erreur chargement avatar : " + e.getMessage());
                // On continue pour charger l'avatar par défaut
            }
        }

        // Avatar par défaut depuis les ressources
        try {
            var url = getClass().getResource(DEFAULT_AVATAR_RESOURCE);
            if (url != null) {
                return new Image(url.toExternalForm(), true);
            }
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
    private void handleHome() {}

    @FXML
    private void handleNotif() {
        HomeViewController.handleNotif();
    }

    @FXML
    private void handleLogout() {}

    private void switchScene(String fxmlPath, String title, Node triggerNode) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();
        Stage stage = (Stage) triggerNode.getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(new Scene(root, 1280, 720));
    }
}