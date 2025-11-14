package client.ihmMain.impl;

import client.ihmMain.MainCore;
import client.interfaces.CommClientCallsMain;
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
    public void displayDecision(LightUser user, LightKanban kanban, boolean decision) {
        System.out.println("[Main->CommCB] decision=" + decision
                + " user=" + user.getUsername()
                + " kanban=" + kanban.getTitle());
    }

    @Override
    public void displayPermissionRequest(LightUser user, LightKanban kanban) {
        System.out.println("[Main->CommCB] permission request from " + user.getUsername()
                + " for kanban " + kanban.getTitle());
    }

    @Override
    public void connectionAccepted(LightUser user, List<LightKanban> kanban) {
        core.setMe(user);
        core.addKanbans(kanban);
        core.showHomeView();
    }

    @Override
    public void connectServer(LightUser user, List<LightKanban> kanbans) {
        if (user == null) {
            System.err.println("[CommCallsMainImpl] connectServer: user is null");
            return;
        }

        // 1) Mettre à jour l’état IHM
        core.setMe(user);
        if (kanbans != null && !kanbans.isEmpty()) {
            core.addKanbans(kanbans);
        }

        // 2) Log pour debug
        System.out.println(
                "[CommCallsMainImpl] connectServer: user=" + user.getUsername()
                        + ", kanbans=" + (kanbans == null ? 0 : kanbans.size())
        );

    }
}