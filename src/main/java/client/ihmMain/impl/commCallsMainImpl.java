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

/**
 * Implémentation des callbacks de la couche Communication vers la couche Main.
 * 
 * Cette classe sert d'adaptateur entre la couche Communication et l'interface
 * utilisateur Main. Elle reçoit les notifications du serveur (demandes de permission,
 * décisions, profils distants, perte de connexion) et les transmet au cœur de
 * l'application Main pour mise à jour de l'interface utilisateur.
 * 
 * Les opérations d'affichage sont exécutées sur le thread JavaFX pour garantir
 * la sécurité des opérations sur l'interface utilisateur.
 * 
 * @author Équipe Kanban
 * @version 1.0
 * @since 1.0
 * @see CommClientCallsMain
 * @see MainCore
 * @see Platform#runLater(Runnable)
 */
public class commCallsMainImpl implements CommClientCallsMain {

    /**
     * Cœur de l'application principale.
     */
    private final MainCore core;

    /**
     * Logger pour les messages de log de cette classe.
     */
    private static final Logger LOGGER = Logger.getLogger(commCallsMainImpl.class.getName());

    /**
     * Constructeur de l'implémentation.
     * 
     * @param core Le cœur de l'application principale (ne doit pas être null)
     */
    public commCallsMainImpl(MainCore core) {
        this.core = core;
    }

    /**
     * Affiche une demande de permission d'accès à un kanban privé.
     * 
     * Cette méthode est appelée par la couche Communication lorsqu'un utilisateur
     * demande l'accès à un kanban privé. Elle affiche une notification interactive
     * dans l'interface utilisateur avec des boutons pour accepter ou refuser.
     * 
     * @param user L'utilisateur qui demande l'accès (ne doit pas être null)
     * @param kanban Le kanban concerné par la demande (ne doit pas être null)
     */
    @Override
    public void displayPermissionRequest(LightUser user, LightKanban kanban) {
        javafx.application.Platform.runLater(() -> {
            if (HomeViewController.getInstance() != null) {
                // Appel de la version "Jolie"
                HomeViewController.getInstance().addRequestNotification(user, kanban);
            }
        });
    }

    /**
     * Reçoit un message générique de la couche Communication.
     * 
     * Cette méthode est appelée pour les messages qui ne nécessitent pas
     * de traitement spécifique. L'implémentation actuelle se contente de logger le message.
     * 
     * @param message Le message reçu (ne doit pas être null)
     */
    @Override
    public void receiveMessage(Message message) {
        LOGGER.fine(() -> "Message générique reçu dans Main: " + message);
    }

    /**
     * Affiche la décision prise sur une demande d'accès.
     * 
     * Cette méthode est appelée par la couche Communication lorsque le propriétaire
     * d'un kanban a répondu à une demande d'accès. Elle affiche une notification
     * indiquant si la demande a été acceptée ou refusée, et rafraîchit l'affichage
     * des kanbans si l'accès a été accordé.
     * 
     * @param user L'utilisateur qui a pris la décision (peut être null)
     * @param kanban Le kanban concerné (ne doit pas être null)
     * @param decision true si l'accès a été accordé, false sinon
     */
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

    /**
     * Gère l'acceptation de la connexion par le serveur.
     * 
     * Cette méthode est appelée lorsque le serveur accepte la connexion de l'utilisateur.
     * Elle définit l'utilisateur connecté, ajoute ses kanbans au core, et affiche
     * la page d'accueil.
     * 
     * @param user L'utilisateur connecté (ne doit pas être null)
     * @param kanban La liste des kanbans de l'utilisateur (ne doit pas être null)
     */
    @Override
    public void connectionAccepted(LightUser user, List<LightKanban> kanban) {
        core.setMe(user);
        core.addKanbans(kanban);
        core.showHomeView();
    }

    /**
     * Ajoute un utilisateur et ses kanbans à la liste.
     * 
     * Cette méthode est appelée par la couche Communication lorsqu'un nouvel utilisateur
     * se connecte ou lorsqu'un utilisateur existant met à jour ses kanbans.
     * 
     * @param user L'utilisateur à ajouter (ne doit pas être null)
     * @param kanbans La liste des kanbans de l'utilisateur (peut être null ou vide)
     */
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

    /**
     * Affiche le profil d'un utilisateur distant.
     * 
     * Cette méthode est appelée par la couche Communication lorsque le serveur
     * répond avec le profil complet d'un utilisateur distant. Elle met à jour
     * l'affichage du contrôleur de profil distant.
     * 
     * @param requestedUser L'utilisateur distant avec ses données complètes (ne doit pas être null)
     */
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

    /**
     * Gère la perte de connexion avec le serveur.
     * 
     * Cette méthode est appelée lorsque la connexion avec le serveur est perdue
     * ou interrompue. Elle nettoie l'état local, affiche une alerte à l'utilisateur,
     * et redirige vers la page de connexion.
     */
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