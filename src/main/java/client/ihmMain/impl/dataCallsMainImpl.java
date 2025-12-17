package client.ihmMain.impl;

import client.ihmMain.MainCore;
import client.ihmMain.controllers.HomeViewController;
import client.interfaces.DataClientCallsMain;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;
import javafx.application.Platform;

import java.util.List;

/**
 * Implémentation des callbacks de la couche Data vers la couche Main.
 * 
 * Cette classe sert d'adaptateur entre la couche Data et l'interface utilisateur Main.
 * Elle reçoit les notifications de la couche Data concernant les mises à jour des
 * listes d'utilisateurs et de kanbans, et les transmet au cœur de l'application
 * pour mise à jour de l'interface utilisateur.
 * 
 * Les opérations d'affichage sont exécutées sur le thread JavaFX pour garantir
 * la sécurité des opérations sur l'interface utilisateur.
 * 
 * @author Équipe Kanban
 * @version 1.0
 * @since 1.0
 * @see DataClientCallsMain
 * @see MainCore
 * @see Platform#runLater(Runnable)
 */
public class dataCallsMainImpl implements DataClientCallsMain {

    /**
     * Cœur de l'application principale.
     */
    private final MainCore core;

    /**
     * Constructeur de l'implémentation.
     * 
     * @param core Le cœur de l'application principale (ne doit pas être null)
     */
    public dataCallsMainImpl(MainCore core) {
        this.core = core;
    }

    /**
     * Met à jour les listes de kanbans et d'utilisateurs après déconnexion d'un utilisateur.
     * 
     * Cette méthode est appelée lorsque la couche Data détecte qu'un utilisateur
     * s'est déconnecté. Elle met à jour les kanbans et les informations utilisateur.
     * 
     * @param kanbansOfUserDisconnected La liste des kanbans de l'utilisateur déconnecté (ne doit pas être null)
     * @param userId L'identifiant de l'utilisateur déconnecté (ne doit pas être null)
     */
    @Override
    public void updateListsKanbansUsers(List<LightKanban> kanbansOfUserDisconnected, LightUser userId) {
        core.addKanbans(kanbansOfUserDisconnected);
        core.updateAllKanbansForUser(userId);
        System.out.println("[Main->DataCB] updateListsKanbansUsers for user=" + userId);
    }

    /**
     * Met à jour un kanban dans la liste.
     * 
     * Cette méthode est appelée lorsque la couche Data met à jour un kanban.
     * Le kanban est ajouté ou remplacé dans la liste du core.
     * 
     * @param lightKanban Le kanban mis à jour (ne doit pas être null)
     */
    @Override
    public void updateListKanban(LightKanban lightKanban) {
        core.addOrReplaceKanban(lightKanban);
        System.out.println("[Main->DataCB] updateListKanban " + lightKanban.getId());
    }

    /**
     * Ajoute un utilisateur à la liste des modificateurs d'un kanban.
     * 
     * Cette méthode est appelée lorsque la couche Data ajoute un utilisateur
     * à la liste des modificateurs autorisés d'un kanban.
     * L'implémentation actuelle se contente de logger l'action.
     * 
     * @param kanbanId Le kanban concerné (ne doit pas être null)
     * @param userId L'utilisateur à ajouter (ne doit pas être null)
     * @implNote Cette méthode devrait mettre à jour l'affichage du kanban.
     */
    @Override
    public void addListModifiers(LightKanban kanbanId, LightUser userId) {
        System.out.println("[Main->DataCB] addListModifiers kanban=" + kanbanId + " user=" + userId);
    }

    /**
     * Notifie qu'un kanban doit être téléversé.
     * 
     * Cette méthode est appelée lorsque la couche Data demande le téléversement
     * d'un kanban vers le serveur. L'implémentation actuelle se contente de logger l'action.
     * 
     * @param kanbanId Le kanban à téléverser (ne doit pas être null)
     * @implNote Cette méthode devrait déclencher le téléversement du kanban.
     */
    @Override
    public void uploadKanbans(LightKanban kanbanId) {
        System.out.println("[Main->DataCB] uploadKanbans " + kanbanId);
    }

