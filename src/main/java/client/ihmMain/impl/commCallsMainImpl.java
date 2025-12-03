package client.ihmMain.impl;

import client.data.KanbanCallsDataImplementation;
import client.ihmMain.MainCore;
import client.ihmMain.controllers.HomeViewController;
import client.interfaces.CommClientCallsMain;
import common.dataClasses.Kanban;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;

import java.util.List;

/** Impl des callbacks de la couche Comm vers Main. */
public class commCallsMainImpl implements CommClientCallsMain {

    private final MainCore core;

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
}