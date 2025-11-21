package client.ihmMain.controllers;

import client.MainApp;
import client.ihmMain.MainCore;
import client.ihmMain.impl.dataCallsMainImpl;
import client.interfaces.MainCallsDataClient;
import client.interfaces.IhmMainCallsComm;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Collections;
import java.util.List;
import client.ihmMain.impl;

import static client.ihmMain.utils.UiFormUtils.safe;
import static client.ihmMain.utils.UiFormUtils.showError;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField ipField;
    @FXML private TextField portField;
    @FXML private Label errorLabel;

    private MainCore core;

    @FXML
    public void initialize() {
        core = MainApp.getCore();

        if (errorLabel != null) {
            errorLabel.setVisible(false);
        }
        if (ipField != null) {
            ipField.setText("127.0.0.1");
        }
        if (portField != null) {
            portField.setText("8080");
        }
    }

    @FXML
    private void onLogin() {
        showError(errorLabel, null);

        String username = safe(usernameField.getText());
        String password = safe(passwordField.getText());

        // --- Récupération et validation IP/Port ---
        String ip = safe(ipField.getText());
        String portStr = safe(portField.getText());
        int port;

        if (username.isBlank() || password.isBlank()) {
            showError(errorLabel, "Username and password are required.");
            return;
        }
        if (password.length() < 6) {
            showError(errorLabel, "Password must be ≥ 6 characters.");
            return;
        }
        if (ip.isBlank()) {
            showError(errorLabel, "IP Address is required.");
            return;
        }
        try {
            port = Integer.parseInt(portStr);
            if (port <= 0 || port > 65535) {
                showError(errorLabel, "Invalid port number.");
                return;
            }
        } catch (NumberFormatException e) {
            showError(errorLabel, "Port must be a number.");
            return;
        }
        // ------------------------------------------

        // Connexion dynamique
        if (!updateConnection(ip, port)) {
            // L'erreur est affichée dans updateConnection si échec
            return;
        }

        loginUser(username, password);
    }

    @FXML
    private void onGoToSignup() {
        core.showSignupView();
    }

    private void loginUser(String username, String password) {
        boolean ok = authentify(username, password);
        if (!ok) {
            showError(errorLabel, "Invalid username or password.");
            return;
        }

        LightUser me = loadLightUser();
        if (me == null) {
            showError(errorLabel, "Unable to load profile.");
            return;
        }

        List<LightKanban> myKanbans = loadMyKanbans();
        if (myKanbans == null) {
            myKanbans = Collections.emptyList();
        }

        core.setMe(me);
        core.addKanbans(myKanbans);

        connectToServer(me, myKanbans);

        core.showHomeView();
    }

    // Gère l'appel à la couche COMM pour la connexion dynamique
    private boolean updateConnection(String newHost, int newPort) {
        IhmMainCallsComm comm = core.getCommPort();
        if (comm == null) {
            showError(errorLabel, "Communication service unavailable.");
            return false;
        }

        if (!comm.connect(newHost, newPort)) {
            showError(errorLabel, "Failed to connect to " + newHost + ":" + newPort);
            return false;
        }
        return true;
    }


    // ---------------------------------------------------------------------
    //                 FACTORISATION ACCÈS DATA LAYER
    // ---------------------------------------------------------------------

    private MainCallsDataClient getDataPortOrShowError() {
        MainCallsDataClient data = core.getDataPort();
        if (data == null) {
            showError(errorLabel, "Data service unavailable.");
        }
        return data;
    }

    private boolean authentify(String username, String password) {
        MainCallsDataClient data = getDataPortOrShowError();
        if (data == null) {
            return false;
        }
        try {
            return data.authentify(username, password);
        } catch (Exception e) {
            showError(errorLabel, "Auth error: " + e.getMessage());
            return false;
        }
    }

    private LightUser loadLightUser() {
        MainCallsDataClient data = getDataPortOrShowError();
        if (data == null) {
            return null;
        }
        try {
            return data.getMyLightUser();
        } catch (Exception e) {
            showError(errorLabel, "Error loading profile: " + e.getMessage());
            return null;
        }
    }

    private List<LightKanban> loadMyKanbans() {
        MainCallsDataClient data = getDataPortOrShowError();
        if (data == null) {
            return Collections.emptyList();
        }
        try {
            return data.getMyListLightKanbans();
        } catch (Exception e) {
            showError(errorLabel, "Error loading Kanbans: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    // ---------------------------------------------------------------------
    //                         CALL TO COMM LAYER
    // ---------------------------------------------------------------------

    private void connectToServer(LightUser me, List<LightKanban> kanbans) {
        IhmMainCallsComm comm = core.getCommPort();
        if (comm == null) {
            return;
        }
        try {
            comm.connectServer(me, kanbans);
        } catch (Exception e) {
            showError(errorLabel, "Server connection failed: " + e.getMessage());
        }
    }
}