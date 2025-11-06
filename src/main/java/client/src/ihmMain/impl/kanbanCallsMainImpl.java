package client.src.ihmMain.impl;

import client.src.ihmMain.MainCore;
import client.src.interfaces.KanbanCallsMain;
import common.src.dataClasses.LightKanban;
import common.src.dataClasses.LightUser;

import java.util.UUID;

public class kanbanCallsMainImpl implements KanbanCallsMain {

    private final MainCore core;

    public kanbanCallsMainImpl(MainCore core) {
        this.core = core;
    }

    @Override
    public void closingKanbanToServer(LightKanban kanban, LightUser user) {
        System.out.println("[Kanban->Main] closingKanbanToServer kanbanId=" + kanban.getId() + " userId=" + user.getId());
        // core.onCloseKanban(kanbanId, userId);
    }
}