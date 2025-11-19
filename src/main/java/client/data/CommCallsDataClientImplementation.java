package client.data;

import client.interfaces.ComCallsDataClient;
import common.dataClasses.Kanban;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;
import common.dataClasses.Modification;

import java.util.List;
import java.util.UUID;

public class CommCallsDataClientImplementation implements ComCallsDataClient{
    private DataClientProvider provider;

    public void updateUserList(List<LightUser> users, List<LightKanban> kanbans){
        provider.getMyModel().setMyLightKanbans(kanbans);
        provider.getMyModel().setConnectedUsers(users);
    }

    public List<LightUser> getUsersList() {
        DataClientProvider prov = this.getProvider();
        ClientModel model = prov.getMyModel();
        return model.getConnectedUsers();
    }


    public void send(Kanban kanban){
        //TODO
    }

    @Override
    public void updateLists(LightUser user){
        // Récupérer et publier les listes d'utilisateurs mises à jour
        if (provider != null && provider.getMyModel() != null) {
            List<LightUser> users = provider.getMyModel().getConnectedUsers();
            if (provider.getMainInterface() != null) {
                provider.getMainInterface().publishUsersList(users);
            }
        }
    }

    @Override
    public void uploadKanbans(LightKanban kanban){
        // Publier les listes de kanbans mises à jour
        if (provider != null && provider.getMyModel() != null) {
            List<LightKanban> kanbans = provider.getMyModel().getMyLightKanbans();
            if (provider.getMainInterface() != null) {
                provider.getMainInterface().publishKanbansList(kanbans);
            }
        }
    }

    public void addListModifiers(LightUser user, LightKanban kanban){
        //TODO
    }

    public boolean addAuthorizedUser(UUID kanbanId, UUID userId){
        //TODO
        return true;
    }

    public void addToListKanban(LightKanban kanban){
        //TODO
    }

    public void saveModifiedKanban(Modification modification, LightKanban kanban){
        //TODO
    }

    public void saveTempKanban(Kanban kanban){
        //TODO
    }

    public void addUserToList(LightUser user, List<LightKanban> kanbans){
        //TODO
    }

    //Constructeur
    public CommCallsDataClientImplementation(DataClientProvider provider) {
        
    }
    //getters
    public DataClientProvider getProvider() {
        return this.provider;
    }
    //setters
    public void setProvider(DataClientProvider provider) {
        this.provider = provider;
    }
}
