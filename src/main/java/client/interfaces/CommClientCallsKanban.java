package client.interfaces;

import common.dataClasses.Kanban;
import common.dataClasses.LightKanban;
import common.dataClasses.Modification;


// CommCallsKanban
public interface CommClientCallsKanban {
    void deliverNotification(LightKanban idKanban, Modification modif);
    void displayKanban(Kanban kanban, javafx.scene.control.ScrollPane kanbanArea);
}