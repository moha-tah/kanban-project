package client.interfaces;
import common.dataClasses.Kanban;
import client.ihmMain.controllers.HomeViewController;
import client.ihmMain.controllers.ProfileController;

// DataCallsKanban
public interface DataClientCallsKanban {
    void displayKanban(Kanban kanban, HomeViewController homeController, ProfileController profileController);
}





