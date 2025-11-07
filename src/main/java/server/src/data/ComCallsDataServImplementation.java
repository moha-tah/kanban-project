package src.main.java.server.src.data;

import java.util.List;
import java.util.UUID;

import common.src.dataClasses.Kanban;
import common.src.dataClasses.LightKanban;
import common.src.dataClasses.LightUser;
import common.src.dataClasses.Modification;
import src.main.java.server.src.interfaces.CommCallsDataServer;

public class ComCallsDataServImplementation implements CommCallsDataServer {
  private DataServProvider myProvider;
  public ComCallsDataServImplementation() {}
  public static ComCallsDataServImplementation newComCallsDataServImplementation() {
        return new ComCallsDataServImplementation();
  }
  @Override
    public Kanban requestKanban(LightUser user, LightKanban kanban) {
        
    }

    @Override
    public List<Kanban> notifyLogout(UUID userId) {
        
    }

    @Override
    public void askDeleteKanban(LightUser user, LightKanban kanban) {
        
    }

    @Override
    public void askAddListModifiers(LightUser user, LightKanban kanban) {
        
    }

    @Override
    public boolean addAuthorizedUser(UUID kanbanId, UUID userId) {
        
    }

    @Override
    public void addNewUser(LightUser user, List<LightKanban> kanbans) {
        
    }

    @Override
    public List<LightUser> getUserList() {
        
    }

    @Override
    public List<LightKanban> getKanbanList() {
        
    }

    @Override
    public LightKanban saveKanban(Kanban kanban) {
        
    }

    @Override
    public List<LightUser> saveModifiedKanban(Kanban kanban, Modification modification) {
        
    }

    @Override
    public Kanban getKanban(LightKanban lightKanban, LightUser user) {
        
    }

    @Override
    public void closeKanban(LightKanban lightKanban, LightUser user) {
        
    }
    
    public void setDataServProvider(DataServProvider provider) {
        this.myProvider = provider;
    }

    public DataServProvider getDataServProvider() {
        return myProvider;
    }
}




