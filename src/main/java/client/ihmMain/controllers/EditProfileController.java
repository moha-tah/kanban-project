package client.ihmMain.controllers;

import client.MainApp;
import client.ihmMain.MainCore;
import client.interfaces.MainCallsDataClient;
import common.dataClasses.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDate;
import java.util.logging.Logger;

public class EditProfileController {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private DatePicker birthDatePicker;
    @FXML private TextField usernameField;
    @FXML private ImageView profileImageView;
    @FXML private Label errorLabel;

    private MainCore core;
    private User currentUser;
    private File selectedImage;

    private static final Logger LOGGER = Logger.getLogger(EditProfileController.class.getName());

    // -------------------------------------------------------------------------------------
    // INITIALISATION
    // -------------------------------------------------------------------------------------
    @FXML
    private void initialize() {
        errorLabel.setVisible(false);
    }

    // Injecte le core
    public void setCore(MainCore core) {
        this.core = core;

        if (core != null && core.getDataPort() != null) {
            loadUserData();
        }
    }

    // Récupère le User complet via Data
    private void loadUserData() {
        try {
            MainCallsDataClient data = core.getDataPort();
            if (data == null) return;

            this.currentUser = data.getLocalUser();
            if (currentUser == null) return;

            // Pré-remplissage des champs
            firstNameField.setText(currentUser.getFirstName());
            lastNameField.setText(currentUser.getLastName());
            birthDatePicker.setValue(currentUser.getBirthDate());
            usernameField.setText(currentUser.getUsername());

            // Avatar
            if (currentUser.getAvatar() != null && !currentUser.getAvatar().isBlank()) {
                File f = new File(currentUser.getAvatar());
                if (f.exists()) {
                    profileImageView.setImage(new Image(f.toURI().toString()));
                }
            }

        } catch (Exception e) {
            LOGGER.warning("Erreur lors du chargement du User complet : " + e.getMessage());
        }
    }

    // -------------------------------------------------------------------------------------
    // ACTION : CHOISIR UNE IMAGE
    // -------------------------------------------------------------------------------------
    @FXML
    private void onSelectImage() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select profile picture");

        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        Stage stage = (Stage) profileImageView.getScene().getWindow();
        selectedImage = chooser.showOpenDialog(stage);

        if (selectedImage != null) {
            profileImageView.setImage(new Image(selectedImage.toURI().toString()));
        }
    }

    // -------------------------------------------------------------------------------------
    // ACTION : ENREGISTRER
    // -------------------------------------------------------------------------------------
    @FXML
    private void onSaveProfile() {
        errorLabel.setVisible(false);

        if (core == null || core.getDataPort() == null) {
            errorLabel.setText("Internal error: Data layer missing.");
            errorLabel.setVisible(true);
            return;
        }

        // Validation username
        String newUsername = usernameField.getText().trim();
        if (newUsername.isEmpty()) {
            errorLabel.setText("Username cannot be empty.");
            errorLabel.setVisible(true);
            return;
        }

        // Récupération des valeurs
        String newFirstName = firstNameField.getText();
        String newLastName = lastNameField.getText();
        LocalDate newBirthDate = birthDatePicker.getValue();
        String newAvatar = (selectedImage != null) ? selectedImage.getAbsolutePath() : null;

        // Appel Data (la seule vraie source de vérité utilisateur)
        core.getDataPort().modifyLocalUser(
                newFirstName,
                newLastName,
                newBirthDate,
                newAvatar,
                newUsername
        );

        goBack();
    }

    // -------------------------------------------------------------------------------------
    // ACTION : RETOUR
    // -------------------------------------------------------------------------------------
    @FXML
    private void onBackToProfile() {
        goBack();
    }

    private void goBack() {
        try {
            if (core == null)
                core = MainApp.getCore();

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