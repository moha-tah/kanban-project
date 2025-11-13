package client.src.interfaces;

import common.dataClasses.Kanban;
import common.dataClasses.LightUser;
import common.dataClasses.LightKanban;
import common.dataClasses.Modification;

import java.util.List;
import java.util.UUID;


// CommCallsKanban
public interface CommClientCallsKanban {
    void deliverNotification(LightKanban idKanban, Modification modif);
} 