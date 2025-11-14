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

        if (username.isBlank()) {
            showError(errorLabel, "Username is required.");
            return;
        }
        if (password.length() < 6) {
            showError(errorLabel, "Password must be ≥ 6 characters.");
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