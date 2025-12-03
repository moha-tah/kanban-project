package client.interfaces;

import client.ihmMain.controllers.HomeViewController;
import common.dataClasses.Kanban;

// MainCallsKanban
public interface MainCallsKanban {
    void displaySnapshotList();
    void openCreateForm(Kanban kanban, HomeViewController homeController);

}