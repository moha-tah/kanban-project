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
 

    @Override
    public void updateUserList(List<LightUser> users, List<LightKanban> kanbans){
        provider.getMyModel().setAvailableLightKanbans(kanbans);
        provider.getMyModel().setConnectedUsers(users);
    }

    public List<LightUser> getUsersList() {
        DataClientProvider prov = this.getProvider();
        ClientModel model = prov.getMyModel();
        return model.getConnectedUsers();
    }
    @Override
    public UUID askIdUser(){
        return this.provider.getMyModel().getLocalUser().getId();
    }


    @Override
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
            List<LightKanban> kanbans = provider.getMyModel().getAvailableLightKanbans();
            if (provider.getMainInterface() != null) {
                provider.getMainInterface().publishKanbansList(kanbans);
            }
        }
    }

    @Override
    public void addListModifiers(LightUser user, LightKanban kanban){
        provider.getMyModel().addKanban(kanban);
    }

    @Override
    public boolean addAuthorizedUser(UUID kanbanId, UUID userId){
        if (provider == null || provider.getCommInterface() == null) {
            return false;
        }
        try {
            provider.getCommInterface().addAuthorizedUser(kanbanId, userId);
            return true;
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(CommCallsDataClientImplementation.class.getName())
                .log(java.util.logging.Level.SEVERE, "Error while adding authorized user (kanbanId={0}, userId={1})", new Object[]{kanbanId, userId});
            return false;
        }
    }
    
    @Override
    public void addToListKanban(LightKanban kanban){
        if (provider == null || provider.getMyModel() == null || kanban == null) {
            return;
        }
        
        ClientModel model = provider.getMyModel();
        List<LightKanban> kanbans = model.getAvailableLightKanbans();
        
        // Initialiser la liste si elle est null
        if (kanbans == null) {
            kanbans = new ArrayList<>();
            model.setAvailableLightKanbans(kanbans);
        }
        
        // Retirer le kanban s'il existe déjà (même ID) puis ajouter le nouveau
        kanbans.removeIf(k -> k.getId().equals(kanban.getId()));
        kanbans.add(kanban);

        // Publier la liste mise à jour à l'interface (comme dans uploadKanbans)
        if (provider.getMainInterface() != null) {
            provider.getMainInterface().publishKanbansList(kanbans);
        }
    }

    @Override
    public void saveModifiedKanban(Modification modification, LightKanban kanban){
        //TODO
    }

    @Override
    public void saveTempKanban(Kanban kanban){
        provider.getMyModel().setCurrentKanban(kanban);
    }

    @Override
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
