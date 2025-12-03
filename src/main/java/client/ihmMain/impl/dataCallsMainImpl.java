package client.ihmMain.impl;

import client.ihmMain.MainCore;
import client.ihmMain.controllers.HomeViewController;
import client.interfaces.DataClientCallsMain;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;
import javafx.application.Platform;

import java.util.List;

/** Impl des callbacks de la couche Data vers Main. */
public class dataCallsMainImpl implements DataClientCallsMain {

    private final MainCore core;

    public dataCallsMainImpl(MainCore core) {
        this.core = core;
    }

    @Override
    public void updateListsKanbansUsers(List<LightKanban> kanbansOfUserDisconnected, LightUser userId) {
        core.addKanbans(kanbansOfUserDisconnected);
        core.updateAllKanbansForUser(userId);
        System.out.println("[Main->DataCB] updateListsKanbansUsers for user=" + userId);
    }

    @Override
    public void updateListKanban(LightKanban lightKanban) {
        core.addOrReplaceKanban(lightKanban);
        System.out.println("[Main->DataCB] updateListKanban " + lightKanban.getId());
    }

    @Override
    public void addListModifiers(LightKanban kanbanId, LightUser userId) {
        System.out.println("[Main->DataCB] addListModifiers kanban=" + kanbanId + " user=" + userId);
    }

    @Override
    public void uploadKanbans(LightKanban kanbanId) {
        System.out.println("[Main->DataCB] uploadKanbans " + kanbanId);
    }

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

    @Override
    public void addKanbansList(List<LightKanban> kanbans) {
        core.addKanbans(kanbans);
        System.out.println("[Main->DataCB] addKanbansList size=" + kanbans.size());
    }

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