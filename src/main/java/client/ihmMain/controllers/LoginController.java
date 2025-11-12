package client.ihmMain.controllers;

import client.MainApp;
import client.ihmMain.MainCore;
import client.interfaces.MainCallsDataClient;
import client.interfaces.IhmMainCallsComm;

import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Collections;
import java.util.List;

/**
 * Implémente le scénario complet du login d’après les diagrammes de séquence :
 *  1) loginUser(username, password) appelé par l’utilisateur (onLogin)
 *  2) DATA.authentify(username, password)
 *  3) si OK -> DATA.getLightUser(), DATA.getMyListLightKanbans()
 *  4) COMM.connectServer(lightUser, kanbans)
 *  5) Navigation vers Home
 */
public class LoginController {

    // ---- FXML
    @FXML private TextField usernameField, ipField, portField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    // ---- Core principal
    private MainCore core;

    @FXML
    public void initialize() {
        core = MainApp.getCore();
        if (errorLabel != null) errorLabel.setVisible(false);
        if (ipField != null)   ipField.setText("127.0.0.1");
        if (portField != null) portField.setText("5050");
    }

    // ================== Handlers ==================

    /** Bouton Login cliqué : lance le scénario complet */
    @FXML
    private void onLogin() {
        showError(null);

        final String username = safe(usernameField.getText());
        final String password = safe(passwordField.getText());

        if (username.isBlank())        { showError("Username is required."); return; }
        if (password.length() < 6)     { showError("Password must be ≥ 6 characters."); return; }

        loginUser(username, password);
    }

    /** Bouton SignUp cliqué : redirige vers la page d’inscription */
    @FXML
    private void onGoToSignup() {
        try {
            MainApp.loadScene("/signup.fxml", "Sign up");
        } catch (Exception e) {
            showError("Cannot open signup: " + e.getMessage());
        }
    }

    /**
     * Méthode principale correspondant à l'appel "loginUser(username, password)" du diagramme.
     * Orchestration complète de la séquence d’authentification.
     */
    public void loginUser(String username, String password) {

        // 1) Authentification
        boolean ok = callAuthentify(username, password);
        if (!ok) { showError("Invalid username or password."); return; }

        // 2) Récupération du profil utilisateur
        LightUser me = callGetLightUser();
        if (me == null) { showError("Unable to load profile."); return; }

        // 3) Récupération de la liste des Kanbans
        List<LightKanban> myKanbans = callGetLightKanbans();
        if (myKanbans == null) myKanbans = Collections.emptyList();

        // MàJ état local
        core.setMe(me);
        core.addKanbans(myKanbans);

        // 4) Connexion serveur (COMM)
        callConnectServer(me, myKanbans);

        // 5) Navigation vers la vue Home
        navigateHome();
    }

    // ================== Appels élémentaires ==================

    private boolean callAuthentify(String username, String password) {
        MainCallsDataClient data = core.getDataPort();
        if (data == null) { showError("Data service not wired."); return false; }
        try {
            return data.authentify(username, password);
        } catch (Exception e) {
            showError("Auth error: " + e.getMessage());
            return false;
        }
    }

    private LightUser callGetLightUser() {
        MainCallsDataClient data = core.getDataPort();
        if (data == null) { showError("Data service not wired."); return null; }
        try {
            return data.getMyLightUser();
        } catch (Exception e) {
            showError("getLightUser failed: " + e.getMessage());
            return null;
        }
    }

    private List<LightKanban> callGetLightKanbans() {
        MainCallsDataClient data = core.getDataPort();
        if (data == null) { showError("Data service not wired."); return Collections.emptyList(); }
        try {
            return data.getMyListLightKanbans();
        } catch (Exception e) {
            showError("getLightKanbans failed: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    private void callConnectServer(LightUser me, List<LightKanban> kanbans) {
        IhmMainCallsComm comm = core.getCommPort();
        if (comm == null) return;
        try {
            comm.connectServer(me, kanbans);
        } catch (Exception e) {
            showError("connectServer failed: " + e.getMessage());
        }
    }

    private void navigateHome() {
        try {
            MainApp.loadScene("/home.fxml", "Home");
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
    private static String safe(String s){ return s==null? "" : s.trim(); }
}
