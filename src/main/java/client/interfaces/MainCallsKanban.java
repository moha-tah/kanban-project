package client.interfaces;

import java.util.List;
import java.util.UUID;
import common.dataClasses.LightUser;
import common.dataClasses.LightKanban;

// MainCallsKanban
public interface MainCallsKanban {
    void openCreateFrom(LightKanban lightKanban);
    void displaySnapshotList();

}