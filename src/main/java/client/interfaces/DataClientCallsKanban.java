package client.interfaces;
import client.ihmMain.controllers.HomeViewController;
import client.ihmMain.controllers.ProfileController;
import client.ihmMain.controllers.ProfileDistantController;
import common.dataClasses.Kanban;

// DataCallsKanban
public interface DataClientCallsKanban {
    void displayKanban(Kanban kanban, HomeViewController homeController, ProfileController profileController, ProfileDistantController profileDistantController);
}







