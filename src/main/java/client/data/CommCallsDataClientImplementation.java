package client.data;

import client.interfaces.ComCallsDataClient;
import common.dataClasses.Kanban;
import common.dataClasses.LightKanban;
import common.dataClasses.LightUser;
import common.dataClasses.Modification;

import java.util.ArrayList;
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

    public void updateLists(LightUser user){
        //TODO
    }

    public void uploadKanbans(LightKanban kanban){
        //TODO
    }

    public void addListModifiers(LightUser user, LightKanban kanban){
        //TODO
    }

    public boolean addAuthorizedUser(UUID kanbanId, UUID userId){
        //TODO
        return true;
    }

    public void addToListKanban(LightKanban kanban){
        if (provider == null || provider.getMyModel() == null || kanban == null) {
            return;
        }
        
        ClientModel model = provider.getMyModel();
        List<LightKanban> kanbans = model.getMyLightKanbans();
        
        // Initialiser la liste si elle est null
        if (kanbans == null) {
            kanbans = new ArrayList<>();
            model.setMyLightKanbans(kanbans);
        }
        
        // Retirer le kanban s'il existe déjà (même ID) puis ajouter le nouveau
        kanbans.removeIf(k -> k.getId().equals(kanban.getId()));
        kanbans.add(kanban);
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
        this.provider = provider;
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
