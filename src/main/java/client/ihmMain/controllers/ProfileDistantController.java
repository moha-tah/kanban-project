package client.ihmMain.controllers; 

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import client.data.KanbanCallsDataImplementation;
import client.ihmMain.MainCore;
import common.dataClasses.Kanban;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;
import common.dataClasses.User;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
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
            LOGGER.severe(() -> "Erreur lors de la navigation vers home_fxml.fxml : " + e.getMessage());
        }
    }
    private boolean isParticipating(Kanban kanban, LightUser me) {
        if (kanban.getAccessList() == null || me == null) return false;
        return kanban.getAccessList().stream()
                .anyMatch(a -> a.getUser().getId().equals(me.getId()));
    }   

    public void refreshKanbans() {
        if (core == null) return;

        User last = core.getLastRequestedProfile();
        if (last != null) {
            updateDistantProfile(last);
        }
    }




    private Node createKanbanCard(Kanban kanban) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/kanban_card.fxml"));
        Node cardNode = loader.load();

        KanbanCardController controller = loader.getController();
        controller.setMainCore(core);

        LightUser me = core.getMe();

        boolean isMine = kanban.getCreator() != null
             && kanban.getCreator().getId().equals(me.getId());


        boolean isParticipating = isParticipating(kanban, me);

        String color;
        if (isMine) {
            color = "#D8E9FF";       // bleu
        } else if (isParticipating) {
            color = "#EAD8FF";       // violet
        } else {
            color = "#D9FFE3";       // vert
        }

        controller.setKanbanData(kanban, color, isMine, isParticipating);

        core.registerKanbanCardController(kanban.getId(), controller);

        return cardNode;
    }


    public void updateDistantProfile(User requestedUser) {

    Platform.runLater(() -> {

        LOGGER.info(() -> "[UI] Mise à jour du profil distant : "
                + requestedUser.getUsername()
                + " (ID=" + requestedUser.getId() + ")");

        try {
            String displayName = requestedUser.getFullName();
            if (displayName == null || displayName.isBlank()) {
                displayName = requestedUser.getUsername();
            }

            profileName2.setText(displayName);
            profileUsername2.setText("@" + requestedUser.getUsername());

            // Avatar
            Image avatarImg = loadAvatarDistant(requestedUser.getAvatar());
            if (avatarImg != null) {
                profileAvatar2.setImage(avatarImg);
            }

            // 🔥 UTILISER LE MODELE PARTAGÉ (PAS requestedUser.myKanban)
            List<LightKanban> allKanbans = core.getAvailableLightKanbans();
            List<Kanban> kanbans = new ArrayList<>();

            for (LightKanban lk : allKanbans) {
                Kanban full = KanbanCallsDataImplementation.loadKanbanFromJson(lk);
                if (full == null) continue;

                UUID creatorId = full.getCreatorId();

                if (creatorId != null && creatorId.equals(requestedUser.getId())) {
                    kanbans.add(full);
                }

            }

            kanbansCreated2.setText(String.valueOf(kanbans.size()));

            // UI grid
            kanbansGrid2.getChildren().clear();
            int row = 0, col = 0;

            for (Kanban k : kanbans) {
                Node card = createKanbanCard(k);
                kanbansGrid2.add(card, col++, row);
                if (col >= 3) { col = 0; row++; }
            }

            LOGGER.info("[UI] Profil distant affiché avec succès.");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "[UI] Erreur updateDistantProfile", e);
        }
    });
}



}
