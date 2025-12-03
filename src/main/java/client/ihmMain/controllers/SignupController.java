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
 * Gère le scénario d’inscription (Sign Up).
 * 1) L’utilisateur remplit le formulaire
 * 2) On valide les champs
 * 3) DATA.sendCreateProfile()
 * 4) DATA.saveUser()
 * 5) COMM.connectToServer()
 * 6) Navigation vers Home
 */
public class SignupController {

    // ---- FXML
    @FXML private TextField firstNameField, lastNameField, usernameField;
    @FXML private PasswordField passwordField;
    @FXML private DatePicker birthDatePicker;
    @FXML private ImageView profileImageView;
    @FXML private Label errorLabel;

    // ---- Core principal
    private MainCore core;
    private File selectedImageFile;

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

    @FXML
    private void onBackToLogin() {
        try {
            core.showLoginView();
        } catch (Exception e) {
            showError("Cannot return to Login: " + e.getMessage());
        }
    }

    // ================== Logique principale ==================

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

    private void navigateBackToLogin() {
        try {
            core.showLoginView();
        } catch (Exception e) {
            showError("Cannot open Home: " + e.getMessage());
        }
    }

    // ================== Utilitaires ==================

    private void showError(String msg) {
        if (errorLabel == null) return;
        if (msg == null || msg.isBlank()) { errorLabel.setVisible(false); return; }
        errorLabel.setText(msg);
        errorLabel.setStyle("-fx-text-fill: red;");
        errorLabel.setVisible(true);
    }

    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }
}
