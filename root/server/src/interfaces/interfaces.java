import java.util.List;

// CommCallsData
public interface CommCallsDataServer {
    Kanban requestKanban(LightUser user, LightKanban kanban);
    List<Kanban> notifyLogout(UUID userId);
    void askDeleteKanban(LightUser user, LightKanban kanban);
    void askAddListModifiers(LightUser user, LightKanban kanban);
    boolean addAuthorizedUser(UUID kanbanId, UUID userId);
    void addNewUser(LightUser user, List<LightKanban> kanbans);
    List<LightUser> getUsersList();
    List<LightKanban> getKanbansList();
    LightKanban saveKanban(Kanban kanban);
    List<LightUser> saveModifiedKanban(LightKanban kanban, Modification modif);
    Kanban getKanban(LightKanban kanban, LightUser user);
    void closeKanban(LightKanban kaban, LightUser user);
}

public interface KanbanCallsData {
    void saveSnapshot(Kanban kanban);
    List<Snapshot> getListSnapshot();
    Snapshot getSnapshot(Snapshot snap);
    void deleteSnapshot(Snapshot snap);
    void getModified(UUID modificationID, UUID kanbanID);
}