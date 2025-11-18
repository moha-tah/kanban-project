package server.data;

import java.util.List;
import java.util.UUID;

import common.dataClasses.AddAccess;
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
    public void addListModifiers(LightUser user, LightKanban kanban) {
        ServerModel model = myProvider.getModel();
        //Ici on prend en compte les changements de la branche 'feature/getKanban' (à vérifier)
        List<Kanban> kanbans = model.getInUseKanbans();
        for (Kanban k : kanbans) {
            if (k.getId().equals(kanban.getId())) {
                LightKanban lightK = k.getLightKanban();
                AddAccess modifier = new AddAccess(lightK);
                // A voir avec l'équipe si ajout d'un argument
                modifier.execute(user);
                break;
            }
        }  
    }


    @Override
    public boolean addAuthorizedUser(UUID kanbanId, UUID userId) {
        return Boolean.FALSE;
    }

    @Override
    public void addNewUser(LightUser user, List<LightKanban> kanbans) {
        List<LightUser> updatedUsersList = getUsersList();
        List<LightKanban> updatedKanbansList = getKanbansList();

        if (updatedUsersList != null && user != null) {
            updatedUsersList.add(user);
        }

        if (updatedKanbansList != null && kanbans != null && !kanbans.isEmpty()) {
            updatedKanbansList.addAll(kanbans);
        }
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
}




