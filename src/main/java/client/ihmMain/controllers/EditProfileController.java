package client.ihmMain.controllers;

import client.ihmMain.MainCore;
import common.dataClasses.LightUser;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import client.MainApp;

import java.io.File;
import java.util.logging.Logger;

public class EditProfileController {

    @FXML private TextField usernameField;
    @FXML private ImageView profileImageView;
    @FXML private Label errorLabel;

    private MainCore core;
    private LightUser currentUser;
    private File selectedImage;

    private static final Logger LOGGER = Logger.getLogger(EditProfileController.class.getName());

    @FXML
    private void initialize() {
        // IMPORTANT : ne rien charger d’images ici → évite les Invalid URL
        errorLabel.setVisible(false);
    }

    public void setCore(MainCore core) {
        this.core = core;

        if (core != null && core.getMe() != null) {
            setUser(core.getMe());
        }
    }

    public void setUser(LightUser user) {
        if (user == null) return;
        this.currentUser = user;

        // Remplir le username
        usernameField.setText(user.getUsername());

        // Charger l’avatar si présent
        if (user.getAvatar() != null && !user.getAvatar().isBlank()) {
            File f = new File(user.getAvatar());
            if (f.exists()) {
                profileImageView.setImage(new Image(f.toURI().toString()));
            }
        }
    }

    @FXML
    private void onSelectImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select profile picture");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        Stage stage = (Stage) profileImageView.getScene().getWindow();
        selectedImage = fileChooser.showOpenDialog(stage);

        if (selectedImage != null) {
            profileImageView.setImage(new Image(selectedImage.toURI().toString()));
        }
    }

    @FXML
    private void onSaveProfile() {
        errorLabel.setVisible(false);

        if (core == null || core.getMe() == null) {
            errorLabel.setText("Internal error: no user.");
            errorLabel.setVisible(true);
            return;
        }

        LightUser me = core.getMe();

        // Validation
        String newUsername = usernameField.getText().trim();
        if (newUsername.isEmpty()) {
            errorLabel.setText("Username cannot be empty.");
            errorLabel.setVisible(true);
            return;
        }

        // Mise à jour locale
        me.setUsername(newUsername);
        if (selectedImage != null) {
            me.setAvatar(selectedImage.getAbsolutePath());
        }

        // --- FUTURE UPDATE ---
        // Ici la team DATA ajoutera une méthode pour update le profil
        // On prépare juste l’appel
        if (core.getDataPort() != null) {
            try {
                //core.getDataPort().updateUser(me); --- IGNORE ---
            } catch (Exception e) {
                LOGGER.warning("Data port updateUser not implemented yet.");
            }
        }

        goBack();
    }

    @FXML
    private void onBackToProfile() {
        goBack();
    }

    private void goBack() {
        try {
            // Sécurisation : si core est NULL, on va le chercher dans MainApp
            if (core == null) {
                core = MainApp.getCore();
            }

            if (core == null) {
                LOGGER.warning("Impossible de revenir au profil : core est NULL.");
                return;
            }

            core.showProfileView();

        } catch (Exception e) {
            LOGGER.warning("Error returning to profile: " + e.getMessage());
        }
    }
}