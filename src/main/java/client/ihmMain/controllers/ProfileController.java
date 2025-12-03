package client.ihmMain.controllers; 

import client.MainApp;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import client.ihmMain.MainCore;
import common.dataClasses.Kanban;
import common.dataClasses.LightUser;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import common.dataClasses.User;


import java.io.File;
import java.util.logging.Logger;

public class ProfileController {

    @FXML
    private Label profileName;

    @FXML
    private Label profileUsername;

    @FXML
    private ImageView profileAvatar;

    @FXML
    private Label collaborations;


    @FXML
    private GridPane kanbansGrid;

    private MainCore core;

    private static final String DEFAULT_AVATAR = "/profile_pic.png";
    private static final Logger LOGGER = Logger.getLogger(ProfileController.class.getName());

    public void setCore(MainCore core) {
        this.core = core;
    }

    @FXML private ScrollPane kanbanArea; 

    public ScrollPane getKanbanArea() {
        return kanbanArea;
    }  

    private Node showKanban(Kanban kanban)
    {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/kanban_card.fxml"));
            Node cardNode = loader.load();

            KanbanCardController controller = loader.getController();
            controller.setMainCore(core);

            String color = "#D8E9FF"; // bleu

            controller.setKanbanData(kanban, color, true, true);
            core.registerKanbanCardController(kanban.getId(), controller);


            return cardNode;

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Impossible de charger kanban_card.fxml", e);
            return null;
        }
    }
    
    public void setUser(LightUser currentUser) {

        User me = core.getDataPort().getLocalUser();
        List<Kanban> kanbans = me.getMyKanban();

        profileName.setText(me.getUsername());
        profileUsername.setText("@" + me.getUsername());

        collaborations.setText("0"); 

        // Avatar
        Image avatarImg = loadAvatarProfile(currentUser.getAvatar());
        if (avatarImg != null) profileAvatar.setImage(avatarImg);

        // Afficher les Kanbans
        kanbansGrid.getChildren().clear();

        int row = 0, col = 0;
        for (Kanban k : kanbans) {
            Node kanbanCard = showKanban(k);
            if (kanbanCard != null) {
                kanbansGrid.add(kanbanCard, col, row);
                col++;
                if (col >= 3) { 
                    col = 0;
                    row++;
                }
            }
        }
        
    }

   private Image loadAvatarProfile(String avPath) {
        // 1) si un chemin fichier valide est fourni depuis le serveur
        if (avPath != null && !avPath.isBlank()) {
            File f = new File(avPath);
            if (f.exists()) {
                return new Image(f.toURI().toString(), true);
            } else {
                LOGGER.log(Level.WARNING, "Avatar file not found: {0}", avPath);
            }
        }

        var url = getClass().getResource(DEFAULT_AVATAR);
        if (url == null) {
            LOGGER.warning("Default avatar resource not found: " + DEFAULT_AVATAR);
            return null; 
        }
        return new Image(url.toExternalForm(), true);
    }


    @FXML
    private void handleBackClick() {
        LOGGER.info("Bouton 'Back' cliqué : retour à l'écran d'accueil.");

        // Sécuriser le core
        if (core == null) {
            core = MainApp.getCore();
        }

        if (core == null) {
            LOGGER.severe("Impossible de revenir à l'accueil : core est null.");
            return;
        }

        try {
            core.showHomeView();
            LOGGER.info("Navigation vers home.fxml réussie.");
        } catch (Exception e) {
            LOGGER.severe("Erreur lors de la navigation vers home.fxml : " + e.getMessage());
        }
    }


    @FXML
    private void handleEditProfileClick() throws IOException {

        // Récupérer le MainCore global
        core = MainApp.getCore();

        // Charger l'interface
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/editProfile.fxml"));
        Parent root = loader.load();

        // Récupérer le controller
        EditProfileController controller = loader.getController();

        // Injecter Core
        controller.setCore(core);

        // Injecter l'utilisateur actuel
        if (core != null && core.getMe() != null) {
            controller.setUser(core.getMe());
        }

        // Afficher la scène
        Stage stage = (Stage) profileAvatar.getScene().getWindow();
        stage.setScene(new Scene(root, 1280, 720));
        stage.setTitle("Edit Profile");
        stage.show();
    }

}
