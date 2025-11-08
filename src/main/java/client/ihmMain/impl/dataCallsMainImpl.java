package client.ihmMain.impl;

import client.ihmMain.MainCore;
import client.interfaces.DataClientCallsMain;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;

import java.util.List;
import java.util.UUID;

/** Impl des callbacks de la couche Data vers Main. */
public class dataCallsMainImpl implements DataClientCallsMain {

    private final MainCore core;

    public dataCallsMainImpl(MainCore core) {
        this.core = core;
    }

    @Override
    public void updateListsKanbansUsers(List<LightKanban> kanbansOfUserDisconnected, UUID userId) {
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
    public void addListModifiers(UUID kanbanId, UUID userId) {
        System.out.println("[Main->DataCB] addListModifiers kanban=" + kanbanId + " user=" + userId);
    }

    @Override
    public void uploadKanbans(UUID kanbanId) {
        System.out.println("[Main->DataCB] uploadKanbans " + kanbanId);
    }

    @Override
    public void publishUsersList(List<LightUser> users) {
        core.addUsers(users);
        System.out.println("[Main->DataCB] publishUsersList size=" + users.size());
    }

    @Override
    public void publishKanbansList(List<LightKanban> kanbans) {
        core.addKanbans(kanbans);
        System.out.println("[Main->DataCB] publishKanbansList size=" + kanbans.size());
    }

    @Override
    public void addUserToList(LightUser user, List<LightKanban> kanbans) {
        core.addUser(user);
        core.addKanbans(kanbans);
        System.out.println("[Main->DataCB] addUserToList user=" + user.getUsername());
    }

    @Override
    public void addKanbansList(List<LightKanban> kanbans) {
        core.addKanbans(kanbans);
        System.out.println("[Main->DataCB] addKanbansList size=" + kanbans.size());
    }
}