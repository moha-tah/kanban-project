package client.ihmMain.controllers;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import client.MainApp;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class UserCardController {

    private static final Logger LOGGER = Logger.getLogger(UserCardController.class.getName());

    @FXML
    private Label nameLabel;

    @FXML
    private ImageView avatarImageView;

    // on réutilise profile_pic.png comme avatar par défaut
    private static final String DEFAULT_AVATAR = "/profile_pic.png";

    public void setUserData(String username, String avatarPath) {
        nameLabel.setText(username);

        Image avatar = loadAvatar(avatarPath);
        avatarImageView.setImage(avatar);
    }

    private Image loadAvatar(String avatarPath) {
        // 1) si un chemin fichier valide est fourni depuis le serveur
        try {
            if (avatarPath != null && !avatarPath.isBlank()) {
                File f = new File(avatarPath);
                if (f.exists()) {
                    return new Image(f.toURI().toString(), true);
                } else {
                    LOGGER.warning("Avatar file not found: " + avatarPath);
                }
            }
        } catch (Exception e) {
            LOGGER.warning("Error loading avatar '" + avatarPath + "': " + e.getMessage());
        }

        // 2) sinon, avatar par défaut dans les resources
        try {
            var url = getClass().getResource(DEFAULT_AVATAR);
            if (url == null) {
                LOGGER.warning("Default avatar resource not found: " + DEFAULT_AVATAR);
                return null; // dans ce cas, on garde l’image définie par FXML (@profile_pic.png)
            }
            return new Image(url.toExternalForm(), true);
        } catch (Exception e) {
            LOGGER.warning("Error loading default avatar: " + e.getMessage());
            return null;
        }
    }

    @FXML
    private void handleProfileDistantClick() throws IOException {
        // Charger le FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/profile_distant.fxml"));
        Parent root = loader.load();

        // Récupérer le controller
        ProfileDistantController controller = loader.getController();

        // Injecter MainCore
        controller.setCore(MainApp.getCore());  // récupère le Core global

        // Optionnel : mettre directement l'utilisateur courant
        if (MainApp.getCore() != null && MainApp.getCore().getMe() != null) {
            controller.setUser(MainApp.getCore().getMe());
        }

        // Afficher la scène
        Stage stage = (Stage) avatarImageView.getScene().getWindow();
        stage.setTitle("Mon Profil");
        stage.setScene(new Scene(root, 1280, 720));
    }
}