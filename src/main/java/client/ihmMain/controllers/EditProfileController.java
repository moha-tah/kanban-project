package client.ihmMain.controllers;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import client.MainApp;
import client.ihmMain.MainCore;
import client.interfaces.MainCallsDataClient;
import common.dataClasses.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Contrôleur de la page d'édition de profil.
 * 
 * Ce contrôleur gère l'édition des informations du profil utilisateur :
 * prénom, nom, nom d'utilisateur, date de naissance, et avatar.
 * Il permet également de supprimer le compte utilisateur.
 * 
 * @author Équipe Kanban
 * @version 1.0
 * @since 1.0
 * @see ProfileController
 * @see MainCore
 */
public class EditProfileController {

    /**
     * Champ de saisie du prénom.
     */
    @FXML private TextField firstNameField;
    
    /**
     * Champ de saisie du nom de famille.
     */
    @FXML private TextField lastNameField;
    
    /**
     * Sélecteur de date de naissance.
     */
    @FXML private DatePicker birthDatePicker;
    
    /**
     * Champ de saisie du nom d'utilisateur.
     */
    @FXML private TextField usernameField;
    
    /**
     * ImageView pour l'avatar de profil.
     */
    @FXML private ImageView profileImageView;
    
    /**
     * Label pour afficher les erreurs de validation.
     */
    @FXML private Label errorLabel;

    /**
     * Cœur de l'application principale.
     */
    private MainCore core;
    
    /**
     * Utilisateur actuellement édité.
     */
    private User currentUser;
    
    /**
     * Fichier image sélectionné pour l'avatar.
     */
    private File selectedImage;

    /**
     * Logger pour les messages de log de cette classe.
     */
    private static final Logger LOGGER = Logger.getLogger(EditProfileController.class.getName());

    /**
     * Initialise le contrôleur après le chargement du FXML.
     * 
     * Cette méthode masque le label d'erreur et configure le DatePicker
     * pour empêcher la sélection de dates futures.
     */
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

    /**
     * Définit le cœur de l'application principale et charge les données utilisateur.
     * 
     * @param core Le cœur de l'application (ne doit pas être null)
     */
    public void setCore(MainCore core) {
        this.core = core;

        if (core != null && core.getDataPort() != null) {
            loadUserData();
        }
    }

    /**
     * Charge les données complètes de l'utilisateur depuis la couche Data.
     * 
     * Cette méthode récupère l'utilisateur complet et pré-remplit tous les champs
     * du formulaire avec ses données actuelles.
     */
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

    /**
     * Gère la sélection d'une image pour l'avatar.
     * 
     * Cette méthode ouvre un dialogue de sélection de fichier pour choisir
     * une image de profil (PNG, JPG, JPEG) et l'affiche dans l'ImageView.
     */
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

    /**
     * Gère l'enregistrement des modifications du profil.
     * 
     * Cette méthode valide les champs, puis appelle la couche Data
     * pour mettre à jour le profil utilisateur avec les nouvelles valeurs.
     */
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

    /**
     * Gère la suppression du compte utilisateur.
     * 
     * Cette méthode affiche une boîte de dialogue de confirmation,
     * puis supprime le profil local et redirige vers la landing page.
     */
    @FXML
    private void handleDeleteAccount() {
        // 1. Création de la boite de dialogue de confirmation
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Suppression de compte");
        alert.setHeaderText("Êtes-vous sûr de vouloir supprimer votre profil ?");
        alert.setContentText("Cette action est irréversible. Toutes vos données locales seront effacées.");

        // 2. Attente de la réponse
        Optional<ButtonType> result = alert.showAndWait();

        // 3. Si l'utilisateur clique sur OK
        if (result.isPresent() && result.get() == ButtonType.OK) {
            performDeletion();
        }
    }

    /**
     * Effectue la suppression effective du compte.
     * 
     * Cette méthode déconnecte l'utilisateur du serveur, supprime le profil local,
     * nettoie l'état de l'application, et redirige vers la landing page.
     */
    private void performDeletion() {
        if (core == null) return;

        try {
            // A. Prévenir le serveur pour se déconnecter proprement avant de supprimer
            if (core.getMe() != null && core.getCommPort() != null) {
                core.getCommPort().logout(core.getMe());
            }

            // B. Appel à la couche Data pour supprimer le fichier
            core.getDataPort().deleteLocalProfile();

            // C. Nettoyage de la mémoire vive (User courant = null)
            core.launchApp();

            // D. Redirection vers la Landing Page (ou Login)
            // On navigue vers l'accueil
            core.showLandingView();

            System.out.println("Compte supprimé et redirection effectuée.");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la suppression du compte : {0}", e.getMessage());
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setContentText("Erreur lors de la suppression : " + e.getMessage());
            errorAlert.show();
        }
    }

    /**
     * Gère l'annulation des modifications et retourne au profil.
     */
    @FXML
    private void handleCancel() {
        goBack();
    }

    /**
     * Gère le retour au profil sans sauvegarder.
     */
    @FXML
    private void onBackToProfile() {
        goBack();
    }

    /**
     * Navigue vers la vue de profil.
     * 
     * Cette méthode charge la vue de profil, injecte le MainCore et
     * l'utilisateur actuel, puis affiche la scène.
     */
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