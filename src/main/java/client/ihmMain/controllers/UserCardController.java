package client.ihmMain.controllers; 

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import client.MainApp;
import common.dataClasses.LightUser;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/**
 * Contrôleur d'une carte utilisateur.
 * 
 * Ce contrôleur gère l'affichage d'une carte utilisateur avec son nom
 * et son avatar. Il permet également de naviguer vers le profil distant
 * de l'utilisateur en cliquant sur la carte.
 * 
 * @author Équipe Kanban
 * @version 1.0
 * @since 1.0
 * @see ProfileDistantController
 */
public class UserCardController {

    /**
     * Logger pour les messages de log de cette classe.
     */
    private static final Logger LOGGER = Logger.getLogger(UserCardController.class.getName());

    /**
     * Label affichant le nom d'utilisateur.
     */
    @FXML
    private Label nameLabel;

    /**
     * ImageView affichant l'avatar de l'utilisateur.
     */
    @FXML
    private ImageView avatarImageView;

    /**
     * Nom d'utilisateur de la carte.
     */
    private String username;

    /**
     * Chemin de l'avatar par défaut dans les ressources.
     * On réutilise profile_pic.png comme avatar par défaut.
     */
    private static final String DEFAULT_AVATAR = "/profile_pic.png";

    /**
     * Définit les données de l'utilisateur à afficher.
     * 
     * Cette méthode met à jour le label avec le nom d'utilisateur et
     * charge l'avatar depuis le chemin fourni ou utilise l'avatar par défaut.
     * 
     * @param username Le nom d'utilisateur à afficher (ne doit pas être null)
     * @param avatarPath Le chemin vers l'image de l'avatar (peut être null ou vide)
     */
    public void setUserData(String username, String avatarPath) {
        this.username = username;
        nameLabel.setText(username);

        Image avatar = loadAvatar(avatarPath);
        avatarImageView.setImage(avatar);
    }

    /**
     * Charge l'image de l'avatar depuis un chemin de fichier.
     * 
     * Cette méthode tente de charger l'avatar depuis le chemin fourni.
     * Si le fichier n'existe pas ou si le chemin est vide, elle charge
     * l'avatar par défaut depuis les ressources.
     * 
     * @param avatarPath Le chemin vers l'image de l'avatar (peut être null ou vide)
     * @return L'image de l'avatar, ou null si le chargement échoue
     */
    private Image loadAvatar(String avatarPath) {
        // 1) si un chemin fichier valide est fourni depuis le serveur
        try {
            if (avatarPath != null && !avatarPath.isBlank()) {
                File f = new File(avatarPath);
                if (f.exists()) {
                    return new Image(f.toURI().toString(), true);
                } else {
                    LOGGER.warning("Avatar file not found: " + avatarPath);
                }
            }
        } catch (Exception e) {
            LOGGER.warning("Error loading avatar '" + avatarPath + "': " + e.getMessage());
        }

        // 2) sinon, avatar par défaut dans les resources
        try {
            var url = getClass().getResource(DEFAULT_AVATAR);
            if (url == null) {
                LOGGER.warning("Default avatar resource not found: " + DEFAULT_AVATAR);
                return null; // dans ce cas, on garde l’image définie par FXML (@profile_pic.png)
            }
            return new Image(url.toExternalForm(), true);
        } catch (Exception e) {
            LOGGER.warning("Error loading default avatar: " + e.getMessage());
            return null;
        }
    }

    /**
     * Gère le clic sur la carte utilisateur pour afficher son profil distant.
     * 
     * Cette méthode charge la vue de profil distant, récupère l'utilisateur
     * correspondant au nom d'utilisateur, et affiche son profil.
     * 
     * @throws IOException si le chargement du FXML échoue
     */
    @FXML
    private void handleProfileDistantClick() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/profile_distant.fxml"));
        Parent root = loader.load();

        ProfileDistantController controller = loader.getController();
        controller.setCore(MainApp.getCore());

        //on récupère l'utilisateur et on le passe au contrôleur
        LightUser user = MainApp.getCore().searchUserByUsername(username); 
        controller.setUser(user);

        Stage stage = (Stage) avatarImageView.getScene().getWindow();
        stage.setTitle("Profil Distant - " + nameLabel.getText());
        stage.setScene(new Scene(root, 1280, 720));
    }

}