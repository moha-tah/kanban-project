package client.ihmMain.controllers;

import java.io.File;
import java.io.IOException;

import client.MainApp;
import client.ihmMain.MainCore;
import common.dataClasses.LightUser;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

/**
 * Contrôleur du menu de navigation principal.
 * 
 * Ce contrôleur gère le menu latéral de l'application, affichant l'avatar
 * de l'utilisateur connecté et fournissant des actions de navigation :
 * accueil, profil, notifications, et déconnexion.
 * 
 * @author Équipe Kanban
 * @version 1.0
 * @since 1.0
 * @see ProfileController
 * @see HomeViewController
 */
public class MenuController {

    /**
     * ImageView affichant l'avatar de l'utilisateur connecté.
     */
    @FXML private ImageView profilePic;
    
    /**
     * Chemin de l'avatar par défaut dans les ressources.
     */
    private static final String DEFAULT_AVATAR_RESOURCE = "/profile_pic.png";

    /**
     * Initialise le contrôleur après le chargement du FXML.
     * 
     * Cette méthode charge l'avatar de l'utilisateur connecté et l'affiche
     * dans le menu. Si l'utilisateur n'a pas d'avatar, l'avatar par défaut est utilisé.
     */
    @FXML
    private void initialize() {
        MainCore core = MainApp.getCore();
        if (core == null || profilePic == null) return;

        LightUser me = core.getMe();
        if (me == null) return;

        Image avatar = loadAvatar(me.getAvatar());
        profilePic.setImage(avatar);
    }

    /**
     * Charge l'image de l'avatar depuis un chemin de fichier ou une URL.
     * 
     * Cette méthode tente de charger l'avatar depuis le chemin fourni.
     * Elle supporte les chemins de fichiers locaux et les URLs (http/file:).
     * Si le chargement échoue, elle retourne l'avatar par défaut.
     * 
     * @param avatarPath Le chemin vers l'image de l'avatar (peut être null ou vide)
     * @return L'image de l'avatar, ou null si le chargement échoue
     */
    private Image loadAvatar(String avatarPath) {
        if (avatarPath != null && !avatarPath.isBlank()) {
            try {
                if (avatarPath.startsWith("http") || avatarPath.startsWith("file:")) {
                    return new Image(avatarPath, true);
                }
                File f = new File(avatarPath);
                if (f.exists()) return new Image(f.toURI().toString(), true);
            } catch (Exception ignored) {}
        }
        try {
            var url = getClass().getResource(DEFAULT_AVATAR_RESOURCE);
            if (url != null) return new Image(url.toExternalForm(), true);
        } catch (Exception ignored) {}
        return null;
    }

    /**
     * Gère le clic sur l'avatar pour afficher le profil utilisateur.
     * 
     * Cette méthode charge la vue de profil, injecte le MainCore et
     * l'utilisateur actuel, puis affiche la scène.
     * 
     * @throws IOException si le chargement du FXML échoue
     */
    @FXML
    private void handleProfileClick() throws IOException {
        // Charger le FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/profile.fxml"));
        Parent root = loader.load();

        // Récupérer le controller
        ProfileController controller = loader.getController();

        // Injecter MainCore
        controller.setCore(MainApp.getCore());  // récupère le Core global

        // Optionnel : mettre directement l'utilisateur courant
        if (MainApp.getCore() != null && MainApp.getCore().getMe() != null) {
            controller.setUser(MainApp.getCore().getMe());
        }

        // Afficher la scène
        Stage stage = (Stage) profilePic.getScene().getWindow();
        stage.setTitle("Mon Profil");
        stage.setScene(new Scene(root, 1280, 720));
    }


    /**
     * Navigue vers la vue d'accueil.
     * 
     * @throws IOException si la navigation échoue
     */
    @FXML
    private void handleHome() throws IOException {
        if (MainApp.getCore() != null) MainApp.getCore().showHomeView();
    }

    /**
     * Gère l'affichage/masquage du panneau de notifications.
     * 
     * Cette méthode délègue à HomeViewController pour gérer les notifications.
     */
    @FXML
    private void handleNotif() {
        HomeViewController.handleNotif();
    }

    /**
     * Gère la déconnexion de l'utilisateur.
     * 
     * Cette méthode sauvegarde les données locales, envoie une demande de
     * déconnexion au serveur, nettoie l'état local, et redirige vers la page de login.
     */
    @FXML
    private void handleLogout() {
        MainCore core = MainApp.getCore();
        if (core == null) return;

        LightUser me = core.getMe();
        if (me != null) {
            try {
                if (core.getDataClientProvider() != null) {
                    System.out.println("[CLIENT] Sauvegarde locale avant déconnexion...");
                    core.getDataClientProvider().getToMainImpl().saveUser();
                }
            } catch (Exception e) {
                System.err.println("Erreur sauvegarde logout : " + e.getMessage());
            }

            // 2. DIAGRAMME : logout() vers la couche comm
            if (core.getCommPort() != null) {
                System.out.println("[CLIENT] Envoi demande de déconnexion pour " + me.getUsername());
                core.getCommPort().logout(me);
            }
        }

        // 3. Nettoyer l'état local et revenir au Login
        core.launchApp(); // Vide les listes et l'utilisateur courant
        core.showLoginView();
    }

    /**
     * Change de scène en chargeant un nouveau FXML.
     * 
     * @param fxmlPath Le chemin vers le fichier FXML à charger
     * @param title Le titre de la nouvelle fenêtre
     * @param triggerNode Le nœud qui a déclenché le changement de scène
     * @throws IOException si le chargement du FXML échoue
     */
    private void switchScene(String fxmlPath, String title, Node triggerNode) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();
        Stage stage = (Stage) triggerNode.getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(new Scene(root, 1280, 720));
    }
}