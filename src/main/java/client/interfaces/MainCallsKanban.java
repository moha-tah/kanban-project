package client.interfaces;

import client.ihmMain.controllers.HomeViewController;
import client.ihmMain.controllers.ProfileController;
import common.dataClasses.Kanban;

// MainCallsKanban
public interface MainCallsKanban {
    void displaySnapshotList();
    void openCreateForm(Kanban kanban, HomeViewController homeController);


    void openKanbanViewFromProfile(Kanban kanban, ProfileController profileController);
}