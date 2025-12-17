package client.ihmMain.impl;

import client.comm.messages.Message;
import client.data.KanbanCallsDataImplementation;
import client.ihmMain.MainCore;
import client.ihmMain.controllers.HomeViewController;
import client.ihmMain.controllers.ProfileDistantController;
import client.interfaces.CommClientCallsMain;
import common.dataClasses.Kanban;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;
import common.dataClasses.User;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.util.logging.Logger;
import java.util.logging.Level;


import java.util.List;

/** Impl des callbacks de la couche Comm vers Main. */
public class commCallsMainImpl implements CommClientCallsMain {

    private final MainCore core;

    private static final Logger LOGGER = Logger.getLogger(commCallsMainImpl.class.getName());

    public commCallsMainImpl(MainCore core) {
        this.core = core;
    }

    @Override
    public void displayPermissionRequest(LightUser user, LightKanban kanban) {
        javafx.application.Platform.runLater(() -> {
            if (HomeViewController.getInstance() != null) {
                // Appel de la version "Jolie"
                HomeViewController.getInstance().addRequestNotification(user, kanban);
            }
        });
    }

    @Override
    public void receiveMessage(Message message) {
        LOGGER.fine(() -> "Message générique reçu dans Main: " + message);
    }

    @Override
    public void displayDecision(LightUser user, LightKanban kanban, boolean decision) {
        String decisionMaker = (user != null) ? user.getUsername() : "Le propriétaire";

        System.out.println("[Main->CommCB] decision=" + decision
                + " by=" + decisionMaker
                + " kanban=" + kanban.getTitle());

        javafx.application.Platform.runLater(() -> {
            if (HomeViewController.getInstance() != null) {
                String status = decision ? "ACCEPTÉE" : "REFUSÉE";
                Kanban full = KanbanCallsDataImplementation.loadKanbanFromJson(kanban);
                String realTitle = (full != null) ? full.getTitle() : kanban.getTitle();

                String msg = "Votre demande pour '" + realTitle + "' a été " + status;
                HomeViewController.getInstance().addNotification(msg);
                HomeViewController.handleNotif();
                if (decision) {
                    HomeViewController.getInstance().refreshKanbansFromModel();
                }
            }
        });
    }

    @Override
    public void connectionAccepted(LightUser user, List<LightKanban> kanban) {
        core.setMe(user);
        core.addKanbans(kanban);
        core.showHomeView();
    }

    @Override
    public void addUserToList(LightUser user, List<LightKanban> kanbans) {

        if (user == null) {
            System.err.println("[Comm->Main] addUserToList: user is null");
            return;
        }
        core.addUser(user);

        if (kanbans != null && !kanbans.isEmpty()) {
            core.addKanbans(kanbans);
        }

        System.out.println("[Comm->Main] addUserToList: "
                + user.getUsername() + " (" + user.getId() + "), kanbans="
                + (kanbans == null ? 0 : kanbans.size()));
    }

    @Override
    public void displayDistantProfile(User requestedUser) {
        if (requestedUser == null) {
            LOGGER.severe("[Comm->Main] displayDistantProfile: requestedUser est NULL");
            return;
        }
    
        LOGGER.info(() -> "[Comm->Main] Profil distant reçu : "
                + requestedUser.getUsername() + " (ID=" + requestedUser.getId() + ")");
    
        javafx.application.Platform.runLater(() -> {
            try {

                ProfileDistantController controller = ProfileDistantController.getInstance();
                
                if (controller == null) {
                    LOGGER.severe("[Comm->Main] Impossible d'afficher le profil : controller == null");
                    return;
                }
    
                controller.updateDistantProfile(requestedUser);
                LOGGER.info("[Comm->Main] Profil distant envoyé au controller.");
    
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "[Comm->Main] Erreur lors de l’affichage du profil distant", e);
            }
        });
    }

    @Override
    public void handleServerConnectionLost() {
        Platform.runLater(() -> {
            // On ne réagit que si l'utilisateur est actuellement connecté
            if (core.getMe() != null) {
                System.out.println("[CLIENT] Serveur arrêté. Déconnexion forcée.");

                // 1. Nettoyer les données locales
                core.launchApp();

                // 2. Afficher l'alerte
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Déconnexion");
                alert.setHeaderText("Connexion perdue");
                alert.setContentText("Le serveur a été arrêté ou la connexion a été interrompue.");
                alert.show();

                // 3. Retour au Login
                core.showLoginView();
            }
        });
    }
    
}