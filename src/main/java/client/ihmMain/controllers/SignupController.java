package client.ihmMain.controllers;

import client.MainApp;
import client.ihmMain.MainCore;
import client.interfaces.MainCallsDataClient;
import client.interfaces.IhmMainCallsComm;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.time.LocalDate;
import javafx.scene.control.DateCell;

/**
 * Contrôleur de la page d'inscription (Sign Up).
 * 
 * Ce contrôleur gère le processus d'inscription d'un nouvel utilisateur :
 * 1) L'utilisateur remplit le formulaire
 * 2) Validation des champs
 * 3) Création du profil via DATA.sendCreateProfile()
 * 4) Sauvegarde de l'utilisateur via DATA.saveUser()
 * 5) Navigation vers la page de login
 * 
 * @author Équipe Kanban
 * @version 1.0
 * @since 1.0
 * @see LoginController
 * @see MainCore
 */
public class SignupController {

    /**
     * Champ de saisie du prénom.
     */
    @FXML private TextField firstNameField, lastNameField, usernameField;
    
    /**
     * Champ de saisie du mot de passe.
     */
    @FXML private PasswordField passwordField;
    
    /**
     * Sélecteur de date de naissance.
     */
    @FXML private DatePicker birthDatePicker;
    
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
     * Fichier image sélectionné pour l'avatar.
     */
    private File selectedImageFile;

    /**
     * Initialise le contrôleur après le chargement du FXML.
     * 
     * Cette méthode récupère le MainCore, masque le label d'erreur,
     * et configure le DatePicker pour empêcher la sélection de dates futures.
     */
    @FXML
    public void initialize() {
        core = MainApp.getCore();
        if (errorLabel != null) errorLabel.setVisible(false);

        // Prevent selecting future dates from the date picker UI
        if (birthDatePicker != null) {
            birthDatePicker.setEditable(false);
            birthDatePicker.setDayCellFactory(picker -> new DateCell() {
                @Override
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    if (date != null && date.isAfter(LocalDate.now())) {
                        setDisable(true);
                        setStyle("-fx-background-color: #f4cccc;");
                    }
                }
            });
        }
    }

    // ================== Handlers ==================

    /**
     * Gère la sélection d'une image pour l'avatar.
     * 
     * Cette méthode ouvre un dialogue de sélection de fichier pour choisir
     * une image de profil (PNG, JPG, JPEG).
     */
    @FXML
    private void onSelectImage() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose Profile Picture");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );
        File file = chooser.showOpenDialog(null);
        if (file != null) {
            selectedImageFile = file;
            profileImageView.setImage(new Image(file.toURI().toString()));
        }
    }

    /**
     * Gère le processus d'inscription.
     * 
     * Cette méthode valide tous les champs du formulaire, puis crée
     * le profil utilisateur via la couche Data.
     */
    @FXML
    private void onSignup() {
        showError(null);

        String firstName = safe(firstNameField.getText());
        String lastName  = safe(lastNameField.getText());
        String username  = safe(usernameField.getText());
        String password  = safe(passwordField.getText());
        LocalDate birth  = birthDatePicker.getValue();

        // --- Validation
        if (firstName.isBlank()) { showError("First name is required."); return; }
        if (lastName.isBlank())  { showError("Last name is required."); return; }
        if (username.isBlank())  { showError("Username is required."); return; }
        if (password.length() < 6) { showError("Password must be ≥ 6 characters."); return; }
        if (birth == null) { showError("Please select your birth date."); return; }
        // Prevent selecting a birth date in the future
        if (birth.isAfter(LocalDate.now())) {
            showError("Birth date cannot be in the future.");
            return;
        }

        createProfile(firstName, lastName, username, password, birth);
    }

    /**
     * Navigue vers la page de connexion.
     */
    @FXML
    private void onBackToLogin() {
        try {
            core.showLoginView();
        } catch (Exception e) {
            showError("Cannot return to Login: " + e.getMessage());
        }
    }

    /**
     * Crée un nouveau profil utilisateur.
     * 
     * Cette méthode appelle la couche Data pour créer le profil,
     * sauvegarde l'utilisateur, puis redirige vers la page de login.
     * 
     * @param firstName Le prénom (ne doit pas être null)
     * @param lastName Le nom de famille (ne doit pas être null)
     * @param username Le nom d'utilisateur (ne doit pas être null)
     * @param password Le mot de passe (ne doit pas être null)
     * @param birth La date de naissance (ne doit pas être null)
     */
    private void createProfile(String firstName, String lastName, String username, String password, LocalDate birth) {
        MainCallsDataClient data = core.getDataPort();
        IhmMainCallsComm comm = core.getCommPort();

        if (data == null) { showError("Data service not wired."); return; }

        try {
            String imagePath = selectedImageFile != null ? selectedImageFile.getAbsolutePath() : "";

            data.sendCreateProfile(username, password, firstName, lastName, birth.getYear(), imagePath, "", "", "", "", "");

            data.saveUser();

            // Si on retourne à la fenetre de longin, on ne set pas la liste de kanban et user
            // LightUser me = data.getMyLightUser();
            // if (me == null) { showError("Failed to retrieve new user profile."); return; }

            // List<LightKanban> myKanbans = data.getMyListLightKanbans();
            // if (myKanbans == null) myKanbans = Collections.emptyList();

            // core.setMe(me);
            // core.addKanbans(myKanbans);

            // if (comm != null) comm.connectServer(me, myKanbans);

            navigateBackToLogin();

        } catch (Exception e) {
            showError("Signup failed: " + e.getMessage());
        }
    }

    /**
     * Navigue vers la page de connexion après l'inscription.
     */
    private void navigateBackToLogin() {
        try {
            core.showLoginView();
        } catch (Exception e) {
            showError("Cannot open Home: " + e.getMessage());
        }
    }

    /**
     * Affiche un message d'erreur dans le label d'erreur.
     * 
     * @param msg Le message d'erreur à afficher (null pour masquer l'erreur)
     */
    private void showError(String msg) {
        if (errorLabel == null) return;
        if (msg == null || msg.isBlank()) { errorLabel.setVisible(false); return; }
        errorLabel.setText(msg);
        errorLabel.setStyle("-fx-text-fill: red;");
        errorLabel.setVisible(true);
    }

    /**
     * Nettoie une chaîne de caractères en supprimant les espaces.
     * 
     * @param s La chaîne à nettoyer (peut être null)
     * @return La chaîne nettoyée, ou une chaîne vide si null
     */
    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }
}
