package client.src.ihmMain.controllers;

import client.src.MainApp;
import client.src.ihmMain.MainCore;
import client.src.interfaces.MainCallsDataClient;
import client.src.interfaces.IhmMainCallsComm;

import common.src.dataClasses.LightKanban;
import common.src.dataClasses.LightUser;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

/**
 * Suivi strict du diagramme :
 *  1) authentify(username, password) via DATA
 *  2) si KO -> message d'erreur
 *  3) si OK -> getLightUser(), getMyListLightKanbans()
 *  4) connectServer(lightUser, kanbans) via COMM
 *  5) navigation Home
 */
public class LoginController {

    @FXML private TextField usernameField, ipField, portField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private MainCore core;

    @FXML
    public void initialize() {
        core = MainApp.getCore();
        if (errorLabel != null) errorLabel.setVisible(false);
        if (ipField != null)   ipField.setText("127.0.0.1");
        if (portField != null) portField.setText("5050");
    }

    @FXML
    private void onLogin() {
        showError(null);

        final String username = safe(usernameField.getText());
        final String password = safe(passwordField.getText());

        if (username.isBlank())        { showError("Username is required."); return; }
        if (password.length() < 6)     { showError("Password must be ≥ 6 characters."); return; }

        // ---- DATA.authentify ----
        final MainCallsDataClient data = core.getDataPort();
        if (data == null) { showError("Data service not wired."); return; }

        boolean ok;
        try {
            ok = data.authentify(username, password);
        } catch (Exception e) {
            showError("Auth error: " + e.getMessage());
            return;
        }
        if (!ok) { showError("Invalid username or password."); return; }

        // ---- DATA.getLightUser / getMyListLightKanbans ----
        LightUser me;
        List<LightKanban> myKanbans;
        try {
            me = data.getMyLightUser();
            myKanbans = data.getMyListLightKanbans();
        } catch (Exception e) {
            showError("Loading profile failed: " + e.getMessage());
            return;
        }

        // MàJ état IHM
        core.setMe(me);
        core.addKanbans(myKanbans);

        // ---- COMM.connectServer(lightUser, kanbans) ----
        final IhmMainCallsComm comm = core.getCommPort();
        if (comm != null) {
            try { comm.connectServer(me, myKanbans); }
            catch (Exception e) { showError("ConnectServer failed: " + e.getMessage()); return; }
        }

        // ---- Go Home ----
        try {
            MainApp.loadScene("/home.fxml", "Home");
        } catch (Exception e) {
            showError("Cannot open Home: " + e.getMessage());
        }
    }

    @FXML
    private void onGoToSignup() {
        try {
            MainApp.loadScene("/signup.fxml", "Sign up");
        } catch (Exception e) {
            showError("Cannot open signup: " + e.getMessage());
        }
    }

    // -- utils
    private void showError(String msg) {
        if (errorLabel == null) return;
        if (msg == null || msg.isBlank()) { errorLabel.setVisible(false); return; }
        errorLabel.setText(msg);
        errorLabel.setStyle("-fx-text-fill: red;");
        errorLabel.setVisible(true);
    }
    private static String safe(String s){ return s==null? "" : s.trim(); }
}