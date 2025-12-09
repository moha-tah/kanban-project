package client.ihmMain.controllers;

import client.MainApp;
import client.ihmMain.MainCore;
import client.interfaces.MainCallsDataClient;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.logging.Logger;

public class LandingPageController {

    private MainCore core;
    private static final Logger LOGGER = Logger.getLogger(LandingPageController.class.getName());

    @FXML
    private BorderPane root;

    @FXML
    private Button logoButton;

    @FXML
    public void initialize() {
        // Récupère le MainCore initialisé dans MainApp.start()
        core = MainApp.getCore();

        if (core == null) {
            System.err.println("[LandingPageController] MainCore is null !");
        }

        if (root == null) {
            System.err.println("[LandingPageController] root is null !");
        }
    }

    @FXML
    private void login() {
        if (core != null) {
            core.showLoginView();
        } else {
            System.err.println("[LandingPageController] core is null in login()");
        }
    }

    @FXML
    private void signup() {
        if (core != null) {
            core.showSignupView();
        } else {
            System.err.println("[LandingPageController] core is null in signup()");
        }
    }

    @FXML
    private void importProfile() {
        if (core == null) return;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import Profile JSON");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));

        // Ouvre le dialogue sur la fenêtre actuelle
        Stage stage = (Stage) root.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            String path = selectedFile.getAbsolutePath();
            System.out.println("Importing profile from: " + path);

            try {
                MainCallsDataClient data = core.getDataPort();
                if (data != null) {
                    // Appel à la couche Data pour importer
                    data.importMyProfile(path);

                    // Feedback utilisateur
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Profile imported successfully!");

                    // Redirection vers le login pour se connecter avec ce nouveau profil
                    core.showLoginView();
                }
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Import Failed", "Could not import profile: " + e.getMessage());
                LOGGER.warning(e.getMessage());
            }
        }
    }
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}