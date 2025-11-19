package server.data;

import java.util.List;
import java.util.UUID;

import common.dataClasses.Kanban;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;
import common.dataClasses.Modification;
import common.dataClasses.Access;
import static common.dataClasses.Role;
import server.interfaces.CommCallsDataServer;


public class ComCallsDataServImplementation implements CommCallsDataServer {
  private DataServProvider myProvider;
  public ComCallsDataServImplementation() {}
  public static ComCallsDataServImplementation newComCallsDataServImplementation() {
        return new ComCallsDataServImplementation();
  }
  @Override
    public Kanban requestKanban(LightUser user, LightKanban kanban) {
        ServerModel model = myProvider.getModel();
        List<Kanban> inUseKanbans = model.getInUseKanbans();
        Kanban myKanban = null;
        for (Kanban k : inUseKanbans) {
            LightKanban lightK = k.getLightKanban();
            if (lightK.getId().equals(kanban.getId())) {
                myKanban = k;
                break;
            }
        }
        //doute sur la méthode, peut etre que les classes ont des problèmes d'implémentation (manque d'attributs ?)
        Access accessList = myKanban.getAccessList()
        Boolean hasAccess = false;
        for (Access a : accessList) {
            aUser = a.getUser();
            aRole = a.getRole();
            if (aUser.getId().equals(user.getId()) && (aRole.equals(VIEWER) || aRole.equals(MODIFIER))) {
                hasAccess = true;
                break;
            }
        }
        if (hasAccess) {
            return myKanban;
        }
        else{
            return null;
        }
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
        return myProvider.getModel().getInUseLightKanbans();
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




