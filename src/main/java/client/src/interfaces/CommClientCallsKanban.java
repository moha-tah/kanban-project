package client.src.interfaces;

import common.src.dataClasses.Kanban;
import common.src.dataClasses.LightUser;
import common.src.dataClasses.LightKanban;
import common.src.dataClasses.Modification;

import java.util.List;
import java.util.UUID;


// CommCallsKanban
public interface CommClientCallsKanban {
    void deliverNotification(LightKanban idKanban, Modification modif);
}