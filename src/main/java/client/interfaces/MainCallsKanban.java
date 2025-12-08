package client.interfaces;

import java.util.List;
import java.util.UUID;

import client.ihmMain.controllers.HomeViewController;
import common.dataClasses.LightUser;
import common.dataClasses.Kanban;

// MainCallsKanban
public interface MainCallsKanban {
    void displaySnapshotList();
    void openCreateForm(Kanban kanban, HomeViewController homeController);

}