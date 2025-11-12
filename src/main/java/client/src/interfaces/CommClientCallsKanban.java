package src.main.java.client.src.interfaces;

import src.main.java.common.src.dataClasses.Kanban;
import src.main.java.common.src.dataClasses.Snapshot;
import java.util.List;
import java.util.UUID;
import src.main.java.common.src.dataClasses.LightUser;
import src.main.java.common.src.dataClasses.LightKanban;
import src.main.java.common.src.dataClasses.Modification;


// CommCallsKanban
public interface CommClientCallsKanban {
    void deliverNotification(LightKanban idKanban, Modification modif);
}
