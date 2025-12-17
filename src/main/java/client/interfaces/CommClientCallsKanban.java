package client.interfaces;

import client.ihmMain.controllers.HomeViewController;
import client.ihmMain.controllers.ProfileController;
import client.ihmMain.controllers.ProfileDistantController;
import common.dataClasses.Kanban;
import common.dataClasses.LightKanban;
import common.dataClasses.Modification;


// CommCallsKanban
public interface CommClientCallsKanban {
    void deliverNotification(LightKanban idKanban, Modification modif);
    void displayKanban(Kanban kanban, HomeViewController homeController, ProfileController profileController, ProfileDistantController profileDistantController);
}