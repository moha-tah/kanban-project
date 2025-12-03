package client.ihmMain.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import client.ihmMain.MainCore;
import common.dataClasses.LightUser;
import java.util.logging.Level;



import java.io.File;
import java.util.logging.Logger;

public class ProfileDistantController {

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
    private static final Logger LOGGER = Logger.getLogger(ProfileDistantController.class.getName());

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

        try {
            core.showHomeView();
            LOGGER.info("Navigation vers home_fxml.fxml réussie.");
        } catch (Exception e) {
            LOGGER.severe("Erreur lors de la navigation vers home_fxml.fxml : " + e.getMessage());
            e.printStackTrace();
        }
    }

}
