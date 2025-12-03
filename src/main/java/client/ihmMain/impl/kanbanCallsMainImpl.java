package client.ihmMain.impl;

import client.ihmMain.MainCore;
import client.interfaces.KanbanCallsMain;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;

public class kanbanCallsMainImpl implements KanbanCallsMain {

    private final MainCore core;

    public kanbanCallsMainImpl(MainCore core) {
        this.core = core;
    }

    @Override
    public void closingKanbanToServer(LightKanban kanban, LightUser user) {
        System.out
                .println("[Kanban->Main] closingKanbanToServer kanbanId=" + kanban.getId() + " userId=" + user.getId());
        // core.onCloseKanban(kanbanId, userId);
    }

    @Override
    public void closingKanban(){
        core.onCloseKanban();
    }

    @Override
    public void goHomeView() {
        core.showHomeView();
    }

}