    /**
     * Ajoute un utilisateur à la liste des utilisateurs connectés.
     * 
     * Cette méthode est appelée lorsque la couche Data détecte un nouvel utilisateur
     * ou met à jour les informations d'un utilisateur existant. Elle met à jour
     * le core et rafraîchit la barre des utilisateurs dans l'interface.
     * 
     * @param user L'utilisateur à ajouter (ne doit pas être null)
     */
    @Override
    public void addUserToList(LightUser user) {
        core.addUser(user);
        System.out.println("[Main->DataCB] addUserToList user=" + user.getUsername());

        Platform.runLater(() -> {
            HomeViewController ui = HomeViewController.getInstance();
            if (ui != null)
                ui.refreshUsersBar();
        });
    }

    /**
     * Ajoute un utilisateur et ses kanbans à la liste.
     * 
     * Cette méthode est appelée lorsque la couche Data détecte un nouvel utilisateur
     * avec ses kanbans, ou met à jour les informations d'un utilisateur existant.
     * Elle met à jour le core et rafraîchit la barre des utilisateurs dans l'interface.
     * 
     * @param user L'utilisateur à ajouter (ne doit pas être null)
     * @param kanbans La liste des kanbans de l'utilisateur (ne doit pas être null)
     */
    @Override
    public void addUserToList(LightUser user, List<LightKanban> kanbans) {
        core.addUser(user);
        core.addKanbans(kanbans);
        System.out.println("[Main->DataCB] addUserToList (2 params) user=" + user.getUsername());

        Platform.runLater(() -> {
            HomeViewController ui = HomeViewController.getInstance();
            if (ui != null)
                ui.refreshUsersBar();
        });
    }

    /**
     * Ajoute une liste de kanbans au core.
     * 
     * Cette méthode est appelée lorsque la couche Data met à jour plusieurs kanbans
     * à la fois. Les kanbans sont ajoutés au core.
     * 
     * @param kanbans La liste des kanbans à ajouter (ne doit pas être null)
     */
    @Override
    public void addKanbansList(List<LightKanban> kanbans) {
        core.addKanbans(kanbans);
        System.out.println("[Main->DataCB] addKanbansList size=" + kanbans.size());
    }

    /**
     * Publie la liste complète des utilisateurs connectés.
     * 
     * Cette méthode est appelée lorsque la couche Data publie une mise à jour
     * complète de la liste des utilisateurs. Elle remplace la liste existante
     * dans le core et rafraîchit la barre des utilisateurs dans l'interface.
     * 
     * @param users La liste complète des utilisateurs (ne doit pas être null)
     */
    @Override
    public void publishUsersList(List<LightUser> users) {
        if (users == null) {
            System.err.println("[Data->MainCB] publishUsersList: null list, ignored");
            return;
        }

        // MAJ du modèle
        core.replaceUsers(users);
        System.out.println("[Data->MainCB] publishUsersList size=" + users.size());

        // MAJ de l'IHM si home.fxml est chargé
        Platform.runLater(() -> {
            HomeViewController ui = HomeViewController.getInstance();
            if (ui != null) {
                ui.refreshUsersBar();
            } else {
                System.out.println("[Data->MainCB] HomeViewController instance is null, UI not refreshed.");
            }
        });
    }

    /**
     * Publie la liste complète des kanbans disponibles.
     * 
     * Cette méthode est appelée lorsque la couche Data publie une mise à jour
     * complète de la liste des kanbans. Elle remplace la liste existante dans
     * le core et rafraîchit l'affichage des kanbans dans l'interface.
     * 
     * @param kanbans La liste complète des kanbans (ne doit pas être null)
     */
    @Override
    public void publishKanbansList(List<LightKanban> kanbans) {
        if (kanbans == null) {
            System.err.println("[Data->MainCB] publishKanbansList: null list, ignored");
            return;
        }

        core.replaceKanbans(kanbans);
        System.out.println("[Data->MainCB] publishKanbansList size=" + kanbans.size());

        Platform.runLater(() -> {
            HomeViewController ui = HomeViewController.getInstance();
            if (ui != null)
                ui.refreshKanbansFromModel();
        });
    }
}