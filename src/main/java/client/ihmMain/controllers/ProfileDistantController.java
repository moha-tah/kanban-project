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
    private Label profileName2;

    @FXML
    private Label profileUsername2;

    @FXML
    private ImageView profileAvatar2;

    @FXML
    private Label collaborations2;


    @FXML
    private GridPane kanbansGrid2;

    private MainCore core;

    private static final String DEFAULT_AVATAR = "/profile_pic.png";
    private static final Logger LOGGER = Logger.getLogger(ProfileDistantController.class.getName());

    public void setCore(MainCore core) {
        this.core = core;
    }
    
    public void setUser(LightUser currentUser) {
        profileName2.setText(currentUser.getUsername());
        profileUsername2.setText("@" + currentUser.getUsername());

        collaborations2.setText("1");

        Image avatarImg = loadAvatarDistant(currentUser.getAvatar());
        if (avatarImg != null) profileAvatar2.setImage(avatarImg);

        kanbansGrid2.getChildren().clear();
    }

    private Image loadAvatarDistant(String avPath) {
        if (avPath != null && !avPath.isBlank()) {
            File f = new File(avPath);
            if (f.exists()) {
                return new Image(f.toURI().toString(), true);
            }
        }

        var url = getClass().getResource(DEFAULT_AVATAR);
        if (url == null) {
            return null; 
        }
        return new Image(url.toExternalForm(), true);
    }


    @FXML
    private void handleBackClick() {
        LOGGER.info("Bouton 'Back' cliqué : retour à l'écran d'accueil.");

        try {
            core.showHomeView();
            LOGGER.info("Navigation vers home_fxml.fxml réussie.");
        } catch (Exception e) {
            LOGGER.severe("Erreur lors de la navigation vers home_fxml.fxml : " + e.getMessage());
        }
    }

}
