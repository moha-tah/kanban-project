package client.ihmMain.controllers; 

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import client.MainApp;
import client.ihmKanban.controllers.DisplayKanbanController;
import client.ihmMain.MainCore;
import common.dataClasses.Kanban;
import common.dataClasses.LightUser;
import common.dataClasses.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class ProfileDistantController {

    @FXML
    private Label profileName2;

    @FXML
    private Label profileUsername2;

    @FXML
    private Label kanbansCreated2;

    @FXML
    private ImageView profileAvatar2;


    @FXML
    private GridPane kanbansGrid2;

    @FXML private ScrollPane kanbanArea2; 

    

    public ScrollPane getKanbanArea() {
        return kanbanArea2;
    }  

    private MainCore core;
    private static ProfileDistantController instance;

    private static final String DEFAULT_AVATAR = "/profile_pic.png";
    private static final Logger LOGGER = Logger.getLogger(ProfileDistantController.class.getName());

    public void setCore(MainCore core) {
        this.core = core;
    }

    public static ProfileDistantController getInstance() {
        return instance;
    }

    @FXML
    public void initialize() {
        instance = this;
        LOGGER.info("[UI] ProfileDistantController initialisé (instance enregistrée).");
        core = MainApp.getCore();
        core.setCurrentView("ProfileDistant");
    }

    public void setUser(LightUser currentUser) {
        LOGGER.info(() -> "[UI] Ouverture du profil distant pour : " 
                + currentUser.getUsername()
                + " (ID=" + currentUser.getId() + ")");

        core.getCommPort().requestDistantProfile(core.getMe(), currentUser.getId());
    }


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


    @FXML
    private void handleBackClick() {
        LOGGER.info("Bouton 'Back' cliqué : retour à l'écran d'accueil.");

        try {
            core.showHomeView();
            LOGGER.info("Navigation vers home_fxml.fxml réussie.");
        } catch (Exception e) {
            LOGGER.severe("Erreur lors de la navigation vers home_fxml.fxml : " + e.getMessage());
        }
    }

    public void displayKanban(Kanban kanban) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/displayKanban.fxml"));
            Parent kanbanView = loader.load();

            DisplayKanbanController controller = loader.getController();
            //controller.set(kanban);

            // Remplacer le contenu central
            kanbanArea2.setContent(kanbanView);

            core.getKanbanPort().openKanban(kanban,null, null,this); 

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Impossible de charger displayKanban.fxml", e);
        }
    }

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


    public void updateDistantProfile(User requestedUser) {
        LOGGER.info(() -> "[UI] Mise à jour du profil distant : "
                + requestedUser.getUsername()
                + " (ID=" + requestedUser.getId() + ")");

        try {
            String displayName = requestedUser.getFullName();
            if (displayName == null || displayName.isBlank()) {
                displayName = requestedUser.getUsername();
            }
            java.util.List<common.dataClasses.Kanban> kanbans = requestedUser.getMyKanban();

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

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "[UI] Erreur lors de updateDistantProfile", e);
        }
    }


}
