package client.ihmMain.controllers;

import client.MainApp;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import client.ihmMain.MainCore;
import common.dataClasses.LightUser;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.logging.Level;



import java.io.File;
import java.util.logging.Logger;

public class ProfileController {

    @FXML
    private Label profileName;

    @FXML
    private Label profileUsername;

    @FXML
    private ImageView profileAvatar;

    @FXML
    private Label kanbansCreated;

    @FXML
    private Label collaborations;


    @FXML
    private GridPane kanbansGrid;

    private MainCore core;

    private static final String DEFAULT_AVATAR = "/profile_pic.png";
    private static final Logger LOGGER = Logger.getLogger(ProfileController.class.getName());

    public void setCore(MainCore core) {
        this.core = core;
    }
    
    public void setUser(LightUser currentUser) {
        // Infos utilisateur
        profileName.setText(currentUser.getUsername());
        profileUsername.setText("@" + currentUser.getUsername());

        collaborations.setText("0"); 

        // Avatar
        Image avatarImg = loadAvatar(currentUser.getAvatar());
        if (avatarImg != null) profileAvatar.setImage(avatarImg);

        // Afficher les Kanbans
        kanbansGrid.getChildren().clear();
    }

    private Image loadAvatar(String avatarPath) {
        // 1) si un chemin fichier valide est fourni depuis le serveur
        try {
            if (avatarPath != null && !avatarPath.isBlank()) {
                File f = new File(avatarPath);
                if (f.exists()) {
                    return new Image(f.toURI().toString(), true);
                } else {
                    LOGGER.log(Level.WARNING, "Avatar file not found: {0}", avatarPath);
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
    private void handleBackClick() {
        LOGGER.info("Bouton 'Back' cliqué : retour à l'écran d'accueil.");

        // Sécuriser le core
        if (core == null) {
            core = MainApp.getCore();
        }

        if (core == null) {
            LOGGER.severe("Impossible de revenir à l'accueil : core est null.");
            return;
        }

        try {
            core.showHomeView();
            LOGGER.info("Navigation vers home.fxml réussie.");
        } catch (Exception e) {
            LOGGER.severe("Erreur lors de la navigation vers home.fxml : " + e.getMessage());
        }
    }


    @FXML
    private void handleEditProfileClick() throws IOException {

        // Récupérer le MainCore global
        core = MainApp.getCore();

        // Charger l'interface
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/editProfile.fxml"));
        Parent root = loader.load();

        // Récupérer le controller
        EditProfileController controller = loader.getController();

        // Injecter Core
        controller.setCore(core);

        // Injecter l'utilisateur actuel
        if (core != null && core.getMe() != null) {
            controller.setUser(core.getMe());
        }

        // Afficher la scène
        Stage stage = (Stage) profileAvatar.getScene().getWindow();
        stage.setScene(new Scene(root, 1280, 720));
        stage.setTitle("Edit Profile");
        stage.show();
    }

}
