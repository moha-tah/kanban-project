package client.interfaces;

import common.dataClasses.Kanban;
import common.dataClasses.LightKanban;
import common.dataClasses.Modification;
import client.ihmMain.controllers.HomeViewController;
import client.ihmMain.controllers.ProfileController;


// CommCallsKanban
public interface CommClientCallsKanban {
    void deliverNotification(LightKanban idKanban, Modification modif);
    void displayKanban(Kanban kanban, HomeViewController homeController, ProfileController profileController);
}