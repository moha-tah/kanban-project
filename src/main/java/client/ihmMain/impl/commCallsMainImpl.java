package client.ihmMain.impl;

import client.ihmMain.MainCore;
import client.interfaces.CommClientCallsMain;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;

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
}