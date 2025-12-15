package client.ihmMain.controllers; 

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import client.MainApp;
import client.ihmMain.MainCore;
import common.dataClasses.Kanban;
import common.dataClasses.LightUser;
import common.dataClasses.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
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
    private Label kanbansCreated;

    @FXML
    private GridPane kanbansGrid;

    private MainCore core;
    private LightUser currentUser;

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

        this.currentUser = currentUser;

        User me = core.getDataPort().getLocalUser();
        List<Kanban> kanbans = me.getMyKanban();

        profileName.setText(me.getFullName());
        profileUsername.setText("@" + me.getUsername());
        kanbansCreated.setText(String.valueOf(kanbans.size()));

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
            LOGGER.log(Level.SEVERE, "Erreur lors de la navigation vers home.fxml : {0}", e.getMessage());
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

        // Afficher la scène
        Stage stage = (Stage) profileAvatar.getScene().getWindow();
        stage.setScene(new Scene(root, 1280, 720));
        stage.setTitle("Edit Profile");
        stage.show();
    }

    @FXML
    private void handleExportProfile() {
        if (core == null) return;

        // 1. Configurer le sélecteur de fichier
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exporter mon profil");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));

        // Nom par défaut
        if (currentUser != null) {
            fileChooser.setInitialFileName(currentUser.getUsername() + "_backup.json");
        }

        // 2. Ouvrir la fenêtre de dialogue
        Stage stage = (Stage) profileAvatar.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try {
                // 3. Appel à la couche Data
                core.getDataPort().exportProfile(currentUser, file.getAbsolutePath());

                // Feedback
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Export réussi");
                alert.setHeaderText(null);
                alert.setContentText("Votre profil a été exporté vers :\n" + file.getName());
                alert.showAndWait();

            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur Export");
                alert.setContentText("Impossible d'exporter le profil : " + e.getMessage());
                alert.showAndWait();
            }
        }
    }
}
