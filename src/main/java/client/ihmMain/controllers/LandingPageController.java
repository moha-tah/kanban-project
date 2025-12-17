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

/**
 * Contrôleur de la page d'accueil (Landing Page).
 * 
 * Ce contrôleur gère la première page visible de l'application, permettant
 * à l'utilisateur de se connecter, de s'inscrire, ou d'importer un profil.
 * 
 * @author Équipe Kanban
 * @version 1.0
 * @since 1.0
 * @see MainCore
 */
public class LandingPageController {

    /**
     * Cœur de l'application principale.
     */
    private MainCore core;
    
    /**
     * Logger pour les messages de log de cette classe.
     */
    private static final Logger LOGGER = Logger.getLogger(LandingPageController.class.getName());

    /**
     * Panneau racine de la vue.
     */
    @FXML
    private BorderPane root;

    /**
     * Bouton du logo (non utilisé actuellement).
     */
    @FXML
    private Button logoButton;

    /**
     * Initialise le contrôleur après le chargement du FXML.
     * 
     * Cette méthode récupère le MainCore depuis MainApp et vérifie
     * que les composants essentiels sont initialisés.
     */
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

    /**
     * Navigue vers la vue de connexion.
     * 
     * Cette méthode est appelée lorsque l'utilisateur clique sur le bouton
     * de connexion pour afficher la page de login.
     */
    @FXML
    private void login() {
        if (core != null) {
            core.showLoginView();
        } else {
            System.err.println("[LandingPageController] core is null in login()");
        }
    }

    /**
     * Navigue vers la vue d'inscription.
     * 
     * Cette méthode est appelée lorsque l'utilisateur clique sur le bouton
     * d'inscription pour afficher la page de signup.
     */
    @FXML
    private void signup() {
        if (core != null) {
            core.showSignupView();
        } else {
            System.err.println("[LandingPageController] core is null in signup()");
        }
    }

    /**
     * Ouvre un dialogue pour importer un profil depuis un fichier JSON.
     * 
     * Cette méthode permet à l'utilisateur de sélectionner un fichier JSON
     * contenant un profil sauvegardé, puis l'importe via la couche Data.
     * Après l'import réussi, l'utilisateur est redirigé vers la page de login.
     */
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
    /**
     * Affiche une alerte à l'utilisateur.
     * 
     * @param type Le type d'alerte (INFORMATION, ERROR, etc.)
     * @param title Le titre de l'alerte
     * @param content Le contenu du message
     */
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}