package client.ihmMain.controllers;

import client.MainApp;
import client.ihmMain.MainCore;
import client.interfaces.MainCallsDataClient;
import client.interfaces.IhmMainCallsComm;
import common.dataClasses.Kanban;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;
import common.dataClasses.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static client.ihmMain.utils.UiFormUtils.safe;
import static client.ihmMain.utils.UiFormUtils.showError;

/**
 * Contrôleur de la page de connexion.
 * 
 * Ce contrôleur gère le processus de connexion de l'utilisateur :
 * authentification locale, chargement du profil, connexion au serveur,
 * et navigation vers la page d'accueil.
 * 
 * @author Équipe Kanban
 * @version 1.0
 * @since 1.0
 * @see MainCore
 * @see SignupController
 */
public class LoginController {

    /**
     * Champ de saisie du nom d'utilisateur.
     */
    @FXML private TextField usernameField;
    
    /**
     * Champ de saisie du mot de passe.
     */
    @FXML private PasswordField passwordField;
    
    /**
     * Champ de saisie de l'adresse IP du serveur.
     */
    @FXML private TextField ipField;
    
    /**
     * Champ de saisie du port du serveur.
     */
    @FXML private TextField portField;
    
    /**
     * Label pour afficher les erreurs de connexion.
     */
    @FXML private Label errorLabel;

    /**
     * Cœur de l'application principale.
     */
    private MainCore core;
    
    /**
     * Logger pour les messages de log de cette classe.
     */
    private static final Logger LOGGER = Logger.getLogger(LoginController.class.getName());

    /**
     * Initialise le contrôleur après le chargement du FXML.
     * 
     * Cette méthode récupère le MainCore, masque le label d'erreur,
     * et initialise les champs IP et port avec des valeurs par défaut.
     */
    @FXML
    public void initialize() {
        core = MainApp.getCore();
        if (errorLabel != null) errorLabel.setVisible(false);
        if (ipField != null) ipField.setText("127.0.0.1");
        if (portField != null) portField.setText("8080");
    }

    /**
     * Gère le processus de connexion.
     * 
     * Cette méthode valide les champs, établit la connexion au serveur,
     * authentifie l'utilisateur, charge son profil et ses kanbans,
     * puis navigue vers la page d'accueil.
     */
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

    /**
     * Navigue vers la page d'inscription.
     */
    @FXML
    private void onGoToSignup() {
        core.showSignupView();
    }

    /**
     * Effectue le processus complet de connexion de l'utilisateur.
     * 
     * Cette méthode authentifie l'utilisateur, charge son profil,
     * extrait ses kanbans locaux, les ajoute au core, et se connecte au serveur.
     * 
     * @param username Le nom d'utilisateur (ne doit pas être null)
     * @param password Le mot de passe (ne doit pas être null)
     */
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
        connectToServer(me, myKanbansToSend);

        core.showHomeView();
    }

    /**
     * Met à jour la connexion au serveur avec une nouvelle adresse et port.
     * 
     * @param newHost La nouvelle adresse IP du serveur (ne doit pas être null)
     * @param newPort Le nouveau port du serveur
     * @return true si la connexion a réussi, false sinon
     */
    private boolean updateConnection(String newHost, int newPort) {
        IhmMainCallsComm comm = core.getCommPort();
        if (comm == null) return false;
        if (!comm.connect(newHost, newPort)) {
            showError(errorLabel, "Echec connexion serveur.");
            return false;
        }
        return true;
    }

    /**
     * Récupère le port Data ou affiche une erreur.
     * 
     * @return Le port Data, ou null si indisponible
     */
    private MainCallsDataClient getDataPortOrShowError() {
        MainCallsDataClient data = core.getDataPort();
        if (data == null) showError(errorLabel, "Service Data indisponible.");
        return data;
    }

    /**
     * Authentifie l'utilisateur avec son nom d'utilisateur et mot de passe.
     * 
     * @param username Le nom d'utilisateur (ne doit pas être null)
     * @param password Le mot de passe (ne doit pas être null)
     * @return true si l'authentification a réussi, false sinon
     */
    private boolean authentify(String username, String password) {
        MainCallsDataClient data = getDataPortOrShowError();
        return data != null && data.authentify(username, password);
    }

    /**
     * Charge l'utilisateur léger depuis la couche Data.
     * 
     * @return L'utilisateur léger, ou null si le chargement échoue
     */
    private LightUser loadLightUser() {
        MainCallsDataClient data = getDataPortOrShowError();
        return (data != null) ? data.getMyLightUser() : null;
    }

    /**
     * Extrait les kanbans de l'utilisateur depuis le modèle local.
     * 
     * Cette méthode récupère les kanbans complets de l'utilisateur local
     * et les convertit en LightKanbans pour l'envoi au serveur.
     * 
     * @return La liste des kanbans légers de l'utilisateur
     */
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

    /**
     * Se connecte au serveur avec l'utilisateur et ses kanbans.
     * 
     * Cette méthode envoie les informations de l'utilisateur et ses kanbans
     * au serveur pour synchronisation.
     * 
     * @param me L'utilisateur connecté (ne doit pas être null)
     * @param kanbans La liste des kanbans de l'utilisateur (ne doit pas être null)
     */
    private void connectToServer(LightUser me, List<LightKanban> kanbans) {
        IhmMainCallsComm comm = core.getCommPort();
        if (comm != null) {
            comm.connectServer(me, kanbans);
        }
    }
}