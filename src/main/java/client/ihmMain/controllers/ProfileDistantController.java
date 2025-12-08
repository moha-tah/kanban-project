package client.ihmMain.controllers; 

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import client.ihmMain.MainCore;
import common.dataClasses.LightUser;
import java.util.logging.Level;
import common.dataClasses.User;




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
    private GridPane kanbansGrid2;

    private MainCore core;
    private static ProfileDistantController instance;

    private static final String DEFAULT_AVATAR = "/profile_pic.png";
    private static final Logger LOGGER = Logger.getLogger(ProfileDistantController.class.getName());

    public void setCore(MainCore core) {
        this.core = core;
    }

    public static ProfileDistantController getInstance() {
        return instance;
    }

    @FXML
    public void initialize() {
        instance = this;
        LOGGER.info("[UI] ProfileDistantController initialisé (instance enregistrée).");
    }


    public void setUser(LightUser currentUser) {
        LOGGER.info(() -> "[UI] Ouverture du profil distant pour : " 
                + currentUser.getUsername()
                + " (ID=" + currentUser.getId() + ")");

        core.getCommPort().requestDistantProfile(core.getMe(), currentUser.getId());
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

    public void updateDistantProfile(User requestedUser) {
        LOGGER.info(() -> "[UI] Mise à jour du profil distant : "
                + requestedUser.getUsername()
                + " (ID=" + requestedUser.getId() + ")");

        try {
            profileName2.setText(requestedUser.getFullName());
            profileUsername2.setText("@" + requestedUser.getUsername());

            // Avatar
            Image avatarImg = loadAvatarDistant(requestedUser.getAvatar());
            if (avatarImg != null) {
                profileAvatar2.setImage(avatarImg);
                LOGGER.info("[UI] Avatar distant chargé.");
            } else {
                LOGGER.warning("[UI] Avatar distant introuvable, utilisation valeur par défaut.");
            }

            // Kanbans
            kanbansGrid2.getChildren().clear();
            requestedUser.getMyKanban().forEach(k -> {
                LOGGER.info("[UI] Kanban distant : " + k.getTitle());
                // ici tu peux ajouter tes nodes dans la grid
            });

            LOGGER.info("[UI] Profil distant affiché avec succès.");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "[UI] Erreur lors de updateDistantProfile", e);
        }
    }


}
