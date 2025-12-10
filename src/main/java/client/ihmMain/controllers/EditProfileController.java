package client.ihmMain.controllers;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

import client.MainApp;
import client.ihmMain.MainCore;
import client.interfaces.MainCallsDataClient;
import common.dataClasses.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

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
        birthDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);

                if (date != null && date.isAfter(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffcdd2;");
                }
            }
        });
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
            LOGGER.log(Level.WARNING, "Erreur lors du chargement du User complet : {0}", e.getMessage());
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
        String newFirstName = firstNameField.getText().trim();
        String newLastName = lastNameField.getText().trim();
        LocalDate newBirthDate = birthDatePicker.getValue();
        String newAvatar = (selectedImage != null) ? selectedImage.getAbsolutePath() : null;

        if (newBirthDate != null && newBirthDate.isAfter(LocalDate.now())) {
            errorLabel.setText("Birth date cannot be in the future.");
            errorLabel.setVisible(true);
            return;
        }

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
            // Charger le FXML Profil
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/profile.fxml"));
            Parent root = loader.load();

            // Récupérer son contrôleur
            ProfileController controller = loader.getController();

            // Injecter le core
            MainCore core = MainApp.getCore();
            controller.setCore(core);

            // Injecter l’utilisateur courant (depuis Data, le vrai user complet)
            if (core != null && core.getDataPort() != null) {
                controller.setUser(core.getDataPort().getMyLightUser());
            }

            // Changer la scène
            Stage stage = (Stage) profileImageView.getScene().getWindow();
            stage.setTitle("Mon Profil");
            stage.setScene(new Scene(root, 1280, 720));

        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Erreur lors du retour au profil : {0}", e.getMessage());
        }
    }
}