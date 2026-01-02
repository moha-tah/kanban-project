package client.ihmMain.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import client.MainApp;
import client.ihmMain.MainCore;
import static client.ihmMain.utils.UiFormUtils.safe;
import static client.ihmMain.utils.UiFormUtils.showError;
import client.interfaces.IhmMainCallsComm;
import client.interfaces.MainCallsDataClient;
import common.dataClasses.Kanban;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;
import common.dataClasses.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField ipField;
    @FXML private TextField portField;
    @FXML private Label errorLabel;

    private MainCore core;
    private static final Logger LOGGER = Logger.getLogger(LoginController.class.getName());

    @FXML
    public void initialize() {
        core = MainApp.getCore();
        if (errorLabel != null) errorLabel.setVisible(false);
        if (ipField != null) ipField.setText("127.0.0.1");
        if (portField != null) portField.setText("8080");
        
        // Ajouter un listener pour la touche Entrée sur tous les champs
        setupEnterKeyListener(usernameField);
        setupEnterKeyListener(passwordField);
        setupEnterKeyListener(ipField);
        setupEnterKeyListener(portField);
    }
    
    private void setupEnterKeyListener(TextField field) {
        if (field != null) {
            field.setOnKeyPressed(this::handleEnterKey);
        }
    }
    
    private void handleEnterKey(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            onLogin();
        }
    }

    @FXML
    private void onLogin() {
        showError(errorLabel, null);
        String username = safe(usernameField.getText());
        String password = safe(passwordField.getText());
        String ip = safe(ipField.getText());
        String portStr = safe(portField.getText());
        int port;

        if (username.isBlank() || password.isBlank()) {
            showError(errorLabel, "Champs requis.");
            return;
        }
        try {
            port = Integer.parseInt(portStr);
        } catch (NumberFormatException e) {
            showError(errorLabel, "Port invalide.");
            return;
        }

        if (!updateConnection(ip, port)) return;

        loginUser(username, password);
    }

    @FXML
    private void onGoToSignup() {
        core.showSignupView();
    }

    private void loginUser(String username, String password) {
        // 1. Authentification (Charge les données du disque)
        boolean ok = authentify(username, password);
        if (!ok) {
            showError(errorLabel, "Login incorrect.");
            return;
        }

        // 2. Récupération du profil chargé
        LightUser me = loadLightUser();
        if (me == null) {
            showError(errorLabel, "Erreur chargement profil.");
            return;
        }
        core.setMe(me);

        // 3. Extraction des Kanbans chargés LOCALEMENT
        // Important : On convertit les Kanbans lourds du User en LightKanbans
        List<LightKanban> myKanbansToSend = extractMyKanbansFromModel();

        // 4. Ajout au Core pour affichage immédiat
        core.addKanbans(myKanbansToSend);

        // 5. Envoi au serveur pour qu'il les connaisse et les diffuse
        connectToServer(me, myKanbansToSend, core.getDataPort().getLocalUser().getMyKanban());

        core.showHomeView();
    }

    private boolean updateConnection(String newHost, int newPort) {
        IhmMainCallsComm comm = core.getCommPort();
        if (comm == null) return false;
        if (!comm.connect(newHost, newPort)) {
            showError(errorLabel, "Echec connexion serveur.");
            return false;
        }
        return true;
    }

    private MainCallsDataClient getDataPortOrShowError() {
        MainCallsDataClient data = core.getDataPort();
        if (data == null) showError(errorLabel, "Service Data indisponible.");
        return data;
    }

    private boolean authentify(String username, String password) {
        MainCallsDataClient data = getDataPortOrShowError();
        return data != null && data.authentify(username, password);
    }

    private LightUser loadLightUser() {
        MainCallsDataClient data = getDataPortOrShowError();
        return (data != null) ? data.getMyLightUser() : null;
    }

    private List<LightKanban> extractMyKanbansFromModel() {
        try {
            var provider = core.getDataClientProvider();
            User localUser = provider.getMyModel().getLocalUser();

            List<LightKanban> list = new ArrayList<>();
            if (localUser != null && localUser.getMyKanban() != null) {
                for (Kanban k : localUser.getMyKanban()) {
                    list.add(k);
                }
            }
            System.out.println("LOGIN: " + list.size() + " kanbans (complets) chargés pour envoi.");
            return list;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'extraction des kanbans locaux", e);
            return Collections.emptyList();
        }
    }

    private void connectToServer(LightUser me, List<LightKanban> kanbans, List<Kanban> completeKanbans) {
        IhmMainCallsComm comm = core.getCommPort();
        if (comm != null) {
            comm.connectServer(me, kanbans, completeKanbans);
        }
    }
}