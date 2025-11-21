package server.data;

import java.util.List;
import java.util.UUID;

import common.dataClasses.Kanban;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;
import common.dataClasses.Modification;
import server.interfaces.CommCallsDataServer;

public class ComCallsDataServImplementation implements CommCallsDataServer {
  private DataServProvider myProvider;
  public ComCallsDataServImplementation() {}
  public static ComCallsDataServImplementation newComCallsDataServImplementation() {
        return new ComCallsDataServImplementation();
  }
  @Override
    public Kanban requestKanban(LightUser user, LightKanban kanban) {
        return null; //TODO V2
    }

    @Override
    public List<Kanban> notifyLogout(UUID userId) {
        return null ; //TODO V3
    }

    @Override
    public void askDeleteKanban(LightUser user, LightKanban kanban) {
        
    }

    @Override
    public void askAddListModifiers(LightUser user, LightKanban kanban) {
        
    }


    
    @Override
    public void addNewUser(LightUser user, List<LightKanban> kanbans) {
        
    }

    @Override
    public List<LightUser> getUsersList() {
        return myProvider.getModel().getConnectedUsers();
    }

    @Override
    public List<LightKanban> getKanbansList() {
        return myProvider.getModel().getInUseKanbans();
    }

    @Override
    public LightKanban saveKanban(Kanban kanban) {
        myProvider.getModel().getInUseKanbans().add(kanban);
        return kanban.getLightKanban();
    }

    @Override
    public List<LightUser> saveModifiedKanban(LightKanban kanban, Modification modification) {
        return null; //TODO V3
    }

    @Override
    public Kanban getKanban(LightKanban lightKanban, LightUser user) {
        return null;//TODO V2
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

    @Override
    public boolean addAuthorizedUser(UUID kanbanId, UUID userId) {
    
    return false;
}

}




