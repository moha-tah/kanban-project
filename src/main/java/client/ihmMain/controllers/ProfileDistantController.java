package client.ihmMain.controllers; 

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import client.ihmMain.MainCore;
import common.dataClasses.Kanban;
import common.dataClasses.LightUser;
import common.dataClasses.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

/**
 * Contrôleur de la vue de profil d'un utilisateur distant.
 * 
 * Ce contrôleur gère l'affichage du profil d'un autre utilisateur,
 * incluant ses informations (nom, avatar, nombre de kanbans) et
 * la liste de ses kanbans. Les données sont récupérées depuis le serveur.
 * 
 * @author Équipe Kanban
 * @version 1.0
 * @since 1.0
 * @see ProfileController
 * @see MainCore
 */
public class ProfileDistantController {

    /**
     * Label affichant le nom complet de l'utilisateur distant.
     */
    @FXML
    private Label profileName2;

    /**
     * Label affichant le nom d'utilisateur avec @.
     */
    @FXML
    private Label profileUsername2;

    /**
     * Label affichant le nombre de kanbans créés.
     */
    @FXML
    private Label kanbansCreated2;

    /**
     * ImageView affichant l'avatar de l'utilisateur distant.
     */
    @FXML
    private ImageView profileAvatar2;

    /**
     * Grille pour afficher les cartes de kanbans.
     */
    @FXML
    private GridPane kanbansGrid2;

    /**
     * Cœur de l'application principale.
     */
    private MainCore core;
    
    /**
     * Instance unique du contrôleur (singleton).
     */
    private static ProfileDistantController instance;

    /**
     * Chemin de l'avatar par défaut dans les ressources.
     */
    private static final String DEFAULT_AVATAR = "/profile_pic.png";
    
    /**
     * Logger pour les messages de log de cette classe.
     */
    private static final Logger LOGGER = Logger.getLogger(ProfileDistantController.class.getName());

    /**
     * Définit le cœur de l'application principale.
     * 
     * @param core Le cœur de l'application (ne doit pas être null)
     */
    public void setCore(MainCore core) {
        this.core = core;
    }

    /**
     * Récupère l'instance unique du contrôleur.
     * 
     * @return L'instance du contrôleur, ou null si non initialisée
     */
    public static ProfileDistantController getInstance() {
        return instance;
    }

    /**
     * Initialise le contrôleur après le chargement du FXML.
     * 
     * Cette méthode enregistre cette instance comme instance unique (singleton).
     */
    @FXML
    public void initialize() {
        instance = this;
        LOGGER.info("[UI] ProfileDistantController initialisé (instance enregistrée).");
    }

    /**
     * Définit l'utilisateur distant à afficher et demande son profil au serveur.
     * 
     * Cette méthode envoie une requête au serveur pour récupérer le profil
     * complet de l'utilisateur distant. Le profil sera affiché via updateDistantProfile().
     * 
     * @param currentUser L'utilisateur distant à afficher (ne doit pas être null)
     */
    public void setUser(LightUser currentUser) {
        LOGGER.info(() -> "[UI] Ouverture du profil distant pour : " 
                + currentUser.getUsername()
                + " (ID=" + currentUser.getId() + ")");

        core.getCommPort().requestDistantProfile(core.getMe(), currentUser.getId());
    }


    /**
     * Charge l'image de l'avatar depuis un chemin de fichier.
     * 
     * @param avPath Le chemin vers l'image de l'avatar (peut être null ou vide)
     * @return L'image de l'avatar, ou l'avatar par défaut si le chargement échoue
     */
    private Image loadAvatarDistant(String avPath) {
        if (avPath != null && !avPath.isBlank()) {
            File f = new File(avPath);
            if (f.exists()) {
                return new Image(f.toURI().toString(), true);
            }
        }

        var url = getClass().getResource(DEFAULT_AVATAR);
        if (url == null) {
            return null; 
        }
        return new Image(url.toExternalForm(), true);
    }


    /**
     * Gère le clic sur le bouton de retour vers la page d'accueil.
     */
    @FXML
    private void handleBackClick() {
        LOGGER.info("Bouton 'Back' cliqué : retour à l'écran d'accueil.");

        try {
            core.showHomeView();
            LOGGER.info("Navigation vers home_fxml.fxml réussie.");
        } catch (Exception e) {
            LOGGER.severe(() -> "Erreur lors de la navigation vers home_fxml.fxml : " + e.getMessage());
        }
    }

    /**
     * Crée une carte de kanban depuis le fichier FXML.
     * 
     * @param kanban Le kanban pour lequel créer la carte (ne doit pas être null)
     * @return Le nœud représentant la carte, ou null si le chargement échoue
     * @throws IOException si le chargement du FXML échoue
     */
    private Node createKanbanCard(Kanban kanban) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/kanban_card.fxml"));
        Node cardNode = loader.load();

        KanbanCardController controller = loader.getController();
        controller.setMainCore(core);

        String color = "#d8fff3ff"; 
        controller.setKanbanData(kanban, color, true, true);

        core.registerKanbanCardController(kanban.getId(), controller);

        return cardNode;
    }


    /**
     * Met à jour l'affichage du profil distant avec les données reçues du serveur.
     * 
     * Cette méthode est appelée lorsque le serveur répond avec le profil complet
     * de l'utilisateur distant. Elle affiche toutes les informations et les kanbans.
     * 
     * @param requestedUser L'utilisateur distant avec ses données complètes (ne doit pas être null)
     */
    public void updateDistantProfile(User requestedUser) {
        LOGGER.info(() -> "[UI] Mise à jour du profil distant : "
                + requestedUser.getUsername()
                + " (ID=" + requestedUser.getId() + ")");

        try {
            String displayName = requestedUser.getFullName();
            if (displayName == null || displayName.isBlank()) {
                displayName = requestedUser.getUsername();
            }
                final java.util.List<common.dataClasses.Kanban> kanbans = (requestedUser.getMyKanban() != null)
                    ? requestedUser.getMyKanban()
                    : java.util.Collections.emptyList();

            profileName2.setText(displayName);
            profileUsername2.setText("@" + requestedUser.getUsername());
            kanbansCreated2.setText(String.valueOf(kanbans.size()));


            // Avatar
            Image avatarImg = loadAvatarDistant(requestedUser.getAvatar());
            if (avatarImg != null) {
                profileAvatar2.setImage(avatarImg);
                LOGGER.info("[UI] Avatar distant chargé.");
            } else {
                LOGGER.warning("[UI] Avatar distant introuvable, utilisation valeur par défaut.");
            }

            // Kanbans
            kanbansGrid2.getChildren().clear();
            
            LOGGER.info(() -> "[UI] Kanbans distants reçus: " + kanbans.size());
            int row = 0;
            int col = 0;
            for (Kanban k : kanbans) {
                Node card = createKanbanCard(k);
                if (card != null) {
                    kanbansGrid2.add(card, col, row);
                    col++;
                    if (col >= 3) { 
                        col = 0;
                        row++;
                    }
                }
            }
        

            LOGGER.info("[UI] Profil distant affiché avec succès.");

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "[UI] Erreur lors de updateDistantProfile", e);
        }
    }


}
