package client.interfaces;

import common.dataClasses.LightKanban;
import common.dataClasses.Modification;


// CommCallsKanban
public interface CommClientCallsKanban {
    void deliverNotification(LightKanban idKanban, Modification modif);
}